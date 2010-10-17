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

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;

import org.patterson.WebsiteGenerator;
import org.patterson.resource.Resource;

/**
 * Represents a Gallery of Images (Files)
 * 
 * @author waterman
 * 
 */
public class Gallery {

	private static final String CFG_KEY_GALLERY_SORT_STYLE = "gallery.sort.style";
	private static final String CFG_VALUE_GALLERY_SORT_STYLE_BY_NAME = "byName";
	private static final String CFG_VALUE_GALLERY_SORT_STYLE_BY_DATE = "byDate";
	private static final String CFG_VALUE_GALLERY_SORT_STYLE_CUSTOM = "custom";
	private static final String CFG_KEY_GALLERY_SORT_STYLE_CUSTOM_FILE_SEQUENCE = "gallery.sort.file.sequence";

	private int indexPageRowCount = 3;
	private int indexPageColumnCount = 4;
	/**
	 * The number of thumbnail images on one image page to display for
	 * navigation purposes
	 */
	private int navigationImagesPerPageCount = 5;

	private List<File> rawImages;
	private WebsiteGenerator generator;

	private List<HtmlIndexResource> htmlIndexResources;
	private List<HtmlImageResource> htmlImageResources;

	public Gallery() {
		super();
	}

	public Gallery(File[] someImages, WebsiteGenerator aGenerator) {
		rawImages = new ArrayList<File>(Arrays.asList(someImages));
		generator = aGenerator;
		init();
	}

	public int getIndexPageCount() {
		if (htmlImageResources.isEmpty()) {
			return 0;
		}
		double tempImageCount = htmlImageResources.size();
		int tempImagesPerPage = getImagesPerIndexPageCount();
		double tempResult = tempImageCount / tempImagesPerPage;
		return Math.max(1, (int) Math.ceil(tempResult));
	}

	private int getImagesPerIndexPageCount() {
		return Math.max(1, indexPageRowCount * indexPageColumnCount);
	}

	public int getIndexPageRowCount() {
		return indexPageRowCount;
	}

	public void setIndexPageRowCount(int aIndexPageRowCount) {
		indexPageRowCount = aIndexPageRowCount;
	}

	public int getIndexPageColumnCount() {
		return indexPageColumnCount;
	}

	public void setIndexPageColumnCount(int aIndexPageColumnCount) {
		indexPageColumnCount = aIndexPageColumnCount;
	}

	public int getImageCount() {
		return htmlImageResources.size();
	}

	public List<HtmlImageResource> getHtmlImageResources() {
		if (htmlImageResources == null) {
			init();
		}
		return htmlImageResources;
	}

	private void init() {
		sortImages();
		htmlImageResources = new ArrayList<HtmlImageResource>();
		for (ListIterator<File> tempFileIter = rawImages.listIterator(); tempFileIter.hasNext();) {
			File tempFile = (File) tempFileIter.next();

			ImageResource tempImage = new ImageResource();
			generator.initResourceFor(tempImage, tempFile);

			ImageResource tempThumbnail = new ImageResource();
			generator.initResourceFor(tempThumbnail, getThumbnailFile(tempImage));
			tempImage.setThumbnail(tempThumbnail);

			HtmlImageResource tempHtmlImage = new HtmlImageResource();
			String tempName = tempImage.getAbsoluteSourceFilenameNoExtension() + ".html";
			generator.initResourceFor(tempHtmlImage, new File(tempName));
			tempHtmlImage.setImageResource(tempImage);
			htmlImageResources.add(tempHtmlImage);
		}

		for (int i = 0; i < htmlImageResources.size(); i++) {
			HtmlImageResource tempHtmlImage = htmlImageResources.get(i);
			List<HtmlImageResource> tempImages = getNavigationHtmlImagesResourceForImage(i);
			tempHtmlImage.getPreviewImageResources().addAll(tempImages);
			if (i > 0) {
				tempHtmlImage.setPrevious(htmlImageResources.get(i - 1));
			}
			if (i < htmlImageResources.size() - 1) {
				tempHtmlImage.setNext(htmlImageResources.get(i + 1));
			}
		}

		htmlIndexResources = new ArrayList<HtmlIndexResource>();
		for (int i = 0; i < getIndexPageCount(); i++) {
			HtmlIndexResource tempIndexResource = new HtmlIndexResource();
			String tempIdxString = i == 0 ? "" : "" + i;
			String tempFilename = "index" + tempIdxString + ".html";
			tempFilename = htmlImageResources.get(0).getAbsoluteSourceDirectory() + File.separator + tempFilename;
			generator.initResourceFor(tempIndexResource, new File(tempFilename));

			List<HtmlImageResource> tempImagesForIndexPage = getHtmlImagesForIndexPage(i);
			tempIndexResource.addPreviewImageResources(tempImagesForIndexPage);
			htmlIndexResources.add(tempIndexResource);
		}

		for (int i = 0; i < htmlIndexResources.size(); i++) {
			if (i > 0) {
				htmlIndexResources.get(i).setPrevious(htmlIndexResources.get(i - 1));
			}
			if (i < htmlIndexResources.size() - 1) {
				htmlIndexResources.get(i).setNext(htmlIndexResources.get(i + 1));
			}
		}
	}

