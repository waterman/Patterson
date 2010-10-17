package org.patterson;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;
import org.patterson.resource.GeneratorSettings;

/**
 * Provides supporting functionality for testing the {@link WebsiteGenerator}
 * project.
 * 
 * @author waterman
 * 
 */
public class TestSupport {

	public WebsiteGenerator initWebsiteGenerator(String aSrcDirName, String aTargetDirName) throws IOException,
			ConfigurationException {
		return initWebsiteGenerator(aSrcDirName, aTargetDirName, new HashMap<String, String>());
	}

	public WebsiteGenerator initWebsiteGenerator(String aSrcDirName, String aTargetDirName,
			Map<String, String> someProperties) throws IOException, ConfigurationException {
		File tempSrcDir = new File(aSrcDirName);
		File tempTargetDir = new File(aTargetDirName);
		assertTrue(tempSrcDir.exists());
		if (tempTargetDir.exists()) {
			FileUtils.deleteDirectory(tempTargetDir);
		}

		Properties tempProps = new Properties();
		tempProps.put(GeneratorSettings.KEY_SRC_DIR, tempSrcDir.getAbsolutePath());
		tempProps.put(GeneratorSettings.KEY_TARGET_DIR, tempTargetDir.getAbsolutePath());
		for (Entry<String, String> tempEntry : someProperties.entrySet()) {
			tempProps.setProperty(tempEntry.getKey(), tempEntry.getValue());
		}
		WebsiteGenerator tempGenerator = new WebsiteGenerator(tempProps);
		return tempGenerator;

	}

}
