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

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.github.ferstl.maven.pomenforcers.util.XmlUtils;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomReader;


public abstract class AbstractPomSectionReader<T> {
  private final Document pom;
  private Class<T> clazz;

  public AbstractPomSectionReader(Document pom) {
    this.pom = pom;
  }

  public AbstractPomSectionReader(Document pom, Class<T> clazz) {
    this(pom);
    this.clazz = clazz;
  }

  @SuppressWarnings("unchecked")
  public T read(String xpath) {
    Element pomElement = XmlUtils.evaluateXPathAsElement(xpath, this.pom);

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

  public List<T> read2(String xpath) {
    NodeList elements = XmlUtils.evaluateXPathAsNodeList(xpath, this.pom);
    try {
      JAXBContext ctx = JAXBContext.newInstance(this.clazz);
      Unmarshaller unmarshaller = ctx.createUnmarshaller();
      ArrayList<T> elementList = new ArrayList<>(elements.getLength());
      for (int i = 0; i < elements.getLength(); i++) {
        Node item = elements.item(i);
        elementList.add(this.clazz.cast(unmarshaller.unmarshal(item)));
      }
      return elementList;
    } catch (JAXBException e) {
      throw new IllegalStateException("Unable to read POM section " + xpath);
    }
  }

//public static void main(String[] args) throws Exception {
//JAXBContext ctx = JAXBContext.newInstance(Dependencies.class);
//Unmarshaller unmarshaller = ctx.createUnmarshaller();
//Dependencies artifact = (Dependencies) unmarshaller.unmarshal(new StringReader(
//    "<dependencies>" +
//      "<dependency>" +
//        "<groupId>com.foo</groupId>" +
//        "<artifactId>my-lib</artifactId>" +
//        "<version>1.0-SNAPSHOT</version>" +
//        "<classifier>test</classifier>" +
//        "<type>zip</type>" +
//      "</dependency>" +
//    "</dependencies>"));
//
//System.out.println(artifact);
//}

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
