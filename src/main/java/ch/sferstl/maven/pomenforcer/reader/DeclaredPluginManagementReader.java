package ch.sferstl.maven.pomenforcer.reader;

import java.util.List;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

import org.apache.maven.model.Plugin;
import org.w3c.dom.Document;

import com.google.common.collect.Lists;
import com.thoughtworks.xstream.XStream;


public class DeclaredPluginManagementReader extends AbstractPomSectionReader<List<Plugin>> {

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
    xstream.alias(PLUGIN_ALIAS, Plugin.class);
    xstream.omitField(Plugin.class, "configuration");
    xstream.omitField(Plugin.class, "extensions");
    xstream.omitField(Plugin.class, "goals");
    xstream.omitField(Plugin.class, "executions");
    xstream.omitField(Plugin.class, "dependencies");
  }

  @Override
  protected List<Plugin> getUndeclaredSection() {
    return Lists.newArrayList();
  }
}
