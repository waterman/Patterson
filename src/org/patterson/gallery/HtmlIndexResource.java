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
 * Represent a Index HTML page resource.
 * 
 * @author waterman
 * 
 */
public class HtmlIndexResource extends BasicResource {

	private List<HtmlImageResource> previewImageResources = new ArrayList<HtmlImageResource>();

	private HtmlIndexResource previous;
	private HtmlIndexResource next;

	public boolean isFirst() {
		return previous == null;
	}

	public boolean isLast() {
		return next == null;
	}

	public HtmlIndexResource getPrevious() {
		return previous;
	}

	public void setPrevious(HtmlIndexResource aPrevious) {
		previous = aPrevious;
	}

	public HtmlIndexResource getNext() {
		return next;
	}

	public void setNext(HtmlIndexResource aNext) {
		next = aNext;
	}

	public List<HtmlImageResource> getPreviewImageResources() {
		return previewImageResources;
	}

	public HtmlImageResource getPreviewImageResource(int aIdx) {
		if (aIdx >= previewImageResources.size()) {
			return null;
		}
		return previewImageResources.get(aIdx);
	}

	public void addPreviewImageResources(List<HtmlImageResource> someImages) {
		for (HtmlImageResource tempHtmlImageResource : someImages) {
			previewImageResources.add(tempHtmlImageResource);
			tempHtmlImageResource.setIndex(this);
		}
	}

}
