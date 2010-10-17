package org.patterson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import junit.framework.Assert;

import org.junit.Test;

/**
 * Tests {@link GroovyTemplateSupport}.
 * 
 * @author waterman
 * 
 */
public class GroovyTemplateSupportTest extends AbstractWebsiteGeneratorTest {

	public static String TARGET_DIR_NAME = "test" + File.separator + "testGroovyTemplateSupportTargetDir";
	public static String SRC_DIR_NAME = "test" + File.separator + "testGroovyTemplateSupportSrcDir";

	@Test
	public void testTemplateIncluding() throws Exception {
		WebsiteGenerator tempGenerator = getSupport().initWebsiteGenerator(SRC_DIR_NAME, TARGET_DIR_NAME);
		tempGenerator.generate();

		String tempTargetFileName = tempGenerator.calculateTargetFileName("${srcDir}/index.html");
		BufferedReader tempReader = new BufferedReader(new FileReader(tempTargetFileName));
		String tempLine;
		boolean tempTitleFound = false;
		boolean tempNullFound = false;
		while ((tempLine = tempReader.readLine()) != null) {
			if (tempLine.contains("<title>Hello Title</title>")) {
				tempTitleFound = true;
			}
			if (tempLine.contains("null")) {
				tempNullFound = true;
			}
		}
		tempReader.close();
		Assert.assertTrue("Expected result to contain text for title.", tempTitleFound);
		Assert.assertTrue("Result must not contain 'null'.", !tempNullFound);
	}
}
