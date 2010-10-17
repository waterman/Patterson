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

import java.io.Closeable;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author waterman
 * 
 */
public class GroovyTemplateProcessor implements TemplateProcessor {

	private Logger logger = Logger.getLogger(GroovyTemplateProcessor.class.getName());
	private WebsiteGenerator generator;
	private GroovyTemplateSupport templateSupport;

	/**
	 * @param aWebsiteGenerator
	 */
	public GroovyTemplateProcessor(WebsiteGenerator aWebsiteGenerator) {
		generator = aWebsiteGenerator;
		templateSupport = new GroovyTemplateSupport(aWebsiteGenerator);
	}

	public boolean isProcessingRequired(File aFile) {
		return aFile.getName().endsWith(".groovy.html") || aFile.getName().endsWith(".groovy.processor");
	}

	public boolean isCopyFile(File aFile) {
		return !aFile.getAbsolutePath().contains(".groovy");
	}

	/**
	 * @see org.patterson.TemplateProcessor#processVelocityTemplate(java.io.
	 *      File)
	 */
	@Override
	public void process(WebsiteGenerator aGenerator, File aFile) throws IOException, WebsiteGeneratorException {

		if (isTemplate(aFile)) {
			logger.fine("Processing template: " + aFile.getAbsolutePath());
			processTemplate(aFile);
		} else {
			logger.fine("Calling processor: " + aFile.getAbsolutePath());
			callProcessor(aFile);
		}
	}

	private void callProcessor(File aFile) throws WebsiteGeneratorException, IOException {
		Class<?> tempScript = templateSupport.parseClass(aFile);

		Processor tempProcessor;
		try {
			tempProcessor = (Processor) tempScript.newInstance();
		} catch (InstantiationException e) {
			throw new WebsiteGeneratorException("Intantiation of Groovy Object for file faild: '"
					+ aFile.getAbsolutePath() + "'.", e);
		} catch (IllegalAccessException e) {
			throw new WebsiteGeneratorException("Intantiation of Groovy Object for file faild: '"
					+ aFile.getAbsolutePath() + "'.", e);
		}
		tempProcessor.process(generator, aFile);
	}

	private void processTemplate(File aSrcFile) throws IOException, WebsiteGeneratorException {
		String tempTargetFileName = generator.calculateTargetFileName(aSrcFile);
		File tempTargetFile = new File(tempTargetFileName.replace(".groovy", ""));
		tempTargetFile.getParentFile().mkdirs();
		FileWriter tempOutputWriter = new FileWriter(tempTargetFile);
		FileReader tempInput = new FileReader(aSrcFile);
		try {
			templateSupport.evalTemplate(tempInput, tempOutputWriter);
		} finally {
			closeWriterFor(tempOutputWriter, tempTargetFile);
			closeWriterFor(tempInput, aSrcFile);
		}
	}

	private boolean isTemplate(File aFile) {
		return aFile.getName().toLowerCase().endsWith(".html");
	}

	/**
	 * @param aClosable
	 * @param aFile
	 */
	private void closeWriterFor(Closeable aClosable, File aFile) {
		try {
			aClosable.close();
		} catch (IOException e) {
			logger.log(Level.WARNING, "Error while closing reader/writer for " + aFile.getAbsolutePath(), e);
		}
	}

	public GroovyTemplateSupport getTemplateSupport() {
		return templateSupport;
	}
}
