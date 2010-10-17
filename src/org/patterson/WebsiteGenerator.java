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

package org.patterson;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Properties;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.patterson.menu.BasicMenuManager;
import org.patterson.menu.MenuManager;
import org.patterson.resource.BasicResource;
import org.patterson.resource.ChainedSettings;
import org.patterson.resource.GeneratorSettings;
import org.patterson.resource.Resource;
import org.patterson.resource.Settings;
import org.patterson.util.FilePath;

/**
 * @author waterman
 * 
 */
public class WebsiteGenerator {

	private Logger logger = Logger.getLogger(WebsiteGenerator.class.getName());

	private GeneratorSettings settings;
	/** The resource which is currently generated */
	private Resource currentResource;

	private TemplateProcessor processor;
	private MenuManager menuManager;

	public WebsiteGenerator(InputStream aPropertiesStream) throws IOException, ConfigurationException {
		Properties tempProps = new Properties();
		tempProps.load(aPropertiesStream);
		aPropertiesStream.close();
		init(tempProps);
	}

	public MenuManager getMenuManager() {
		if (menuManager == null) {
			menuManager = createMenuManager();
		}
		return menuManager;
	}

	private MenuManager createMenuManager() {
		return new BasicMenuManager(this);
	}

	public TemplateProcessor getProcessor() {
		return processor;
	}

	public Resource getCurrentResource() {
		return currentResource;
	}

	public WebsiteGenerator(GeneratorSettings aSettings) throws ConfigurationException {
		init(aSettings);
	}

	public WebsiteGenerator(Properties aProperties) throws IOException, ConfigurationException {
		init(aProperties);
	}

	public void generate() throws IOException, WebsiteGeneratorException {
		cleanTargetDir();
		processSourceDir();
	}

	/**
	 * @throws IOException
	 * @throws WebsiteGeneratorException
	 * 
	 */
	@SuppressWarnings("unchecked")
	private void processSourceDir() throws IOException, WebsiteGeneratorException {
		File tempSrcDir = new File(settings.getSrcDir());
		logger.info("Processing source dir: " + tempSrcDir.getAbsolutePath());
		processor = getTemplateProcessor();

		for (Iterator<File> tempFileIter = FileUtils.iterateFiles(tempSrcDir, null, true); tempFileIter.hasNext();) {
			File tempFile = tempFileIter.next();
			currentResource = createResource();
			initResourceFor(currentResource, tempFile);

			if (tempFile.isDirectory()) {
				// no op
			} else if (tempFile.getParent() != null && tempFile.getParentFile().getName().equals("CVS")) {
				logger.fine("Skipping CVS resource " + tempFile.getAbsolutePath());
			} else if (processor.isCopyFile(tempFile)) {
				copyFileToTargetDir(tempFile);
			} else if (processor.isProcessingRequired(tempFile)) {
				processor.process(this, tempFile);
			} else {
				// Maybe a resource referenced by a velocity template
				logger.fine("Skipping velocity resource " + tempFile.getAbsolutePath());
			}
			menuManager = null;// reset, so every file gets the original menu
		}
	}

	private TemplateProcessor getTemplateProcessor() {
		return new GroovyTemplateProcessor(this);
	}

	/**
	 * @param aFile
	 * @throws IOException
	 */
	private void copyFileToTargetDir(File aFile) throws IOException {
		File tempTargetFile = calculateTargetFile(aFile);
		logger.fine("Copying " + aFile.getAbsolutePath() + " to " + tempTargetFile.getAbsolutePath());
		FileUtils.copyFile(aFile, tempTargetFile);
	}

	/**
	 * Replaces wildcard for {@link GeneratorSettings#KEY_SRC_DIR}. Sample
	 * ${srcDir}/my/lib.css
	 */
	public String calculateTargetFileName(String aPath) {
		aPath = expandVariablesInFilename(aPath);
		return calculateTargetFileName(new File(aPath));
	}

	public String expandVariablesInFilename(String aPath) {
		String tempPattern = "${" + GeneratorSettings.KEY_SRC_DIR + "}";
		if (aPath.startsWith(tempPattern)) {
			String tempSrcDir = settings.getSrcDirAbsolutePath();
			aPath = tempSrcDir + aPath.substring(tempPattern.length());
		}
		return aPath;
	}

	/**
	 * @param aFile
	 * @return
	 */
	public File calculateTargetFile(File aFile) {
		String tempDir = calculateTargetFileName(aFile);
		return new File(tempDir);
	}

