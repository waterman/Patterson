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

import java.util.ArrayList;
import java.util.List;

import org.patterson.resource.BasicResource;

/**
 * Represent a Image HTML page resource.
 * 
 * @author waterman
 * 
 */
public class HtmlImageResource extends BasicResource{

	private ImageResource imageResource;
	private List<HtmlImageResource> previewImageResources = new ArrayList<HtmlImageResource>();
	
	private HtmlImageResource previous;
	private HtmlImageResource next;
	
	/** The index file this image is contained in */
	private HtmlIndexResource index;

	public HtmlImageResource getPrevious() {
		return previous;
	}

	public void setPrevious(HtmlImageResource aPrevious) {
		previous = aPrevious;
	}

	public HtmlImageResource getNext() {
		return next;
	}

	public void setNext(HtmlImageResource aNext) {
		next = aNext;
	}

	public boolean isFirst() {
		return previous == null;
	}

	public boolean isLast() {
		return next == null;
	}

	public ImageResource getImageResource() {
		return imageResource;
	}

	public void setImageResource(ImageResource aImageResource) {
		imageResource = aImageResource;
	}

	public List<HtmlImageResource> getPreviewImageResources() {
		return previewImageResources;
	}

	public HtmlIndexResource getIndex() {
		return index;
	}

	public void setIndex(HtmlIndexResource aIndex) {
		index = aIndex;
	}

}
