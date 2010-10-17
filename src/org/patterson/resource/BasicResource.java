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

import org.patterson.WebsiteGenerator;

/**
 * @author waterman
 * 
 */
public class BasicResource implements Resource {
	private File sourceFile;
	private WebsiteGenerator generator;
	private Settings settings;

	public BasicResource() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.patterson.Resource#getAbsoluteSourcePath()
	 */
	@Override
	public String getAbsoluteSourcePath() {
		String tempPath = sourceFile.getAbsolutePath();
		return tempPath;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.patterson.Resource#getAbsoluteTargetPath()
	 */
	@Override
	public String getAbsoluteTargetPath() {
		String tempPath = generator.calculateTargetFileName(sourceFile);
		return tempPath;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.patterson.Resource#getRelativeTargetPathTo(org.patterson
	 * .Resource)
	 */
	@Override
	public String getRelativeTargetPathTo(Resource aResource) {
		String tempSrc = getAbsoluteTargetPath();
		String tempTgt = aResource.getAbsoluteTargetPath();
		String tempPath = generator.calculateRelativeUrl(new File(tempSrc), new File(tempTgt));
		return tempPath;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.patterson.Resource#setSourceFile(java.io.File)
	 */
	@Override
	public void setSourceFile(File aFile) {
		sourceFile = aFile;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.patterson.Resource#setWebsiteGenerator(org.patterson.
	 * WebsiteGenerator)
	 */
	@Override
	public void setWebsiteGenerator(WebsiteGenerator aGenerator) {
		generator = aGenerator;
	}

	public boolean exists() {
		return sourceFile.exists();
	}

	public String getFilenameNoExtension() {
		String tempName = getAbsoluteSourceFilenameNoExtension();
		int tempIdx = tempName.lastIndexOf(File.separatorChar);
		if (tempIdx >= 0) {
			tempName = tempName.substring(tempIdx + 1);
		}
		return tempName;
	}

	public String getAbsoluteSourceFilenameNoExtension() {
		String tempName = sourceFile.getAbsolutePath();
		int tempIdx = tempName.lastIndexOf('.');
		if (tempIdx >= 0) {
			tempName = tempName.substring(0, tempIdx);
		}
		return tempName;
	}

	public String getAbsoluteTargetDirectory() {
		String tempPath = getAbsoluteTargetPath();
		return extractDirectory(tempPath);
	}

	private String extractDirectory(String tempPath) {
		int tempIdx = tempPath.lastIndexOf(File.separatorChar);
		if (tempIdx >= 0) {
			tempPath = tempPath.substring(0, tempIdx);
		}
		return tempPath;
	}

	@Override
	public String getAbsoluteSourceDirectory() {
		String tempPath = getAbsoluteSourcePath();
		return extractDirectory(tempPath);
	}

	@Override
	public Settings getSettings() {
		return settings;
	}

	@Override
	public void setSettings(Settings aSettings) {
		settings = aSettings;
	}
}
