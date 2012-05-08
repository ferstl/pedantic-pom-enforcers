package ch.sferstl.maven.enforcerrules;

import java.util.List;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

import org.apache.maven.model.Dependency;
import org.w3c.dom.Document;

import com.google.common.collect.Lists;
import com.thoughtworks.xstream.XStream;


public class DeclaredDependenciesReader extends AbstractPomSectionReader<List<Dependency>> {

  private static final String DEPENDENCIES_XPATH = "/project/dependencies";
  private static final String DEPENDENCIES_ALIAS = "dependencies";
  private static final String DEPENDENCY_ALIAS = "dependency";

  public DeclaredDependenciesReader(Document pom) {
    super(pom);
  }

  /** {@inheritDoc} */
  @Override
  protected XPathExpression createXPathExpression(XPath xpath) throws XPathExpressionException {
    return xpath.compile(DEPENDENCIES_XPATH);
  }

  /** {@inheritDoc} */
  @Override
  protected void configureXStream(XStream xstream) {
    xstream.alias(DEPENDENCIES_ALIAS, List.class);
    xstream.alias(DEPENDENCY_ALIAS, Dependency.class);
  }

  /** {@inheritDoc} */
  @Override
  protected List<Dependency> getUndeclaredSection() {
    return Lists.newArrayList();
  }

}
