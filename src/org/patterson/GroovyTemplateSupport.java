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

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyRuntimeException;
import groovy.lang.GroovyShell;
import groovy.lang.Writable;
import groovy.text.SimpleTemplateEngine;
import groovy.text.Template;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.groovy.control.CompilationFailedException;
import org.patterson.resource.Resource;

/**
 * Provides supporting functionality for Groovy templating.
 * 
 * @author waterman
 * 
 */
public class GroovyTemplateSupport {

	private GroovyShell groovyShell;
	private WebsiteGenerator generator;

	public GroovyTemplateSupport(WebsiteGenerator aWebsiteGenerator) {
		generator = aWebsiteGenerator;
	}

	private GroovyShell getGroovyShell() {
		if (groovyShell == null) {
			groovyShell = new GroovyShell();
			GroovyClassLoader tempClassLoader = groovyShell.getClassLoader();
			String tempSrcDir = generator.getSettings().getSrcDirAbsolutePath();
			tempClassLoader.addClasspath(tempSrcDir);
		}
		return groovyShell;
	}

	@SuppressWarnings("unchecked")
	private void evalTemplate(Map<?, ?> aBinding, Reader aTemplateSource, Writer anOutputWriter) throws IOException,
			WebsiteGeneratorException {
		GroovyShell tempShell = getGroovyShell();
		SimpleTemplateEngine tempEngine = new SimpleTemplateEngine(tempShell);
		Template tempTemplate;
		try {
			tempTemplate = tempEngine.createTemplate(aTemplateSource);
		} catch (CompilationFailedException e) {
			throw new WebsiteGeneratorException("Compilation of Groovy template failed: '" + aTemplateSource, e);
		} catch (GroovyRuntimeException e) {
			throw new WebsiteGeneratorException("RuntimeException in Groovy template: '" + aTemplateSource + "'.", e);
		}
		Map<?,?> tempBinding = createBinding(aBinding);
		Writable tempMake = tempTemplate.make(tempBinding);

		if (isConvertToEntities()) {
			StringWriter tempWriter = new StringWriter();
			tempMake.writeTo(tempWriter);
			String tempString = tempWriter.getBuffer().toString();
			tempString = tempString.replace("ü", "&uuml;");
			tempString = tempString.replace("ä", "&auml;");
			tempString = tempString.replace("ö", "&ouml;");
			tempString = tempString.replace("Ü", "&Uuml;");
			tempString = tempString.replace("Ä", "&Auml;");
			tempString = tempString.replace("Ö", "&Ouml;");
			tempString = tempString.replace("ß", "&szlig;");
			anOutputWriter.write(tempString);
		} else {
			tempMake.writeTo(anOutputWriter);
		}
	}
	
	@SuppressWarnings("unchecked")
	private Map<?,?> createBinding(Map<?, ?> aBinding) {
		Map tempBinding = new HashMap();
		tempBinding.putAll(generator.getSettings().asMap());
		if (aBinding != null) {
			tempBinding.putAll(aBinding);
		}
		tempBinding.put("generator", generator);
		tempBinding.put("templateSupport", this);
		tempBinding.put("binding", tempBinding);
		return tempBinding;
	}

	private boolean isConvertToEntities() {
		return true;
	}

	public void evalTemplate(Reader aTemplateSource, Writer anOutputWriter) throws IOException,
			WebsiteGeneratorException {
		evalTemplate(new HashMap<Object, Object>(), aTemplateSource, anOutputWriter);
	}

	public String evalTemplate(Map<?, ?> aBinding, String aFilename) throws IOException, WebsiteGeneratorException {
		Resource tempResource = generator.createInitResource(aFilename);
		return evalTemplate(aBinding, new FileReader(tempResource.getAbsoluteSourcePath()));
	}

	public String evalTemplate(Map<?, ?> aBinding, Reader aTemplateSource) throws IOException,
			WebsiteGeneratorException {
		StringWriter tempWriter = new StringWriter();
		evalTemplate(aBinding, aTemplateSource, tempWriter);
		String tempTxt = tempWriter.toString();
		return tempTxt;
	}

	public Class<?> parseClass(File aFile) throws WebsiteGeneratorException, IOException {
		try {
			return getGroovyShell().getClassLoader().parseClass(aFile);
		} catch (CompilationFailedException e) {
			throw new WebsiteGeneratorException("Parsing of Groovy Script for file faild: '" + aFile.getAbsolutePath()
					+ "'.", e);
		}
	}
	
	public String urlTo(String aResource){
		String tempUrl = generator.calulateRelativeUrlFromCurrentResourceTo(aResource);
		return tempUrl;
	}

}
