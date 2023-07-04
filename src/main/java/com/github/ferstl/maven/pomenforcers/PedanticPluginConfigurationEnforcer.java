/*
 * Copyright (c) 2012 - 2020 the original author or authors.
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

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import com.github.ferstl.maven.pomenforcers.model.PluginModel;
import com.github.ferstl.maven.pomenforcers.util.CommaSeparatorUtils;
import static com.github.ferstl.maven.pomenforcers.ErrorReport.toList;

/**
 * Enforces that plugin versions, configurations and dependencies are defined in the
 * <code>&lt;pluginManagement&gt;</code> section. Plugin <code>&lt;executions&gt;</code> can still
 * be configured in the <code>&lt;plugins&gt;</code> section if this enforcer is active.
 * <pre>
 * ### Example
 *     &lt;rules&gt;
 *       &lt;pluginConfiguration implementation=&quot;com.github.ferstl.maven.pomenforcers.PedanticPluginConfigurationEnforcer&quot;&gt;
 *         &lt;!-- all plugin versions have to be defined in plugin managment. --&gt;
 *         &lt;manageVersions&gt;true&lt;/manageVersions&gt;
 *         &lt;!-- allow property references such as ${project.version} as versions outside plugin management --&gt;
 *         &lt;allowUnmanagedProjectVersions&gt;true&lt;/allowUnmanagedProjectVersions&gt;
 *         &lt;!-- set the allowed property names for the allowUnmanagedProjectVersions option --&gt;
 *         &lt;allowedUnmanagedProjectVersionProps&gt;some-property.version,some-other.version&lt;/allowedUnmanagedProjectVersionProps&gt;
 *         &lt;!-- plugin configuration (except execution configuration) has to be defined in plugin management. --&gt;
 *         &lt;manageConfigurations&gt;true&lt;/manageConfigurations&gt;
 *         &lt;!-- plugin dependencies may be defined in the &lt;plugins&gt; section. --&gt;
 *         &lt;manageDependencies&gt;false&lt;/manageDependencies&gt;
 *       &lt;/pluginConfiguration&gt;
 *     &lt;/rules&gt;
 * </pre>
 *
 * @id {@link PedanticEnforcerRule#PLUGIN_CONFIGURATION}
 * @since 1.0.0
 */
public class PedanticPluginConfigurationEnforcer extends AbstractPedanticEnforcer {

  /**
   * If enabled, plugin versions have to be declared in <code>&lt;pluginManagement&gt;</code>.
   */
  private boolean manageVersions;

  /**
   * Allow property references such as <code>${project.version}</code> or <code>${version}</code> as plugin version.
   */
  private boolean allowUnmanagedProjectVersions;

  /**
   * Controls the allowed property references for the allowUnmanagedProjectVersions option.
   */
  private final Set<String> allowedUnmanagedProjectVersionProps;

  /**
   * A sane default set of allowed property references for the allowUnmanagedProjectVersions option.
   */
  private static final Set<String> DEFAULT_ALLOWED_PROPS = new HashSet<>(Arrays.asList("${version}", "${project.version}"));

  /**
   * If enabled, non-execution-bound plugin configurations have to be declared in <code>&lt;pluginManagement&gt;</code>.
   */
  private boolean manageConfigurations;

  /**
   * If enabled, plugin dependencies have to be declared in <code>&lt;pluginManagement&gt;</code>.
   */
  private boolean manageDependencies;

  public PedanticPluginConfigurationEnforcer() {
    this.manageVersions = true;
    this.allowUnmanagedProjectVersions = true;
    this.allowedUnmanagedProjectVersionProps = new HashSet<>(DEFAULT_ALLOWED_PROPS);
    this.manageConfigurations = true;
    this.manageDependencies = true;
  }

  /**
   * Enforces plugin versions to be defined in <code>&lt;pluginManagement&gt;</code>.
   *
   * @param manageVersions Enforces plugin versions to be defined in <code>&lt;pluginManagement&gt;</code>.
   * @configParam
   * @default <code>true</code>
   * @since 1.0.0
   */
  public void setManageVersions(boolean manageVersions) {
    this.manageVersions = manageVersions;
  }

