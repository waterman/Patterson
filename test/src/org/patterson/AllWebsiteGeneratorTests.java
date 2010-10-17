package org.patterson;

import junit.framework.JUnit4TestAdapter;
import junit.framework.Test;
import junit.framework.TestSuite;

import org.patterson.gallery.GalleryTest;
import org.patterson.gallery.GenerateGalleryProcessorTest;
import org.patterson.util.FilePathTest;

/**
 * All tests of the WebsiteGenerator project
 * 
 * @author waterman
 * 
 */
public class AllWebsiteGeneratorTests {

	public static Test suite() {
		TestSuite tempSuite = new TestSuite("All WebsiteGenerator Tests");
		tempSuite.addTest(new JUnit4TestAdapter(GalleryTest.class));
		tempSuite.addTest(new JUnit4TestAdapter(GenerateGalleryProcessorTest.class));
		tempSuite.addTest(new JUnit4TestAdapter(FilePathTest.class));
		tempSuite.addTest(new JUnit4TestAdapter(GroovyTemplateSupportTest.class));
		tempSuite.addTest(new JUnit4TestAdapter(WebsiteGeneratorTest.class));
		return tempSuite;
	}
}
