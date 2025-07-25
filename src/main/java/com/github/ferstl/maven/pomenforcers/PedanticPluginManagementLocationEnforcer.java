/*
 * Copyright (c) 2012 - 2023 the original author or authors.
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

import static com.github.ferstl.maven.pomenforcers.ErrorReport.toList;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluator;

import com.github.ferstl.maven.pomenforcers.model.ArtifactModel;
import com.github.ferstl.maven.pomenforcers.model.functions.StringToArtifactTransformer;
import com.github.ferstl.maven.pomenforcers.util.CommaSeparatorUtils;


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
 *
 * @id {@link PedanticEnforcerRule#PLUGIN_MANAGEMENT_LOCATION}
 * @since 1.0.0
 */
@Named("pluginManagemenLocation")
public class PedanticPluginManagementLocationEnforcer extends AbstractPedanticEnforcer {

  private boolean allowParentPoms;
  private final Set<ArtifactModel> pluginManagingPoms;

  @Inject
  public PedanticPluginManagementLocationEnforcer(final MavenProject project, final ExpressionEvaluator helper) {
	super(project, helper);
    this.allowParentPoms = false;
    this.pluginManagingPoms = new HashSet<>();
  }

  @Override
  protected PedanticEnforcerRule getDescription() {
    return PedanticEnforcerRule.PLUGIN_MANAGEMENT_LOCATION;
  }

  @Override
  protected void accept(PedanticEnforcerVisitor visitor) {
    visitor.visit(this);
  }

  @Override
  protected void doEnforce(ErrorReport report) {
    MavenProject mavenProject = getMavenProject();
    if (containsPluginManagement() && !isPluginManagementAllowed(mavenProject)) {
      report.addLine("Only these POMs are allowed to manage plugins:")
          .addLine(toList(Collections.singletonList("All parent POMs, i.e. POMs with <packaging>pom</packaging>")))
          .addLine(toList(this.pluginManagingPoms));
    }
  }

  /**
   * Indicates whether parent POMs are generally allowed to manage plugins.
   *
   * @param allowParentPoms
   * @configParam
   * @default <code>false</code>
   * @since 1.2.0
   */
  public void setAllowParentPoms(boolean allowParentPoms) {
    this.allowParentPoms = allowParentPoms;
  }

  /**
   * Comma separated list of POMs that may declare <code>&lt;pluginManagement&gt;</code>. Each POM has
   * to be defined in the format <code>groupId:artifactId</code>.
   *
   * @param pluginManagingPoms Comma separated list of POMs that may declare plugin management.
   * @configParam
   * @default n/a
   * @since 1.0.0
   */
  public void setPluginManagingPoms(String pluginManagingPoms) {
    CommaSeparatorUtils.splitAndAddToCollection(pluginManagingPoms, this.pluginManagingPoms, StringToArtifactTransformer::toArtifactModel);
  }

  private boolean containsPluginManagement() {
    return !getProjectModel().getManagedPlugins().isEmpty();
  }

  private boolean isPluginManagementAllowed(MavenProject project) {
    return isPluginManagementAllowedInParentPom(project)
        || isPluginManagingProject(project);
  }

  private boolean isPluginManagementAllowedInParentPom(MavenProject project) {
    return this.allowParentPoms && "pom".equals(project.getPackaging());
  }

  private boolean isPluginManagingProject(MavenProject project) {
    ArtifactModel projectInfo = new ArtifactModel(project.getGroupId(), project.getArtifactId(), project.getVersion());
    return this.pluginManagingPoms.isEmpty() || this.pluginManagingPoms.contains(projectInfo);
  }
}
