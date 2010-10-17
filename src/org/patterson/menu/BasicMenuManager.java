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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.patterson.WebsiteGenerator;
import org.patterson.WebsiteGeneratorRuntimeException;
import org.patterson.resource.GeneratorSettings;

public class BasicMenuManager implements MenuManager {

	private static final String CFG_KEY_MENU_FILE = "menu.file";
	private static final String CFG_VALUE_DEFAULT_CONFIG_FILE = "${" + GeneratorSettings.KEY_SRC_DIR + "}"
			+ File.separator + "menu" + File.separator + "menu.xml";

	private Logger logger = Logger.getLogger(BasicMenuManager.class.getSimpleName());
	private WebsiteGenerator generator;
	private Map<String, Menu> menus;

	private XPathFactory xPathFactory;

	public BasicMenuManager(WebsiteGenerator aGenerator) {
		generator = aGenerator;
		xPathFactory = XPathFactory.newInstance();
	}

	/* (non-Javadoc)
	 * @see org.patterson.menu.IMenuManager#getMenus()
	 */
	public Map<String, Menu> getMenus() {
		if (menus == null) {
			menus = new HashMap<String, Menu>();
			List<Menu> tempLoadMenus = loadMenus();
			for (Menu tempMenu : tempLoadMenus) {
				menus.put(tempMenu.getName(), tempMenu);
				tempMenu.setMenuManager(this);
			}
		}
		return menus;
	}

	private List<Menu> loadMenus() {
		String tempFilename = generator.getSettings().getStringAt(CFG_KEY_MENU_FILE, CFG_VALUE_DEFAULT_CONFIG_FILE);
		tempFilename = generator.expandVariablesInFilename(tempFilename);
		File tempMenuFile = new File(tempFilename);
		if (!tempMenuFile.exists()) {
			logger.warning("No menu definitions found at: '" + tempMenuFile.getAbsolutePath() + "'.");
			return new ArrayList<Menu>();
		}
		MenuConfigParser tempParser;
		InputStream tempIn = null;
		List<Menu> tempMenus;
		try {
			tempIn = new FileInputStream(tempMenuFile);
			tempParser = new MenuConfigParser(tempIn);
			tempMenus = tempParser.getMenues();
		} catch (FileNotFoundException e) {
			throw new WebsiteGeneratorRuntimeException("Error opening menu config: " + tempFilename, e);
		} finally {
			if (tempIn != null) {
				try {
					tempIn.close();
				} catch (IOException e) {
					logger.log(Level.SEVERE, "Error parsing mneu: " + tempFilename, e);
				}
			}
		}
		return tempMenus;
	}

	/* (non-Javadoc)
	 * @see org.patterson.menu.IMenuManager#getMenu(java.lang.String)
	 */
	public Menu getMenu(String aName) {
		return getMenus().get(aName);
	}

	protected XPath createXPath() {
		XPath tempNewXPath = xPathFactory.newXPath();
		return tempNewXPath;
	}
	
	public WebsiteGenerator getGenerator() {
		return generator;
	}
}
