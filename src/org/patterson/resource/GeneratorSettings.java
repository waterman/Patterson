//
// Copyright (c) 2010 Waterman
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in
// all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
// THE SOFTWARE.
//

package org.patterson.resource;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import org.patterson.ConfigurationException;

import com.sun.corba.se.impl.oa.poa.AOMEntry;

/**
 * @author waterman
 * 
 */
public class GeneratorSettings extends AbstractSettings {

	public static final String KEY_TARGET_DIR = "targetDir";
	public static final String KEY_SRC_DIR = "srcDir";

	private String srcDir;
	private String targetDir;
	private File srcDirFile;

	private Properties properties;

	/**
	 * @param aProps
	 * @throws IOException
	 */
	public GeneratorSettings(Properties aProps) throws IOException {
		srcDir = aProps.getProperty(KEY_SRC_DIR, "site");
		targetDir = aProps.getProperty(KEY_TARGET_DIR, "build");
		srcDirFile = srcDir == null ? null : new File(srcDir);
		properties = aProps;
	}

	/**
	 * @return the srcDir
	 */
	public String getSrcDir() {
		return srcDir;
	}

	/**
	 * @return the targetDir
	 */
	public String getTargetDir() {
		return targetDir;
	}

	public void checkDirectories() throws ConfigurationException {
		if (srcDir == null) {
			throw new ConfigurationException("Source directory not specified. Set property 'srcDir' in config file.");
		}
		if (targetDir == null) {
			throw new ConfigurationException("Target directory not specified. Set property 'targetDir' in config file.");
		}
		File tempDir = new File(srcDir);
		if (tempDir.exists()) {
			if (!tempDir.isDirectory()) {
				throw new ConfigurationException("Source dir '" + tempDir.getAbsolutePath() + "' is not a directory.");
			}
		}
		if (!tempDir.canWrite()) {
			throw new ConfigurationException("Source dir '" + tempDir.getAbsolutePath() + "' is not a writable.");
		}
		tempDir = new File(targetDir);
		if (tempDir.exists()) {
			if (!tempDir.isDirectory()) {
				throw new ConfigurationException("Target dir '" + tempDir.getAbsolutePath() + "' is not a directory.");
			}
			if (!tempDir.canWrite()) {
				throw new ConfigurationException("Target dir '" + tempDir.getAbsolutePath() + "' is not a writable.");
			}
		}
	}

	public String getSrcDirAbsolutePath() {
		return srcDirFile.getAbsolutePath();
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void initValues(Map someValues) {
		if (properties == null) {
			return;
		}
		someValues.putAll(properties);
	}
}
