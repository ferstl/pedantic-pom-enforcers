package ch.sferstl.maven.pomenforcer.reader;

import java.util.List;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;

import com.google.common.collect.Lists;
import com.thoughtworks.xstream.XStream;


public class DeclaredModulesReader extends AbstractPomSectionReader<List<String>> {

  private static final String MODULES_XPATH = "/project/modules";
  private static final String MODULES_ALIAS = "modules";
  private static final String MODULE_ALIAS = "module";

  public DeclaredModulesReader(Document pom) {
    super(pom);
  }

  @Override
  protected XPathExpression createXPathExpression(XPath xpath) throws XPathExpressionException {
    return xpath.compile(MODULES_XPATH);
  }

  @Override
  protected void configureXStream(XStream xstream) {
    xstream.alias(MODULES_ALIAS, List.class);
    xstream.alias(MODULE_ALIAS, String.class);
  }

  @Override
  protected List<String> getUndeclaredSection() {
    return Lists.newArrayList();
  }
}
