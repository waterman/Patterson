package org.patterson.gallery;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.patterson.AbstractWebsiteGeneratorTest;
import org.patterson.WebsiteGenerator;

/**
 * Tests the {@link GenerateGalleryProcessor}.
 * 
 * @author waterman
 * 
 */
public class GenerateGalleryProcessorTest extends AbstractWebsiteGeneratorTest {

	public static String TARGET_DIR_NAME = "test" + File.separator + "testGalleryGenerationTargetDir";
	public static String SRC_DIR_NAME = "test" + File.separator + "testGalleryGenerationSrcDir";

	@SuppressWarnings("unchecked")
	@Test
	public void testGeneration() throws Exception {
		Map tempProps = new HashMap();
		WebsiteGenerator tempGenerator = getSupport().initWebsiteGenerator(SRC_DIR_NAME, TARGET_DIR_NAME, tempProps);
		tempGenerator.generate();
	}

}
