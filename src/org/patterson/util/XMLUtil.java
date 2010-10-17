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

import java.io.StringWriter;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.patterson.WebsiteGeneratorRuntimeException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XMLUtil {

	public static String asText(NodeList someNodes) {
		StringBuilder tempBuilder = new StringBuilder();
		for (int i = 0; i < someNodes.getLength(); i++) {
			Node tempItem = someNodes.item(i);
			String tempText = asText(tempItem);
			tempBuilder.append(tempText);
		}
		return tempBuilder.toString();
	}

	public static String asText(Node aNode) {
		try {
			Transformer tempTransformer = TransformerFactory.newInstance().newTransformer();
			tempTransformer.setOutputProperty(OutputKeys.INDENT, "yes");

			StreamResult tempResult = new StreamResult(new StringWriter());
			DOMSource tempSource = new DOMSource(aNode);
			tempTransformer.transform(tempSource, tempResult);

			String tempString = tempResult.getWriter().toString();
			return tempString;
		} catch (Exception e) {
			throw new WebsiteGeneratorRuntimeException(e);
		}
	}
}
