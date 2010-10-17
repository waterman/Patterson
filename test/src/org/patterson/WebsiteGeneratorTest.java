package org.patterson;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import junit.framework.Assert;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.patterson.resource.GeneratorSettings;

/**
 * @author waterman
 * 
 */
public class WebsiteGeneratorTest extends AbstractWebsiteGeneratorTest {

	private static String TARGET_DIR_NAME = "test" + File.separator + "testTargetDir";
	private static String SRC_DIR_NAME = "test" + File.separator + "testSrcDir";

	@Test
	public void testGroovyTemplating() throws Exception {
		Map<String, String> tempProps = new HashMap<String, String>();
		tempProps.put("stuntman", "Colt Seavers");
		WebsiteGenerator tempGenerator = getSupport().initWebsiteGenerator(SRC_DIR_NAME, TARGET_DIR_NAME, tempProps);
		tempGenerator.generate();

		assertTrue(new File(TARGET_DIR_NAME + File.separator + "test.gif").exists());
		assertTrue(new File(TARGET_DIR_NAME + File.separator + "test.html").exists());
		assertTrue(new File(TARGET_DIR_NAME + File.separator + "folder1" + File.separator + "folder1.gif").exists());
		File tempGroovyHtmlFile = new File(TARGET_DIR_NAME + File.separator + "groovytest.html");
		assertTrue(tempGroovyHtmlFile.exists());

		String tempContents = FileUtils.readFileToString(tempGroovyHtmlFile);
		assertTrue(tempContents.contains("Colt Seavers"));
		assertTrue(tempContents.contains("item 1"));
	}

	@Test
	public void testCalculateRelativeUrl() throws Exception {
		WebsiteGenerator tempGenerator = new WebsiteGenerator(new GeneratorSettings(new Properties()) {
			@Override
			public void checkDirectories() throws ConfigurationException {

			}
		});

		File tempSrc = new File("test/me/lala/1.html");
		File tempTarget = new File("test/me/now/2.html");
		// lala/1.html
		// now/2.html
		String tempTargetUrl = tempGenerator.calculateRelativeUrl(tempSrc, tempTarget);
		Assert.assertEquals("../now/2.html", tempTargetUrl);

		tempSrc = new File("test/me/now/here/1/1.html");
		tempTarget = new File("test/me/now/2/2.html");
		// here/1/1.html
		// 2/2.html
		tempTargetUrl = tempGenerator.calculateRelativeUrl(tempSrc, tempTarget);
		Assert.assertEquals("../../2/2.html", tempTargetUrl);

		tempSrc = new File("test/me/now/1.html");
		tempTarget = new File("test/me/2.html");
		// now/1.html
		// 2.html
		tempTargetUrl = tempGenerator.calculateRelativeUrl(tempSrc, tempTarget);
		Assert.assertEquals("../2.html", tempTargetUrl);

		tempSrc = new File("test/me/1.html");
		tempTarget = new File("test/me/now/2.html");
		// 1.html
		// now/2.html
		tempTargetUrl = tempGenerator.calculateRelativeUrl(tempSrc, tempTarget);
		Assert.assertEquals("now/2.html", tempTargetUrl);
	}

}
