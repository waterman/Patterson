package org.patterson.gallery;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import junit.framework.Assert;

import org.junit.Test;
import org.patterson.ConfigurationException;
import org.patterson.WebsiteGenerator;
import org.patterson.resource.GeneratorSettings;

/**
 * Test for {@link Gallery}.
 * 
 * @author waterman
 * 
 */
public class GalleryTest {

	@Test
	public void testGetIndexPageCount() throws Exception {
		Gallery tempGallery = newGallery(0);
		Assert.assertEquals(0, tempGallery.getIndexPageCount());

		tempGallery = newGallery(2);
		Assert.assertEquals(1, tempGallery.getIndexPageCount());

		tempGallery = newGallery(12);
		Assert.assertEquals(1, tempGallery.getIndexPageCount());

		tempGallery = newGallery(13);
		Assert.assertEquals(2, tempGallery.getIndexPageCount());
	}

	@Test
	public void testGetNavigationImages() throws Exception {
		Gallery tempGallery = newGallery(0);
		List<HtmlImageResource> tempImages = tempGallery.getNavigationHtmlImagesResourceForImage(0);
		assertEquals(5, tempImages.size());
		Assert.assertNull(tempImages.get(0));
		Assert.assertNull(tempImages.get(1));
		Assert.assertNull(tempImages.get(2));
		Assert.assertNull(tempImages.get(3));
		Assert.assertNull(tempImages.get(4));

		tempGallery = newGallery(1);
		tempImages = tempGallery.getNavigationHtmlImagesResourceForImage(0);
		assertEquals(5, tempImages.size());
		Assert.assertNull(tempImages.get(0));
		Assert.assertNull(tempImages.get(1));
		Assert.assertNotNull(tempImages.get(2));
		Assert.assertNull(tempImages.get(3));
		Assert.assertNull(tempImages.get(4));

		tempGallery = newGallery(2);
		tempImages = tempGallery.getNavigationHtmlImagesResourceForImage(0);
		assertEquals(5, tempImages.size());
		Assert.assertNull(tempImages.get(0));
		Assert.assertNull(tempImages.get(1));
		Assert.assertNotNull(tempImages.get(2));
		Assert.assertNotNull(tempImages.get(3));
		Assert.assertNull(tempImages.get(4));

		tempGallery = newGallery(2);
		tempImages = tempGallery.getNavigationHtmlImagesResourceForImage(1);
		assertEquals(5, tempImages.size());
		Assert.assertNull(tempImages.get(0));
		Assert.assertNotNull(tempImages.get(1));
		Assert.assertNotNull(tempImages.get(2));
		Assert.assertNull(tempImages.get(3));
		Assert.assertNull(tempImages.get(4));
	}

	@Test
	public void testGetImagesForIndexPage() throws Exception {
		Gallery tempGallery = newGallery(1);
		List<HtmlImageResource> tempImages = tempGallery.getHtmlImagesForIndexPage(0);
		assertEquals(1, tempImages.size());

		tempGallery = newGallery(13);
		tempImages = tempGallery.getHtmlImagesForIndexPage(0);
		assertEquals(12, tempImages.size());
		tempImages = tempGallery.getHtmlImagesForIndexPage(1);
		assertEquals(1, tempImages.size());
	}

	private Gallery newGallery(int anImageCount) throws IOException, ConfigurationException {
		Properties tempProps = new Properties();
		tempProps.put(GeneratorSettings.KEY_SRC_DIR, GenerateGalleryProcessorTest.SRC_DIR_NAME);
		tempProps.put(GeneratorSettings.KEY_TARGET_DIR, GenerateGalleryProcessorTest.TARGET_DIR_NAME);
		WebsiteGenerator tempGenerator = new WebsiteGenerator(tempProps);
		File tempFiles[] = newFileArray(anImageCount);
		return new Gallery(tempFiles, tempGenerator);
	}

	private File[] newFileArray(int aSize) {
		File[] tempResult = new File[aSize];
		for (int i = 0; i < aSize; i++) {
			tempResult[i] = new File("" + i + ".jpg");
		}
		return tempResult;
	}

}
