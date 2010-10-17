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

import java.io.IOException;
import java.io.StringReader;
import java.util.Map;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.patterson.GroovyTemplateSupport;
import org.patterson.WebsiteGeneratorException;
import org.patterson.util.XMLUtil;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Menu {

	protected static String TAG_NAME = "menu";
	protected static String ATTR_NAME = "name";

	private Node menuNode;
	private String name;
	private BasicMenuManager menuManager;

	public Menu(Node aItem) {
		menuNode = aItem;
		name = menuNode.getAttributes().getNamedItem(ATTR_NAME).getNodeValue();
	}

	public String getName() {
		return name;
	}

	protected void setMenuManager(BasicMenuManager aMenuManager) {
		menuManager = aMenuManager;
	}

	public MenuNodeAdapter on(String anXPathExpression) throws XPathExpressionException {
		XPath tempXPath = menuManager.createXPath();
		Object tempResult = tempXPath.evaluate(anXPathExpression, menuNode.getChildNodes(), XPathConstants.NODESET);
		MenuNodeAdapter tempAdapter = new MenuNodeAdapter((NodeList) tempResult);
		tempAdapter.setMenuManager(menuManager);
		return tempAdapter;
	}

	public String asText(Map<?, ?> aBinding) throws IOException, WebsiteGeneratorException {
		GroovyTemplateSupport tempTemplateSupport = menuManager.getGenerator().getProcessor().getTemplateSupport();
		String tempXml = getXmlText();
		StringReader tempReader = new StringReader(tempXml);
		String tempTxt = tempTemplateSupport.evalTemplate(aBinding, tempReader);
		return tempTxt;
	}

	private String getXmlText() {
		String tempTxt = XMLUtil.asText(menuNode.getChildNodes());
		return tempTxt;
	}
}
