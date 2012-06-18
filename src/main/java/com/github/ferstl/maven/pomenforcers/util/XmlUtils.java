/*
 * Copyright (c) 2012 by The Author(s)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.ferstl.maven.pomenforcers.util;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public final class XmlUtils {

  private static final DocumentBuilder DOC_BUILDER;
  private static final XPath XPATH;

  static {
    XPATH = XPathFactory.newInstance().newXPath();
    try {
      DOC_BUILDER = DocumentBuilderFactory.newInstance().newDocumentBuilder();
    } catch (ParserConfigurationException e) {
      throw new IllegalStateException("Cannot create document builder", e);
    }
  }

  public static Document parseXml(File file) {
    if (!file.exists()) {
      throw new IllegalArgumentException("File " + file + " does not exist.");
    }
    try {
      return DOC_BUILDER.parse(file);
    } catch (SAXException | IOException e) {
      throw new IllegalStateException("Unable to parse XML file " + file, e);
    }
  }

  public static Element evaluateXPath(String expression, Document document) {
    try {
      XPathExpression compiledExpression = XPATH.compile(expression);
      return (Element) compiledExpression.evaluate(document, XPathConstants.NODE);
    } catch (XPathExpressionException e) {
      throw new IllegalArgumentException("Cannot evaluate XPath expression '" + expression + "'");
    }
  }

  private XmlUtils() {}
}
