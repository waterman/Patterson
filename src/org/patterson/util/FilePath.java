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

package org.patterson.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Path in a File system, consisting of directories (
 * {@link #folders}) and the final {@link #filename}.
 * 
 * @author waterman
 * 
 */
public class FilePath {
	public static FilePath from(String aSrc) {
		FilePath tempPath = new FilePath();
		tempPath.src = aSrc;

		int tempIdx = Math.max(0, aSrc.lastIndexOf(File.separatorChar));
		while (tempIdx >= 0) {
			String tempPart = aSrc.substring(tempIdx + 1, aSrc.length());
			if (tempPath.filename == null) {
				tempPath.filename = tempPart;
			} else {
				tempPath.folders.add(0, tempPart);
			}
			aSrc = aSrc.substring(0, tempIdx);
			tempIdx = aSrc.lastIndexOf(File.separatorChar);
		}
		if (!"".equals(aSrc)) {
			tempPath.folders.add(0, aSrc);
		}
		return tempPath;
	}

	private String src;
	private List<String> folders = new ArrayList<String>();
	private String filename;

	public String getSrc() {
		return src;
	}

	public List<String> getFolders() {
		return folders;
	}

	public String getFilename() {
		return filename;
	}

	public FilePath stripCommonLeadingFolders(FilePath aPath) {
		FilePath tempResult = new FilePath();
		tempResult.filename = filename;

		int tempMax = Math.min(folders.size(), aPath.getFolders().size());
		for (int i = 0; i < tempMax; i++) {
			String tempFolder1 = folders.get(i);
			String tempFolder2 = aPath.folders.get(i);
			if (!tempFolder1.equals(tempFolder2)) {
				tempResult.folders.addAll(folders
						.subList(i, folders.size()));
				return tempResult;
			}
		}
		tempResult.folders.addAll(folders.subList(tempMax, folders.size()));
		return tempResult;
	}

	/**
	 * Returns the receiver as URL, without leading slash.
	 */
	public String toUrlString() {
		StringBuilder tempResult = new StringBuilder();
		for (String tempFolder : getFolders()) {
			tempResult.append(tempFolder);
			tempResult.append("/");
		}
		tempResult.append(filename);
		return tempResult.toString();
	}
	
	@Override
	public String toString() {
		return toUrlString();
	}

}
