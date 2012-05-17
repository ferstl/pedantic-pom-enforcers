package ch.sferstl.maven.pomenforcer;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public final class XmlParser {

  private static final DocumentBuilder docBuilder;
  
  static {
    try {
      docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
    } catch (ParserConfigurationException e) {
      throw new IllegalStateException("Cannot create document builder", e);
    }
  }
  
  public static Document parseXml(File file) {
    try {
      return docBuilder.parse(file);
    } catch (SAXException | IOException e) {
      throw new IllegalStateException("Unable to parse XML file " + file, e);
    }
  }
  
  private XmlParser() {}
}
