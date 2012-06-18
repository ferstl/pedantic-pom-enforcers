package com.github.ferstl.maven.pomenforcers.reader;

import java.util.List;

import org.apache.maven.model.Plugin;
import org.w3c.dom.Document;

import com.google.common.collect.Lists;
import com.thoughtworks.xstream.XStream;

public class DeclaredPluginsReader extends AbstractPomSectionReader<List<Plugin>> {

  private static final String PLUGINS_XPATH = "/project/build/plugins";
  private static final String PLUGINS_ALIAS = "plugins";
  private static final String PLUGIN_ALIAS = "plugin";

  public DeclaredPluginsReader(Document pom) {
    super(pom);
  }

  @Override
  protected String getXPathExpression() {
    return PLUGINS_XPATH;
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
    return Lists.newArrayListWithCapacity(0);
  }
}
