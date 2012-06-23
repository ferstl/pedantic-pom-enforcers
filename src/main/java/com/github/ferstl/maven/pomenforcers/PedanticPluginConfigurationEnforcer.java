package com.github.ferstl.maven.pomenforcers;

import java.util.Collection;

import org.apache.maven.enforcer.rule.api.EnforcerRuleException;
import org.apache.maven.enforcer.rule.api.EnforcerRuleHelper;
import org.apache.maven.model.Plugin;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.github.ferstl.maven.pomenforcers.reader.DeclaredPluginsReader;
import com.github.ferstl.maven.pomenforcers.reader.XPathExpressions;
import com.github.ferstl.maven.pomenforcers.util.XmlUtils;

public class PedanticPluginConfigurationEnforcer extends AbstractPedanticEnforcer {

  private boolean manageVersions = true;
  private boolean manageConfigurations = true;
  private boolean manageDependencies = true;

  public void setManageVersions(boolean manageVersions) {
    this.manageVersions = manageVersions;
  }

  public void setManageConfigurations(boolean manageConfigurations) {
    this.manageConfigurations = manageConfigurations;
  }

  public void setManageDependencies(boolean manageDependencies) {
    this.manageDependencies = manageDependencies;
  }

  @Override
  protected void doEnforce(EnforcerRuleHelper helper, Document pom) throws EnforcerRuleException {
    if (this.manageVersions) {
      enforceManagedVersions(pom);
    }

    if (this.manageConfigurations) {
      enforceManagedConfiguration(pom);
    }

    if (this.manageDependencies) {
      enforceManagedDependencies(pom);
    }
  }

  private void enforceManagedVersions(Document pom) throws EnforcerRuleException {
    Collection<Plugin> versionedPlugins = searchForPlugins(pom, XPathExpressions.POM_VERSIONED_PLUGINS);
    if (versionedPlugins.size() > 0) {
      throw new EnforcerRuleException("One does not simply set versions on plugins. Plugin versions have to " +
      		"be declared in <pluginManagement>: " + versionedPlugins);
    }

  }

  private void enforceManagedConfiguration(Document pom) throws EnforcerRuleException {
    Collection<Plugin> configuredPlugins = searchForPlugins(pom, XPathExpressions.POM_CONFIGURED_PLUGINS);
    if (configuredPlugins.size() > 0) {
      throw new EnforcerRuleException("One does not simply configure plugins. Use <pluginManagement> to configure "
          +	"these plugins or configure them for a specific <execution>: " + configuredPlugins);
    }
  }

  private void enforceManagedDependencies(Document pom) throws EnforcerRuleException {
    Collection<Plugin> configuredPluginDependencies =
        searchForPlugins(pom, XPathExpressions.POM_CONFIGURED_PLUGIN_DEPENDENCIES);
    if (configuredPluginDependencies.size() > 0) {
      throw new EnforcerRuleException("One does not simply configure plugin dependencies. Use <pluginManagement> "
      	+ "to configure plugin dependencies: " + configuredPluginDependencies);
    }
  }

  private Collection<Plugin> searchForPlugins(Document pom, String xpath) {
    NodeList plugins = XmlUtils.evaluateXPathAsNodeList(xpath, pom);
    Document pluginDoc = XmlUtils.createDocument("plugins", plugins);
    return new DeclaredPluginsReader(pluginDoc).read(XPathExpressions.STANDALONE_PLUGINS);
  }

  @Override
  protected void accept(PedanticEnforcerVisitor visitor) {
    visitor.visit(this);
  }

}
