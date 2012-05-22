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


public class DeclaredPluginManagementReader extends AbstractPomSectionReader<List<Artifact>> {

  private static final String PLUGIN_MANAGEMENT_XPATH = "/project/build/pluginManagement/plugins";
  private static final String PLUGINS_ALIAS = "plugins";
  private static final String PLUGIN_ALIAS = "plugin";

  public DeclaredPluginManagementReader(Document pom) {
    super(pom);
  }

  @Override
  protected XPathExpression createXPathExpression(XPath xpath) throws XPathExpressionException {
    return xpath.compile(PLUGIN_MANAGEMENT_XPATH);
  }

  @Override
  protected void configureXStream(XStream xstream) {
    xstream.alias(PLUGINS_ALIAS, List.class);
    xstream.alias(PLUGIN_ALIAS, DefaultArtifact.class);
    xstream.omitField(DefaultArtifact.class, "configuration");
    xstream.omitField(DefaultArtifact.class, "extensions");
    xstream.omitField(DefaultArtifact.class, "goals");
    xstream.omitField(DefaultArtifact.class, "executions");
    xstream.omitField(DefaultArtifact.class, "dependencies");
  }

  @Override
  protected List<Artifact> getUndeclaredSection() {
    return Lists.newArrayList();
  }
}
