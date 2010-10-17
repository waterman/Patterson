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
 * Represents a resource taking part in the generation process.
 * 
 * @author waterman
 * 
 */
public interface Resource {

	/**
	 * Returns the path in the src dir
	 */
	public String getAbsoluteSourcePath();

	/**
	 * Returns the path in the target dir
	 */
	public String getAbsoluteTargetPath();

	/**
	 * Returns the relative path to the given resource, i.e. for usage in the
	 * "href" attribute.
	 */
	public String getRelativeTargetPathTo(Resource aResource);

	public void setSourceFile(File aFile);

	public void setWebsiteGenerator(WebsiteGenerator aGenerator);

	public String getAbsoluteTargetDirectory();
	
	public String getAbsoluteSourceDirectory();
	
	public Settings getSettings();
	
	public void setSettings(Settings someSettings);
	
	public String getAbsoluteSourceFilenameNoExtension();
}
