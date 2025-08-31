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

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluator;
import com.github.ferstl.maven.pomenforcers.model.DependencyModel;
import com.github.ferstl.maven.pomenforcers.util.CommaSeparatorUtils;
import com.google.common.collect.ImmutableSet;
import static com.github.ferstl.maven.pomenforcers.ErrorReport.toList;

/**
 * This enforcer makes sure that dependency versions and exclusions are declared in the
 * <code>&lt;dependencyManagement&gt;</code> section.
 * <pre>
 * ### Example
 *     &lt;rules&gt;
 *       &lt;dependencConfiguration implementation=&quot;com.github.ferstl.maven.pomenforcers.PedanticDependencyConfigurationEnforcer&quot;&gt;
 *         &lt;!-- Manage dependency versions in dependency management --&gt;
 *         &lt;manageVersions&gt;true&lt;/manageVersions&gt;
 *         &lt;!-- allow property references such as ${project.version} as versions outside dependency management --&gt;
 *         &lt;allowUnmanagedProjectVersions&gt;true&lt;/allowUnmanagedProjectVersions&gt;
 *         &lt;!-- set the allowed property names for the allowUnmanagedProjectVersions option --&gt;
 *         &lt;allowedUnmanagedProjectVersionProperties&gt;some-property.version,some-other.version&lt;/allowedUnmanagedProjectVersionProperties&gt;
 *         &lt;!-- all dependency exclusions must be defined in dependency managment --&gt;
 *         &lt;manageExclusions&gt;true&lt;/manageExclusions&gt;
 *       &lt;/dependencyConfiguration&gt;
 *     &lt;/rules&gt;
 * </pre>
 *
 * @id {@link PedanticEnforcerRule#DEPENDENCY_CONFIGURATION}
 * @since 1.0.0
 */
@Named("dependencyConfiguration")
public class PedanticDependencyConfigurationEnforcer extends AbstractPedanticEnforcer {

  /**
   * If enabled, dependency versions have to be declared in <code>&lt;dependencyManagement&gt;</code>.
   */
  private boolean manageVersions = true;

  /**
   * Allow property references such as <code>${project.version}</code> or <code>${version}</code> as dependency version.
   */
  private boolean allowUnmanagedProjectVersions = true;

  /**
   * Controls the allowed property references for the allowUnmanagedProjectVersions option.
   */
  private final Set<String> allowedUnmanagedProjectVersionProperties = new HashSet<>(DEFAULT_ALLOWED_VERSION_PROPERTIES);

  /**
   * A sane default set of allowed property references for the allowUnmanagedProjectVersions option.
   */
  private static final Set<String> DEFAULT_ALLOWED_VERSION_PROPERTIES = ImmutableSet.of("${version}", "${project.version}");

  /**
   * If enabled, dependency exclusions have to be declared in <code>&lt;dependencyManagement&gt;</code>.
   */
  private boolean manageExclusions = true;

  @Inject
  public PedanticDependencyConfigurationEnforcer(final MavenProject project, final ExpressionEvaluator helper) {
    super(project, helper);
  }

  /**
   * If set to <code>true</code>, all dependency versions have to be defined in the dependency management.
   *
   * @param manageVersions Manage dependency versions in the dependency management.
   * @configParam
   * @default <code>true</code>
   * @since 1.0.0
   */
  public void setManageVersions(boolean manageVersions) {
    this.manageVersions = manageVersions;
  }

  /**
   * If set to <code>true</code>, <code><version>${project.version}</version></code> may be used within
   * the dependencies section.
   *
   * @param allowUnmanagedProjectVersions Allow project versions outside of the dependencies section.
   * @configParam
   * @default <code>true</code>
   * @since 1.0.0
   */
  public void setAllowUnmanagedProjectVersions(boolean allowUnmanagedProjectVersions) {
    this.allowUnmanagedProjectVersions = allowUnmanagedProjectVersions;
  }

  /**
   * Comma-separated list of Maven property variable names (without the ${...} decorators) which are allowed to be used
   * as version references outside dependency management. Has no effect if <code>allowUnmanagedProjectVersions</code>
   * is set to <code>false</code>.
   *
   * @param allowedUnmanagedProjectVersionProperties Set allowed property references for allowUnmanagedProjectVersions option.
   * @configParam
   * @default <code>project.version,version</code>
   * @since 2.2.0
   */
  public void setAllowedUnmanagedProjectVersionProperties(String allowedUnmanagedProjectVersionProperties) {
    CommaSeparatorUtils.splitAndAddToCollection(
        allowedUnmanagedProjectVersionProperties,
        this.allowedUnmanagedProjectVersionProperties,
        property -> String.format("${%s}", property));
  }

  /**
   * If set to <code>true</code>, all dependency exclusions must be declared in the dependency management.
   *
   * @param manageExclusions Manage exclusion in dependency management.
   * @configParam
   * @default <code>true</code>
   * @since 1.0.0
   */
  public void setManageExclusions(boolean manageExclusions) {
    this.manageExclusions = manageExclusions;
  }

  @Override
  protected PedanticEnforcerRule getDescription() {
    return PedanticEnforcerRule.DEPENDENCY_CONFIGURATION;
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

    if (this.manageExclusions) {
      enforceManagedExclusion(report);
    }
  }

  private void enforceManagedVersions(ErrorReport report) {
    Collection<DependencyModel> versionedDependencies = searchForDependencies(dep -> dep.getVersion() != null);

    // Filter all project versions if allowed
    if (this.allowUnmanagedProjectVersions) {
      versionedDependencies = versionedDependencies.stream()
          .filter(dep -> !this.allowedUnmanagedProjectVersionProperties.contains(dep.getVersion()))
          .collect(Collectors.toList());
    }

    if (!versionedDependencies.isEmpty()) {
      report.addLine("Dependency versions have to be declared in <dependencyManagement>:")
          .addLine(toList(versionedDependencies));
    }
  }

  private void enforceManagedExclusion(ErrorReport report) {
    Collection<DependencyModel> depsWithExclusions = searchForDependencies(dep -> !dep.getExclusions().isEmpty());

    if (!depsWithExclusions.isEmpty()) {
      report.addLine("Dependency exclusions have to be declared in <dependencyManagement>:")
          .addLine(toList(depsWithExclusions));
    }
  }

  private Collection<DependencyModel> searchForDependencies(Predicate<DependencyModel> predicate) {
    List<DependencyModel> dependencies = getProjectModel().getDependencies();
    return dependencies.stream().filter(predicate).collect(Collectors.toList());
  }
}
