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

import java.util.HashSet;
import java.util.Set;

import org.apache.maven.enforcer.rule.api.EnforcerRuleException;
import org.apache.maven.project.MavenProject;

import com.github.ferstl.maven.pomenforcers.model.ArtifactModel;
import com.github.ferstl.maven.pomenforcers.util.CommaSeparatorUtils;
import com.github.ferstl.maven.pomenforcers.util.EnforcerRuleUtils;

import static com.github.ferstl.maven.pomenforcers.model.functions.StringToArtifactTransformer.stringToArtifactModel;


/**
 * Enforces that only a well-defined set of POMs may declare plugin management.
 * <pre>
 * ### Example
 *     &lt;rules&gt;
 *       &lt;pluginManagemenLocation implementation=&quot;com.github.ferstl.maven.pomenforcers.PedanticPluginManagementLocationEnforcer&quot;&gt;
 *         &lt;!-- Only these POMs may declare plugin management --&gt;
 *         &lt;pluginManagingPoms&gt;com.example.myproject:parent,com.example.myproject:subparent&lt;/pluginManagingPoms&gt;
 *       &lt;/pluginManagemenLocation&gt;
 *     &lt;/rules&gt;
 * </pre>
 * @id {@link PedanticEnforcerRule#PLUGIN_MANAGEMENT_LOCATION}
 */
public class PedanticPluginManagementLocationEnforcer extends AbstractPedanticEnforcer {

  private final Set<ArtifactModel> pluginManagingPoms;

  public PedanticPluginManagementLocationEnforcer() {
    this.pluginManagingPoms = new HashSet<>();
  }

  @Override
  protected void doEnforce() throws EnforcerRuleException {
    MavenProject mavenProject = EnforcerRuleUtils.getMavenProject(getHelper());
    if (containsPluginManagement() && !isPluginManagementAllowed(mavenProject)) {
      throw new EnforcerRuleException("One does not simply declare plugin management. " +
      		"Only these POMs are allowed to manage plugins: " + this.pluginManagingPoms);
    }
  }

  /**
   * Comma separated list of POMs that may declare <code>&lt;pluginManagement&gt;</code>. Each POM has
   * to be defined in the format <code>groupId:artifactId</code>.
   * @param pluginManagingPoms Comma separated list of POMs that may declare plugin management.
   * @configParam
   * @default n/a
   */
  public void setPluginManagingPoms(String pluginManagingPoms) {
    CommaSeparatorUtils.splitAndAddToCollection(pluginManagingPoms, this.pluginManagingPoms, stringToArtifactModel());
  }

  private boolean containsPluginManagement() {
    return !getProjectModel().getManagedPlugins().isEmpty();
  }

  private boolean isPluginManagementAllowed(MavenProject project) {
    ArtifactModel projectInfo = new ArtifactModel(project.getGroupId(), project.getArtifactId(), project.getVersion());
    return this.pluginManagingPoms.contains(projectInfo);
  }

  @Override
  protected void accept(PedanticEnforcerVisitor visitor) {
    visitor.visit(this);
  }
}
