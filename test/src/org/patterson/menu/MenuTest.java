package org.patterson.menu;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.NodeList;
import org.patterson.ConfigurationException;
import org.patterson.WebsiteGenerator;
import org.patterson.resource.GeneratorSettings;

/**
 * Tests the menu functionality.
 * 
 * @author waterman
 * 
 */
public class MenuTest {

	private WebsiteGenerator generator;

	@Before
	public void setup() throws ConfigurationException, IOException {
		Properties tempProps = new Properties();
		tempProps.setProperty(GeneratorSettings.KEY_SRC_DIR, "test" + File.separator + "testMenuSrcDir");
		tempProps.setProperty(GeneratorSettings.KEY_TARGET_DIR, "test" + File.separator + "testMenuTargetDir");
		GeneratorSettings tempSettings = new GeneratorSettings(tempProps);
		generator = new WebsiteGenerator(tempSettings);
	}

	@Test
	public void testLoad() throws Exception {
		MenuManager tempManager = new BasicMenuManager(generator);
		assertEquals(3, tempManager.getMenus().size());
		assertNotNull(tempManager.getMenu("1"));
		assertNotNull(tempManager.getMenu("2"));
		assertNotNull(tempManager.getMenu("3"));
	}

	@Test
	public void testSetAttributes() throws Exception {
		MenuManager tempManager = new BasicMenuManager(generator);
		String tempXpr = "//a[@href='links/Links.html']";
		NodeList tempNodes = tempManager.getMenu("3").on(tempXpr).setAttributes(new Attribute("a1", "v1")).getNodes();
		assertEquals(1, tempNodes.getLength());
		assertNotNull(tempNodes.item(0).getAttributes().getNamedItem("a1"));
		assertEquals("v1", tempNodes.item(0).getAttributes().getNamedItem("a1").getTextContent());
	}
}
