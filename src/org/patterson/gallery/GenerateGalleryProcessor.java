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

import static org.patterson.util.HTMLUtil.attr;
import static org.patterson.util.HTMLUtil.elem;
import static org.patterson.util.HTMLUtil.startElem;
import static org.patterson.util.HTMLUtil.stopElem;

import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.patterson.GroovyTemplateProcessor;
import org.patterson.GroovyTemplateSupport;
import org.patterson.Processor;
import org.patterson.WebsiteGenerator;
import org.patterson.WebsiteGeneratorException;
import org.patterson.resource.GeneratorSettings;
import org.patterson.resource.Resource;

/**
 * Generates a Gallery. Assumptions:
 * <ul>
 * <li>All files of the Gallery are located in the directory this
 * {@link Processor} is called for.</li>
 * <li>The thumbnails for the images are located in the same directory, having a
 * file name ending on "_thumbnail" (plus file name extension)</li>
 * </ul>
 * 
 * @author waterman
 * 
 */
public class GenerateGalleryProcessor implements Processor {

	private static final String CFG_KEY_FOOTER_FILE_NAME = "footer.file.name";
	private static final String CFG_KEY_HEADER_FILE_NAME = "header.file.name";
	
	protected static final String DEFAULT_FILENAME_POSTFIX_THUMBNAIL = "_thumbnail";
	protected static final String CFG_KEY_FILE_NAME_POSTFIX_THUMBNAIL = "file.name.postfix.thumbnail";

	/**
	 * Generates a Gallery for the {@link WebsiteGenerator}s current resource,
	 * using the content of configured files as header and footer. The name of
	 * the files may be specified in the {@link GeneratorSettings} using the
	 * keys {@link #CFG_KEY_FOOTER_FILE_NAME} and
	 * {@link #CFG_KEY_HEADER_FILE_NAME}. The files may be Groovy templates.
	 * 
	 * @param aGenerator
	 * @param aBinding
	 * @throws IOException
	 * @throws WebsiteGeneratorException
	 */
	public static void run(WebsiteGenerator aGenerator, Map<?, ?> aBinding) throws IOException,
			WebsiteGeneratorException {
		GeneratorSettings tempSettings = aGenerator.getSettings();

		String tempHeaderFileName = tempSettings.getStringAt(CFG_KEY_HEADER_FILE_NAME);
		String tempFooterFileName = tempSettings.getStringAt(CFG_KEY_FOOTER_FILE_NAME);

		GroovyTemplateProcessor tempProcessor = (GroovyTemplateProcessor) aGenerator.getProcessor();
		GroovyTemplateSupport tempTemplateSupport = tempProcessor.getTemplateSupport();

		String tempHeader = null;
		String tempFooter = null;
		if (tempHeaderFileName != null) {
			tempHeader = tempTemplateSupport.evalTemplate(aBinding, tempHeaderFileName);
		}
		if (tempFooterFileName != null) {
			tempFooter = tempTemplateSupport.evalTemplate(aBinding, tempFooterFileName);
		}

		GenerateGalleryProcessor tempGalleryProcessor = new GenerateGalleryProcessor();
		tempGalleryProcessor.setImagePageHeaderHtml(tempHeader);
		tempGalleryProcessor.setIndexPageHeaderHtml(tempHeader);
		tempGalleryProcessor.setImagePageFooterHtml(tempFooter);
		tempGalleryProcessor.setIndexPageFooterHtml(tempFooter);

		String tempPath = aGenerator.getCurrentResource().getAbsoluteSourcePath();
		File tempFile = new File(tempPath);
		tempGalleryProcessor.process(aGenerator, tempFile, aBinding);
	}

	private WebsiteGenerator generator;
	private Resource galleryPlaceholder;
	private Logger logger = Logger.getLogger(getClass().getName());
	private Writer writer;
	private String cssFilePath;
	private String docType;

