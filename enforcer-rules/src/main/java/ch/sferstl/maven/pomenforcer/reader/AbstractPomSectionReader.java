package ch.sferstl.maven.pomenforcer.reader;

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

    // POM element is not declared
    T section;
    if (pomElement == null) {
      section = this.getUndeclaredSection();
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
   * Returns a default value for an undeclared section in the POM. Override this method in case you don't want to deal
   * with <code>null</code> values.
   * @return The default value for an undeclared section.
   */
  protected T getUndeclaredSection() {
    return null;
  }
}
