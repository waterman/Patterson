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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Settings based on a Properties file.
 * 
 * @author waterman
 * 
 */
@SuppressWarnings("unchecked")
public class FileSourceSettings extends AbstractSettings {

	private Logger logger = Logger.getLogger(FileSourceSettings.class.getName());
	private File source;

	public FileSourceSettings(File aSource) {
		source = aSource;
	}

	@Override
	protected void initValues(Map someValues) {
		if (source == null || !source.exists()) {
			return;
		}
		Properties tempProps = new Properties();
		FileInputStream tempIn = null;
		try {
			tempIn = new FileInputStream(source);
			tempProps.load(tempIn);
		} catch (FileNotFoundException e) {
			logger.log(Level.WARNING, "File not existing: " + source.getAbsolutePath(), e);
		} catch (IOException e) {
			logger.log(Level.WARNING, "Error while reading: " + source.getAbsolutePath(), e);
		} finally {
			if (tempIn != null) {
				try {
					tempIn.close();
				} catch (IOException e) {
					logger.log(Level.WARNING, "Error closing: " + source.getAbsolutePath(), e);
				}
			}
		}
		someValues.putAll(tempProps);
	}
}
