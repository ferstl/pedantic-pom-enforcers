package com.github.ferstl.maven.pomenforcers.reader;

public final class XPathExpressions {

  public static final String POM_MODULES = "/project/modules";
  public static final String POM_MANAGED_DEPENDENCIES = "/project/dependencyManagement/dependencies";
  public static final String POM_DEPENENCIES = "/project/dependencies";
  public static final String POM_VERSIONED_DEPENDENCIES = "/project/dependencies/dependency[version]";
  public static final String POM_DEPENDENCIES_WITH_EXCLUSIONS = "/project/dependencies/dependency[exclusions]";

  public static final String POM_MANAGED_PLUGINS = "/project/build/pluginManagement/plugins";
  public static final String POM_PLUGINS = "/project/build/plugins";
  public static final String POM_VERSIONED_PLUGINS = "/project/build/plugins/plugin[version]";
  public static final String POM_CONFIGURED_PLUGINS = "/project/build/plugins/plugin[configuration]";
  public static final String POM_CONFIGURED_PLUGIN_DEPENDENCIES = "/project/build/plugins/plugin[dependencies]";

  public static final String STANDALONE_DEPENDENCIES = "/dependencies";
  public static final String STANDALONE_PLUGINS = "/plugins";

  private XPathExpressions() {}
}
