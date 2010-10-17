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

package org.patterson.gallery;

import java.io.File;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class FileComparatorCustom implements Comparator<File> {

	private Map<String, Integer> nameToIndexMap = new HashMap<String, Integer>();

	public FileComparatorCustom(String aOrder) {
		if (aOrder != null) {
			StringTokenizer tempTokenizer = new StringTokenizer(aOrder, ",");
			for (int i = 0; tempTokenizer.hasMoreTokens(); i++) {
				String tempToken = tempTokenizer.nextToken().trim();
				nameToIndexMap.put(tempToken.toLowerCase(), i);
			}
		}
	}

	@Override
	public int compare(File aO1, File aO2) {
		Integer temp1 = nameToIndexMap.get(aO1.getName().toLowerCase());
		Integer temp2 = nameToIndexMap.get(aO2.getName().toLowerCase());
		if (temp1 == null && temp2 == null) {
			return aO1.getName().compareTo(aO2.getName());
		}
		if (temp1 == null) {
			return +1;
		}
		if (temp2 == null) {
			return -1;
		}
		return temp1.compareTo(temp2);
	}

}
