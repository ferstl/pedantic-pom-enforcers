/*
 * Copyright (c) 2012 by The Author(s)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.ferstl.maven.pomenforcers;

import java.util.Collection;
import java.util.List;

import org.apache.maven.enforcer.rule.api.EnforcerRuleException;
import org.apache.maven.enforcer.rule.api.EnforcerRuleHelper;
import org.apache.maven.plugin.logging.Log;
import org.w3c.dom.Document;

import com.github.ferstl.maven.pomenforcers.model.PluginModel;
import com.github.ferstl.maven.pomenforcers.reader.PomSerializer;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

/**
 * Enforces that plugin versions, configurations and dependencies are defined in the
 * <code>&lt;pluginManagement&gt;</code> section. Plugins <code>&lt;executions&gt;</code> can still
 * be configured in the <code>&lt;plugins&gt;</code> section if this enforcer is active.
 * <pre>
 * ### Example
 *     &lt;rules&gt;
 *       &lt;pluginConfiguration implementation=&quot;com.github.ferstl.maven.pomenforcers.PedanticPluginConfigurationEnforcer&quot;&gt;
 *         &lt;!-- all plugin versions have to be defined in plugin managment --&gt;
 *         &lt;manageVersions&gt;true&lt;/manageVersions&gt;
 *         &lt;!-- plugin configuration (except execution configuration) has to be defined in plugin management. --&gt;
 *         &lt;manageConfigurations&gt;true&lt;/anageConfigurations&gt;
 *         &lt;!-- plugin dependencies may be defined in the &lt;plugins&gt; section. --&gt;
 *         &lt;manageDependencies&gt;false&lt;/manageDependencies&gt;
 *       &lt;/pluginConfiguration&gt;
 *     &lt;/rules&gt;
 * </pre>
 * @id {@link PedanticEnforcerRule#PLUGIN_CONFIGURATION}
 */
public class PedanticPluginConfigurationEnforcer extends AbstractPedanticEnforcer {

  private boolean manageVersions = true;
  private boolean manageConfigurations = true;
  private boolean manageDependencies = true;

  /**
   * Enforces plugin versions to be defined in <code>&lt;pluginManagement&gt;</code>.
   * @param manageVersions Enforces plugin versions to be defined in <code>&lt;pluginManagement&gt;</code>.
   * @configParam
   * @default <code>true</code>
   */
  public void setManageVersions(boolean manageVersions) {
    this.manageVersions = manageVersions;
  }

  /**
   * Enforces plugin <code>configuration</code> to be defined in <code>&lt;pluginManagement&gt;</code>.
   * @param manageConfigurations Enforces plugin <code>configuration</code> to be defined in <code>&lt;pluginManagement&gt;</code>.
   * @configParam
   * @default <code>true</code>
   */
  public void setManageConfigurations(boolean manageConfigurations) {
    this.manageConfigurations = manageConfigurations;
  }

  /**
   * Enforces plugin dependencies to be defined in <code>&lt;pluginManagement&gt;</code>.
   * @param manageDependencies Enforces plugin <code>&lt;dependencies&gt;</code> to be defined in
   *        <code>&lt;pluginManagement&gt;</code>.
   * @configParam
   * @default <code>true</code>
   */
  public void setManageDependencies(boolean manageDependencies) {
    this.manageDependencies = manageDependencies;
  }

  @Override
  protected void doEnforce(EnforcerRuleHelper helper, Document pom) throws EnforcerRuleException {
    Log log = helper.getLog();
    if (this.manageVersions) {
      log.info("Enforcing managed plugin versions.");
      enforceManagedVersions(pom);
    }

    if (this.manageConfigurations) {
      log.info("Enforcing managed plugin configurations.");
      enforceManagedConfiguration(pom);
    }

    if (this.manageDependencies) {
      log.info("Enforcing managed plugin dependencies.");
      enforceManagedDependencies(pom);
    }
  }

  private void enforceManagedVersions(Document pom) throws EnforcerRuleException {
    Collection<PluginModel> versionedPlugins = searchForPlugins(pom, new PluginWithVersionPredicate());
    if (versionedPlugins.size() > 0) {
      throw new EnforcerRuleException("One does not simply set versions on plugins. Plugins versions have to " +
      		"be declared in <pluginManagement>: " + versionedPlugins);
    }

  }

  private void enforceManagedConfiguration(Document pom) throws EnforcerRuleException {
    Collection<PluginModel> configuredPlugins = searchForPlugins(pom, new PluginWithConfigurationPredicate());
    if (configuredPlugins.size() > 0) {
      throw new EnforcerRuleException("One does not simply configure plugins. Use <pluginManagement> to configure "
          +	"these plugins or configure them for a specific <execution>: " + configuredPlugins);
    }
  }

  private void enforceManagedDependencies(Document pom) throws EnforcerRuleException {
    Collection<PluginModel> configuredPluginDependencies =
        searchForPlugins(pom, new PluginWithDependenciesPredicate());
    if (configuredPluginDependencies.size() > 0) {
      throw new EnforcerRuleException("One does not simply configure plugin dependencies. Use <pluginManagement> "
      	+ "to configure plugin dependencies: " + configuredPluginDependencies);
    }
  }

  private Collection<PluginModel> searchForPlugins(Document pom, Predicate<PluginModel> predicate) {
    List<PluginModel> plugins = new PomSerializer(pom).read().getPlugins();
    return Collections2.filter(plugins, predicate);
  }

  @Override
  protected void accept(PedanticEnforcerVisitor visitor) {
    visitor.visit(this);
  }

  static class PluginWithDependenciesPredicate implements Predicate<PluginModel> {
    @Override
    public boolean apply(PluginModel input) {
      return !input.getDependencies().isEmpty();
    }
  }

  static class PluginWithConfigurationPredicate implements Predicate<PluginModel> {
    @Override
    public boolean apply(PluginModel input) {
      return input.isConfigured();
    }
  }

  static class PluginWithVersionPredicate implements Predicate<PluginModel> {
    @Override
    public boolean apply(PluginModel input) {
      return input.getVersion() != null;
    }
  }

}
