package ch.sferstl.maven.pomenforcer.reader;

import java.util.List;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DefaultArtifact;
import org.w3c.dom.Document;

import com.google.common.collect.Lists;
import com.thoughtworks.xstream.XStream;

public class DeclaredDependencyManagementReader extends AbstractPomSectionReader<List<Artifact>> {

  private static final String DEPENDENCY_MANAGEMENT_XPATH = "/project/dependencyManagement/dependencies";
  private static final String DEPENDENCIES_ALIAS = "dependencies";
  private static final String DEPENDENCY_ALIAS = "dependency";

  public DeclaredDependencyManagementReader(Document pom) {
    super(pom);
  }

  @Override
  protected XPathExpression createXPathExpression(XPath xpath) throws XPathExpressionException {
    return xpath.compile(DEPENDENCY_MANAGEMENT_XPATH);
  }

  @Override
  protected void configureXStream(XStream xstream) {
    xstream.alias(DEPENDENCIES_ALIAS, List.class);
    xstream.alias(DEPENDENCY_ALIAS, DefaultArtifact.class);
    xstream.omitField(DefaultArtifact.class, "exclusions");
  }

  @Override
  protected List<Artifact> getUndeclaredSection() {
    return Lists.newArrayList();
  }

}