  /**
   * If set to <code>true</code>, <code><version>${project.version}</version></code> may be used within
   * the <code>pluginManagement</code> section.
   *
   * @param allowUnmanagedProjectVersions Allow project versions outside of the <code>&lt;pluginManagement&gt;</code> section.
   * @configParam
   * @default <code>true</code>
   * @since 2.2.0
   */
  public void setAllowUnmanagedProjectVersions(boolean allowUnmanagedProjectVersions) {
    this.allowUnmanagedProjectVersions = allowUnmanagedProjectVersions;
  }

  /**
   * Comma-separated list of Maven property variable names (without the ${...} decorators) which are allowed to be used
   * as version references outside <code>pluginManagement</code>. Has no effect if <code>allowUnmanagedProjectVersions</code>
   * is set to <code>false</code>.
   *
   * @param allowedUnmanagedProjectVersionProps Set allowed property references for allowUnmanagedProjectVersions option.
   * @configParam
   * @default <code>version,project-version</code>
   * @since 2.2.0
   */
  public void setAllowedUnmanagedProjectVersionProps(String allowedUnmanagedProjectVersionProps) {
    CommaSeparatorUtils.splitAndAddToCollection(
            allowedUnmanagedProjectVersionProps,
            this.allowedUnmanagedProjectVersionProps,
            prop -> String.format("${%s}", prop));
  }

  /**
   * Enforces plugin <code>configuration</code> to be defined in <code>&lt;pluginManagement&gt;</code>.
   *
   * @param manageConfigurations Enforces plugin <code>configuration</code> to be defined in <code>&lt;pluginManagement&gt;</code>.
   * @configParam
   * @default <code>true</code>
   * @since 1.0.0
   */
  public void setManageConfigurations(boolean manageConfigurations) {
    this.manageConfigurations = manageConfigurations;
  }

  /**
   * Enforces plugin dependencies to be defined in <code>&lt;pluginManagement&gt;</code>.
   *
   * @param manageDependencies Enforces plugin <code>&lt;dependencies&gt;</code> to be defined in
   * <code>&lt;pluginManagement&gt;</code>.
   * @configParam
   * @default <code>true</code>
   * @since 1.0.0
   */
  public void setManageDependencies(boolean manageDependencies) {
    this.manageDependencies = manageDependencies;
  }

  @Override
  protected PedanticEnforcerRule getDescription() {
    return PedanticEnforcerRule.PLUGIN_CONFIGURATION;
  }

  @Override
  protected void accept(PedanticEnforcerVisitor visitor) {
    visitor.visit(this);
  }

  @Override
  protected void doEnforce(ErrorReport report) {
    if (this.manageVersions) {
      enforceManagedVersions(report);
    }

    if (this.manageConfigurations) {
      enforceManagedConfiguration(report);
    }

    if (this.manageDependencies) {
      enforceManagedDependencies(report);
    }
  }

  private void enforceManagedVersions(ErrorReport report) {
    Collection<PluginModel> versionedPlugins = searchForPlugins(plugin -> plugin.getVersion() != null);

    // Filter all project versions if allowed
    if (this.allowUnmanagedProjectVersions) {
      versionedPlugins = versionedPlugins.stream()
              .filter(plugin -> !allowedUnmanagedProjectVersionProps.contains(plugin.getVersion()))
              .collect(Collectors.toList());
    }

    if (!versionedPlugins.isEmpty()) {
      report.addLine("Plugin versions have to be declared in <pluginManagement>:")
          .addLine(toList(versionedPlugins));
    }

  }

  private void enforceManagedConfiguration(ErrorReport report) {
    Collection<PluginModel> configuredPlugins = searchForPlugins(plugin -> plugin.isConfigured());
    if (!configuredPlugins.isEmpty()) {
      report.addLine("Use <pluginManagement> to configure these plugins or configure them for a specific <execution>:")
          .addLine(toList(configuredPlugins));
    }
  }

  private void enforceManagedDependencies(ErrorReport report) {
    Collection<PluginModel> pluginsWithDependencies = searchForPlugins(plugin -> !plugin.getDependencies().isEmpty());
    if (!pluginsWithDependencies.isEmpty()) {
      report.addLine("Use <pluginManagement> to configure plugin dependencies:")
          .addLine(toList(pluginsWithDependencies));
    }
  }

  private Collection<PluginModel> searchForPlugins(Predicate<PluginModel> predicate) {
    List<PluginModel> plugins = getProjectModel().getPlugins();
    return plugins.stream().filter(predicate).collect(Collectors.toList());
  }
}
