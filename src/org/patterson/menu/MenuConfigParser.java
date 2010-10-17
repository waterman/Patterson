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
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.patterson.WebsiteGeneratorRuntimeException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class MenuConfigParser {

	private InputStream in;

	public MenuConfigParser(InputStream aInputStream) {
		in = aInputStream;
	}

	public List<Menu> getMenues() {
		Document tempDocument;
		try {
			tempDocument = createDocument();
		} catch (Exception e) {
			throw new WebsiteGeneratorRuntimeException("Error parsing menu definition.", e);
		}
		List<Menu> tempMenus = parse(tempDocument);
		return tempMenus;
	}

	private List<Menu> parse(Document aDocument) {
		Element tempRoot = aDocument.getDocumentElement();
		NodeList tempMenus = tempRoot.getElementsByTagName(Menu.TAG_NAME);
		List<Menu> tempResult = new ArrayList<Menu>();
		for (int i = 0; i < tempMenus.getLength(); i++) {
			Node tempItem = tempMenus.item(i);
			Menu tempMenu = new Menu(tempItem);
			tempResult.add(tempMenu);
		}
		return tempResult;
	}

	private Document createDocument() throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory tempFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder tempBuilder = tempFactory.newDocumentBuilder();
		Document tempDocument = tempBuilder.parse(in);
		in.close();
		return tempDocument;
	}

}