	/**
	 * Calculates the URL from "aSrc" to "aTarget". E.g. the result from
	 * "C:\MyProject\site\articles\details\4711.html" to
	 * "C:\MyProject\site\about\about.html" will be "../../about/about.html"
	 * 
	 * @param aSrc
	 * @param aTarget
	 * @return The URL pointing from a href in "aSrc" to "aTarget"
	 */
	public String calculateRelativeUrl(File aSrc, File aTarget) {
		FilePath tempSrc = FilePath.from(aSrc.getAbsolutePath());
		FilePath tempTarget = FilePath.from(aTarget.getAbsolutePath());

		FilePath tempRelSrc = tempSrc.stripCommonLeadingFolders(tempTarget);
		FilePath tempRelTarget = tempTarget.stripCommonLeadingFolders(tempSrc);
		int tempRemainingDirCount = tempRelSrc.getFolders().size();
		String tempResult = "";
		for (int i = 0; i < tempRemainingDirCount; i++) {
			tempResult += "../";
		}
		return tempResult + tempRelTarget.toUrlString();
	}

	public String calulateRelativeUrlFromCurrentResourceTo(String aTargetFile) {
		Resource tempTarget = createInitResource(aTargetFile);
		String tempPath = currentResource.getRelativeTargetPathTo(tempTarget);
		return tempPath;
	}

	/**
	 * @param aFile
	 * @return
	 */
	public String calculateTargetFileName(File aFile) {
		if (aFile.getParent() == null) {
			return settings.getTargetDir() + File.separator + aFile.getName();
		}
		String tempFilePath = aFile.getAbsolutePath();
		String tempSrcPath = new File(settings.getSrcDir()).getAbsolutePath();
		String tempDir = tempFilePath.substring(tempSrcPath.length());
		tempDir = new File(settings.getTargetDir()).getAbsolutePath() + tempDir;
		return tempDir;
	}

	/**
	 * @throws IOException
	 */
	private void cleanTargetDir() throws IOException {
		File tempDest = new File(settings.getTargetDir());
		logger.info("Cleaning target dir: " + tempDest.getAbsolutePath());
		if (tempDest.exists()) {
			FileUtils.cleanDirectory(tempDest);
		}
	}

	/**
	 * @param aProps
	 * @throws IOException
	 * @throws ConfigurationException
	 */
	private void init(Properties aProps) throws IOException, ConfigurationException {
		if (aProps == null) {
			throw new IllegalArgumentException("Stream must not be null");
		}
		GeneratorSettings tempSettings = new GeneratorSettings(aProps);
		init(tempSettings);
	}

	private void init(GeneratorSettings aSettings) throws ConfigurationException {
		settings = aSettings;
		settings.checkDirectories();
	}

	/**
	 * @return the settings
	 */
	public GeneratorSettings getSettings() {
		return settings;
	}

	public static void main(String[] args) throws WebsiteGeneratorException, IOException, ConfigurationException {
		if (args.length != 1) {
			System.out.println("Path to property file required.");
			System.exit(-1);
		}
		InputStream tempIn;
		try {
			tempIn = new FileInputStream(args[0]);
		} catch (FileNotFoundException e) {
			throw new WebsiteGeneratorException("Properties file '" + args[0] + "' not found.", e);
		}
		WebsiteGenerator tempGenerator = new WebsiteGenerator(tempIn);
		tempGenerator.generate();
	}

	public Resource createResource() {
		Resource tempResource = new BasicResource();
		return tempResource;
	}

	public Resource createInitResource(File aFile) {
		Resource tempResource = createResource();
		initResourceFor(tempResource, aFile);
		return tempResource;
	}

	public void initResourceFor(Resource aResource, File aFile) {
		aResource.setSourceFile(aFile);
		aResource.setWebsiteGenerator(this);
		Settings tempSettings = createSettingsFor(aResource, aFile);
		aResource.setSettings(tempSettings);
	}

	private Settings createSettingsFor(Resource aResource, File aFile) {
		Settings tempParentSettings = settings;
		if (!aFile.isDirectory()) {
			String tempPropsName = aFile.getParent() + File.separator + "folder.properties";
			tempParentSettings = new ChainedSettings(new File(tempPropsName), tempParentSettings);
		}
		String tempPropsName = aResource.getAbsoluteSourceFilenameNoExtension() + ".properties";
		Settings tempFileSettings = new ChainedSettings(new File(tempPropsName), tempParentSettings);
		return tempFileSettings;
	}

	public Resource createInitResource(String aPath) {
		aPath = expandVariablesInFilename(aPath);
		return createInitResource(new File(aPath));
	}
}