	private void sortImages() {
		if(rawImages.isEmpty()){
			return;
		}
		File tempFile = rawImages.get(0);
		Resource tempImageResource = generator.createInitResource(tempFile);
		String tempSortStyle = tempImageResource.getSettings().getStringAt(CFG_KEY_GALLERY_SORT_STYLE);
		if (tempSortStyle == null) {
			return;
		}
		Comparator<File> tempComparator = null;
		if (tempSortStyle.equals(CFG_VALUE_GALLERY_SORT_STYLE_BY_NAME)) {
			tempComparator = new FileComparatorByName();
		} else if (CFG_VALUE_GALLERY_SORT_STYLE_BY_DATE.equals(tempSortStyle)) {
			tempComparator = new FileComparatorByDate();
		} else if (CFG_VALUE_GALLERY_SORT_STYLE_CUSTOM.equals(tempSortStyle)) {
			String tempOrder = tempImageResource.getSettings().getStringAt(CFG_KEY_GALLERY_SORT_STYLE_CUSTOM_FILE_SEQUENCE);
			tempComparator = new FileComparatorCustom(tempOrder);
		} else {
			// file system default order
		}
		if (tempComparator != null) {
			Collections.sort(rawImages, tempComparator);
		}
	}

	private File getThumbnailFile(ImageResource anImage) {
		String tempName = anImage.getAbsoluteSourcePath();
		StringBuilder tempBuilder = new StringBuilder(tempName);
		int tempIdx = tempName.lastIndexOf('.');
		String tempPf = generator.getSettings().getStringAt(
				GenerateGalleryProcessor.CFG_KEY_FILE_NAME_POSTFIX_THUMBNAIL,
				GenerateGalleryProcessor.DEFAULT_FILENAME_POSTFIX_THUMBNAIL);
		tempBuilder.insert(tempIdx, tempPf);
		File tempFile = new File(tempBuilder.toString());
		return tempFile;
	}

	List<HtmlImageResource> getHtmlImagesForIndexPage(int aI) {
		int tempPageCount = getIndexPageCount();
		if (aI >= tempPageCount) {
			throw new IllegalArgumentException("Illegal index number >= " + tempPageCount);
		}
		int tempImageCount = getImagesPerIndexPageCount();
		int tempStartIdx = tempImageCount * aI;
		int tempEndIdx = Math.min(htmlImageResources.size(), tempStartIdx + tempImageCount);
		return new ArrayList<HtmlImageResource>(htmlImageResources.subList(tempStartIdx, tempEndIdx));
	}

	List<HtmlImageResource> getNavigationHtmlImagesResourceForImage(int aI) {
		List<HtmlImageResource> tempResult = new ArrayList<HtmlImageResource>();
		int tempHalf = navigationImagesPerPageCount / 2;
		int tempStartIdx = aI - tempHalf;
		int tempEndIdx = aI + tempHalf;
		
		for (int i = tempStartIdx; i <= tempEndIdx; i++) {
			if (i >= 0 && i < htmlImageResources.size()) {
				tempResult.add(htmlImageResources.get(i));
			} else {
				tempResult.add(null);
			}
		}
		return tempResult;
	}

	public List<HtmlIndexResource> getHtmlIndexResources() {
		if (htmlIndexResources == null) {
			init();
		}
		return htmlIndexResources;
	}

}
