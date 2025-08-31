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
import static com.github.ferstl.maven.pomenforcers.util.CommaSeparatorUtils.splitAndAddToCollection;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluator;

import com.github.ferstl.maven.pomenforcers.model.ArtifactModel;
import com.github.ferstl.maven.pomenforcers.model.functions.StringToArtifactTransformer;

/**
 * Enforces that only a well-defined set of POMs may declare dependency management.
 * <pre>
 * ### Example
 *     &lt;rules&gt;
 *       &lt;dependencyManagementLocation implementation=&quot;com.github.ferstl.maven.pomenforcers.PedanticDependencyManagementLocationEnforcer&quot;&gt;
 *         &lt;!-- Only these POMs may declare dependency management --&gt;
 *         &lt;dependencyManagingPoms&gt;com.example.myproject:parent,com.example.myproject:subparent&lt;/dependencyManagingPoms&gt;
 *       &lt;/dependencyManagementLocation&gt;
 *     &lt;/rules&gt;
 * </pre>
 *
 * @id {@link PedanticEnforcerRule#DEPENDENCY_MANAGEMENT_LOCATION}
 * @since 1.0.0
 */
@Named("dependencyManagementLocation")
public class PedanticDependencyManagementLocationEnforcer extends AbstractPedanticEnforcer {

  private boolean allowParentPoms;
  private final Set<ArtifactModel> dependencyManagingPoms;

  @Inject
  public PedanticDependencyManagementLocationEnforcer(final MavenProject project, final ExpressionEvaluator helper) {
	super(project, helper);
    this.allowParentPoms = false;
    this.dependencyManagingPoms = new HashSet<>();
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
   * Comma separated list of POMs that may declare <code>&lt;dependencyManagement&gt;</code>.
   * Each POM has to be defined in the format <code>groupId:artifactId</code>.
   *
   * @param dependencyManagingPoms Comma separated list of POMs that may declare plugin management.
   * @configParam
   * @default n/a
   * @since 1.0.0
   */
  public void setDependencyManagingPoms(String dependencyManagingPoms) {
    splitAndAddToCollection(dependencyManagingPoms, this.dependencyManagingPoms, StringToArtifactTransformer::toArtifactModel);
  }

  @Override
  protected PedanticEnforcerRule getDescription() {
    return PedanticEnforcerRule.DEPENDENCY_MANAGEMENT_LOCATION;
  }

  @Override
  protected void accept(PedanticEnforcerVisitor visitor) {
    visitor.visit(this);
  }

  @Override
  protected void doEnforce(ErrorReport report) {
    MavenProject mavenProject = getMavenProject();
    if (containsDependencyManagement() && !isDependencyManagementAllowed(mavenProject)) {
      report.addLine("Only these POMs are allowed to manage dependencies:")
          .addLine(toList(Collections.singletonList("All parent POMs, i.e. POMs with <packaging>pom</packaging>")))
          .addLine(toList(this.dependencyManagingPoms));
    }
  }

  private boolean containsDependencyManagement() {
    return !getProjectModel().getManagedDependencies().isEmpty();
  }

  private boolean isDependencyManagementAllowed(MavenProject project) {
    return isDependencyManagementAllowedInParentPom(project)
        || isDependencyManagingProject(project);
  }

  private boolean isDependencyManagementAllowedInParentPom(MavenProject project) {
    return this.allowParentPoms && "pom".equals(project.getPackaging());
  }

  private boolean isDependencyManagingProject(MavenProject project) {
    ArtifactModel projectInfo = new ArtifactModel(project.getGroupId(), project.getArtifactId(), project.getVersion());
    return this.dependencyManagingPoms.isEmpty() || this.dependencyManagingPoms.contains(projectInfo);

  }

}
