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

package org.patterson.menu;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class MenuNodeAdapter {
	private interface NodeOperation {
		public void perform(Node aNode);
	}

	private NodeList nodes;
	private MenuManager menuManager;

	public MenuNodeAdapter(NodeList aResult) {
		nodes = aResult;
	}

	protected void setMenuManager(MenuManager aMenuManager) {
		menuManager = aMenuManager;
	}

	private boolean isEmpty() {
		if (exists()) {
			int tempLength = nodes.getLength();
			return tempLength <= 0;
		}
		return false;
	}

	private boolean exists() {
		return nodes != null;
	}

	public void remove() {
		perform(new NodeOperation() {
			@Override
			public void perform(Node aNode) {
				aNode.getParentNode().removeChild(aNode);
			}
		});
	}

	public MenuNodeAdapter setAttributes(final Attribute... someAttributes) {
		perform(new NodeOperation() {
			@Override
			public void perform(Node aNode) {
				for (Attribute tempAttribute : someAttributes) {
					String tempName = tempAttribute.getName();
					String tempValue = tempAttribute.getValue();
					Node tempAttrNode = aNode.getAttributes().getNamedItem(tempName);
					if (tempAttrNode == null) {
						tempAttrNode = aNode.getOwnerDocument().createAttribute(tempName);
						aNode.getAttributes().setNamedItem(tempAttrNode);
					}
					tempAttrNode.setNodeValue(tempValue);
				}
			}
		});
		return this;
	}

	private void perform(NodeOperation aNodeOperation) {
		if (isEmpty()) {
			return;
		}
		for (int i = 0; i < nodes.getLength(); i++) {
			Node tempNode = nodes.item(i);
			aNodeOperation.perform(tempNode);
		}
	}

	public NodeList getNodes() {
		return nodes;
	}
}