	private String indexPageHeaderHtml;
	private String indexPageFooterHtml;
	private String imagePageHeaderHtml;
	private String imagePageFooterHtml;

	/**
	 * @param aFile
	 *            A File in the directory containing the images
	 */
	@Override
	public void process(WebsiteGenerator aGenerator, File aFile, Map aBinding) throws IOException, WebsiteGeneratorException {
		logger.info("Processing gallery: " + aFile.getAbsolutePath());
		generator = aGenerator;
		galleryPlaceholder = aGenerator.createInitResource(aFile);
		File tempDir = aFile.getParentFile();
		final String tempPf = generator.getSettings().getStringAt(
				GenerateGalleryProcessor.CFG_KEY_FILE_NAME_POSTFIX_THUMBNAIL,
				GenerateGalleryProcessor.DEFAULT_FILENAME_POSTFIX_THUMBNAIL);

		File[] tempImageFiles = tempDir.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				String tempName = name.toLowerCase();
				return (tempName.endsWith(".jpg") || tempName.endsWith(".jpeg") || tempName.endsWith(".png") || tempName
						.endsWith(".gif"))
						&& !tempName.contains(tempPf + ".");
			}
		});
		Gallery tempGallery = new Gallery(tempImageFiles, aGenerator);
		generate(tempGallery);
	}

	private void generate(Gallery aGallery) throws IOException {
		for (HtmlIndexResource tempIndex : aGallery.getHtmlIndexResources()) {
			generateIndexPage(aGallery, tempIndex);
		}
		for (HtmlImageResource tempImage : aGallery.getHtmlImageResources()) {
			generateImagePage(aGallery, tempImage);
		}
	}

	private void generateImagePage(Gallery aGallery, HtmlImageResource anImage) throws IOException {

		openWriter(anImage);
		try {
			write(getImagePageHeaderHtml());
			write(startElem("div", attr("id", "wsgImagePanel")));

			// thumbnail navigation
			write(startElem("table"));
			write(startElem("tr"));
			List<HtmlImageResource> tempImages = anImage.getPreviewImageResources();
			for (int i = 0; i < tempImages.size(); i++) {
				HtmlImageResource tempPreviewImageFile = tempImages.get(i);
				boolean tempExists = tempPreviewImageFile != null;
				boolean tempCurrent = tempPreviewImageFile == anImage;
				String tempClassAttr = attr("class", "wsgPreviewImageCellEmpty");
				String tempIdAttr = null;
				if (tempExists) {
					tempClassAttr = attr("class", "wsgPreviewImageCell");
				}
				if (tempCurrent) {
					tempIdAttr = attr("id", "wsgPreviewCurrent");
				}
				write(startElem("td", tempIdAttr, tempClassAttr));

				if (tempExists) {
					String tempSrc = anImage.getRelativeTargetPathTo(tempPreviewImageFile.getImageResource()
							.getThumbnail());
					String tempHref = anImage.getRelativeTargetPathTo(tempPreviewImageFile);
					write(startElem("a", attr("href", tempHref)));
					write(elem("img", attr("src", tempSrc)));
					write(stopElem("a"));
				}
				write(stopElem("td"));
			}
			write(stopElem("tr"));
			write(stopElem("table"));

			write(startElem("div", attr("id", "wsgImageContent")));

			write(startElem("div", attr("id", "wsgRelativeImageNav")));

			// navigate backwards
			write(startElem("span", attr("id", "wsgImageNavBw")));
			if (!anImage.isFirst()) {
				String tempHref = anImage.getRelativeTargetPathTo(anImage.getPrevious());
				write(startElem("a", attr("href", tempHref)));
				write(startElem("span", attr("id","wsgImageNavBwLabel")));
				write("&lt;&lt;");
				write(stopElem("span"));
				write(elem("span", attr("id","wsgImageNavBwImage")));
				write(stopElem("a"));
			}
			write(stopElem("span"));

			// navigate to index
			write(startElem("span", attr("id", "wsgImageNavIdx")));
			String tempIdxHref = anImage.getRelativeTargetPathTo(anImage.getIndex());
			write(startElem("a", attr("href", tempIdxHref)));
			write("Index");
			write(stopElem("a"));
			write(stopElem("span"));

			// navigate forward
			write(startElem("span", attr("id", "wsgImageNavFw")));
			if (!anImage.isLast()) {
				String tempHref = anImage.getRelativeTargetPathTo(anImage.getNext());
				write(startElem("a", attr("href", tempHref)));
				write(startElem("span", attr("id","wsgImageNavFwLabel")));
				write("&gt;&gt;");
				write(stopElem("span"));
				write(elem("span", attr("id","wsgImageNavFwImage")));

				write(stopElem("a"));
			}
			write(stopElem("span"));

			write(stopElem("div"));// wsgRelativeImageNav

			// image
			write(startElem("div", attr("id", "wsgImageItemPanel")));
			write(elem("img", attr("src", anImage.getRelativeTargetPathTo(anImage.getImageResource()))));
			write(stopElem("div"));

			write(stopElem("div")); // wsgImageContent

			write(stopElem("div")); // wsgImagePanel
			write(getImagePageFooterHtml());
		} finally {
			closeWriter();
		}

	}

	private void generateIndexPage(Gallery aGallery, HtmlIndexResource anIndexPage) throws IOException {
		openWriter(anIndexPage);
		try {
			write(getIndexPageHeaderHtml());
			write(startElem("div", attr("id", "wsgIndexPanel")));
			write("\n");

			write(startElem("div", attr("id", "wsgIndexContent")));

			write(startElem("div", attr("id", "wsgRelativeIndexNav")));

			// navigate backwards
			write(startElem("span", attr("id", "wsgIndexNavBw")));
			if (!anIndexPage.isFirst()) {
				String tempHref = anIndexPage.getRelativeTargetPathTo(anIndexPage.getPrevious());
				write(startElem("a", attr("href", tempHref)));
				write(startElem("span", attr("id","wsgIndexNavBwLabel")));
				write("&lt;&lt;");
				write(stopElem("span"));
				write(elem("span", attr("id","wsgIndexNavBwImage")));
				write(stopElem("a"));
			}
			write(stopElem("span"));
			
			// navigate forward
			write(startElem("span", attr("id", "wsgIndexNavFw")));
			if (!anIndexPage.isLast()) {
				String tempHref = anIndexPage.getRelativeTargetPathTo(anIndexPage.getNext());
				write(startElem("a", attr("href", tempHref)));
				write(startElem("span", attr("id","wsgIndexNavFwLabel")));
				write("&gt;&gt;");
				write(stopElem("span"));
				write(elem("span", attr("id","wsgIndexNavFwImage")));
				write(stopElem("a"));
			}
			write(stopElem("span"));
			write(stopElem("div"));// wsgRelativeIndexNav

			// index table
			write("\n");
			write(startElem("table"));
			int tempIdx = 0;
			for (int r = 0; r < aGallery.getIndexPageRowCount(); r++) {
				write("\t");
				write(startElem("tr"));
				for (int c = 0; c < aGallery.getIndexPageColumnCount(); c++) {
					HtmlImageResource tempTarget = anIndexPage.getPreviewImageResource(tempIdx);
					boolean tempExists = tempTarget != null;
					String tempClassAttr = "";
					if (tempExists) {
						tempClassAttr = attr("class", "wsgPreviewImageCell");
					}
					write(startElem("td", tempClassAttr));

					if (tempExists) {
						String tempSrc = anIndexPage.getRelativeTargetPathTo(tempTarget.getImageResource()
								.getThumbnail());
						String tempHref = anIndexPage.getRelativeTargetPathTo(tempTarget);

						write(startElem("a", attr("href", tempHref)));
						write(elem("img", attr("src", tempSrc)));
						write(stopElem("a"));
					}
					write(stopElem("td"));
					tempIdx++;
				}
				write(stopElem("tr"));
				write("\n");
			}
			write(stopElem("table"));
			write("\n");

			write(stopElem("div"));// wsgIndexContent
			write("\n");

			write(stopElem("div"));// wsgIndexPanel
			write(getIndexPageFooterHtml());
		} finally {
			closeWriter();
		}
	}

	public String getDocType() {
		if (docType != null) {
			return docType;
		}
		docType = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">";

		return docType;
	}

	public void setDocType(String aDocType) {
		docType = aDocType;
	}

	public String getCssFilePath() {
		if (cssFilePath != null) {
			return cssFilePath;
		}
		return "${" + GeneratorSettings.KEY_SRC_DIR + "}" + File.separator + "wsgGallery.css";
	}

	public void setCssFilePath(String aCssFilePath) {
		cssFilePath = aCssFilePath;
	}

	public String getIndexPageHeaderHtml() {
		if (indexPageHeaderHtml != null) {
			return indexPageHeaderHtml;
		}
		indexPageHeaderHtml = createHeaderHtml();
		return indexPageHeaderHtml;
	}

	private String createHeaderHtml() {
		String tempCssPath = getCssUrl();
		String tempHtml = getDocType() + "\n" + startElem("html", attr("xmlns", "http://www.w3.org/1999/xhtml")) + "\n"
				+ startElem("head") + "\n" + elem("title") + "\n"
				+ elem("link", attr("rel", "stylesheet"), attr("type", "text/css"), attr("href", tempCssPath)) + "\n"
				+ stopElem("head") + "\n" + startElem("body") + "\n";
		return tempHtml;
	}

	private String getCssUrl() {
		String tempPath = getCssFilePath();
		if (tempPath == null) {
			return "";
		}
		Resource tempCssResource = generator.createInitResource(tempPath);
		tempPath = galleryPlaceholder.getRelativeTargetPathTo(tempCssResource);
		return tempPath;
	}

	public void setIndexPageHeaderHtml(String aIndexPageHeaderHtml) {
		indexPageHeaderHtml = aIndexPageHeaderHtml;
	}

	public String getIndexPageFooterHtml() {
		if (indexPageFooterHtml != null) {
			return indexPageFooterHtml;
		}
		indexPageFooterHtml = "\n" + stopElem("body") + stopElem("html");
		return indexPageFooterHtml;
	}

	public void setIndexPageFooterHtml(String aIndexPageFooterHtml) {
		indexPageFooterHtml = aIndexPageFooterHtml;
	}

	public String getImagePageHeaderHtml() {
		if (imagePageHeaderHtml != null) {
			return imagePageHeaderHtml;
		}
		imagePageHeaderHtml = createHeaderHtml();
		return imagePageHeaderHtml;
	}

	public void setImagePageHeaderHtml(String aImagePageHeaderHtml) {
		imagePageHeaderHtml = aImagePageHeaderHtml;
	}

	public String getImagePageFooterHtml() {
		if (imagePageFooterHtml != null) {
			return imagePageFooterHtml;
		}
		imagePageFooterHtml = "\n" + stopElem("body") + stopElem("html");
		return imagePageFooterHtml;
	}

	public void setImagePageFooterHtml(String aImagePageFooterHtml) {
		imagePageFooterHtml = aImagePageFooterHtml;
	}

	private void closeWriter() {
		try {
			writer.close();
		} catch (IOException e) {
			logger.log(Level.WARNING, "Error closing writer.", e);
		}
	}

	private void openWriter(Resource aResource) throws IOException {
		FileWriter tempWriter = new FileWriter(aResource.getAbsoluteTargetPath());
		writer = tempWriter;
	}

	public void write(String str) throws IOException {
		writer.write(str);
		writer.flush();// TODO: remove
	}
}
