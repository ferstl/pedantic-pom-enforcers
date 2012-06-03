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
package com.github.ferstl.maven.pomenforcers.reader;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomReader;


public abstract class AbstractPomSectionReader<T> {
  private final Document pom;
  private XPathExpression xpathExpression;

  public AbstractPomSectionReader(Document pom) {
    this.pom = pom;
    XPath xpath = XPathFactory.newInstance().newXPath();
    try {
      this.xpathExpression = this.createXPathExpression(xpath);
    } catch (XPathExpressionException e) {
      throw new IllegalStateException("Unable to create XPath expression", e);
    }
  }

  @SuppressWarnings("unchecked")
  public T read() {
    Element pomElement;
    try {
      pomElement = (Element) this.xpathExpression.evaluate(this.pom, XPathConstants.NODE);
    } catch (XPathExpressionException e) {
      throw new IllegalStateException("Unable to extract POM section", e);
    }

    T section;
    if (pomElement == null) {
      section = getUndeclaredSection();
    } else {
      DomReader domReader = new DomReader(pomElement);
      XStream xstream = new XStream();
      this.configureXStream(xstream);
      section = (T) xstream.unmarshal(domReader);
    }
    return section;
  }

  protected abstract XPathExpression createXPathExpression(XPath xpath) throws XPathExpressionException;
  protected abstract void configureXStream(XStream xstream);

  /**
   * Returns a default value (aka. null object) for an undeclared section in the
   * POM. Override this method in case you don't want to deal with
   * <code>null</code> values.
   * @return The default value for an undeclared section.
   */
  protected T getUndeclaredSection() {
    return null;
  }
}
