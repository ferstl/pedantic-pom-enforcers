/*
 * Copyright (c) 2012 - 2020 the original author or authors.
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

import javax.xml.namespace.QName;
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
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public final class XmlUtils {

  /**
   * Parses the given file into an XML {@link Document}.
   * @param file The file to parse.
   * @return The created XML {@link Document}.
   */
  public static Document parseXml(File file) {
    if (!file.exists()) {
      throw new IllegalArgumentException("File " + file + " does not exist.");
    }
    try {
      DocumentBuilder docBuilder = createDocumentBuilder();
      return docBuilder.parse(file);
    } catch (SAXException | IOException e) {
      throw new IllegalStateException("Unable to parse XML file " + file, e);
    }
  }

  /**
   * Returns the XML {@link Element} matching the given XPath expression.
   * @param expression XPath expression.
   * @param document XML document to search for the element.
   * @return The matching XML {@link Element}.
   */
  public static Element evaluateXPathAsElement(String expression, Document document) {
    return evaluateXpath(expression, document, XPathConstants.NODE);
  }

  /**
   * Returns the XML {@link NodeList} matching the given XPath expression.
   * @param expression XPath expression.
   * @param document XML document to search for the node list.
   * @return The matching XML {@link NodeList}.
   */
  public static NodeList evaluateXPathAsNodeList(String expression, Document document) {
    return evaluateXpath(expression, document, XPathConstants.NODESET);
  }

  /**
   * Creates a XML document with the given root element and the given {@link NodeList} as content.
   * @param root The root element of the XML document.
   * @param content Content of the XML document.
   * @return The created XML document.
   */
  public static Document createDocument(String root, NodeList content) {
    DocumentBuilder docBuilder = createDocumentBuilder();
    Document document = docBuilder.newDocument();
    Element rootElement = document.createElement(root);
    document.appendChild(rootElement);

    for (int i = 0; i < content.getLength(); i++) {
      Node item = content.item(i);
      item = document.adoptNode(item.cloneNode(true));
      rootElement.appendChild(item);
    }
    return document;
  }

  @SuppressWarnings("unchecked")
  private static <T> T evaluateXpath(String expression, Document document, QName dataType) {
    try {
      XPath xpath = createXPath();
      XPathExpression compiledExpression = xpath.compile(expression);
      return (T) compiledExpression.evaluate(document, dataType);
    } catch (XPathExpressionException e) {
      throw new IllegalArgumentException("Cannot evaluate XPath expression '" + expression + "'");
    }
  }

  private static XPath createXPath() {
    return XPathFactory.newInstance().newXPath();
  }

  private static DocumentBuilder createDocumentBuilder() {
    try {
      return DocumentBuilderFactory.newInstance().newDocumentBuilder();
    } catch (ParserConfigurationException e) {
      throw new IllegalStateException("Cannot create document builder", e);
    }
  }

  private XmlUtils() {}
}
