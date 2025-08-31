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

import static com.github.ferstl.maven.pomenforcers.model.DependencyScope.COMPILE;
import static com.github.ferstl.maven.pomenforcers.model.DependencyScope.IMPORT;
import static com.github.ferstl.maven.pomenforcers.model.DependencyScope.PROVIDED;
import static com.github.ferstl.maven.pomenforcers.model.DependencyScope.RUNTIME;
import static com.github.ferstl.maven.pomenforcers.model.DependencyScope.SYSTEM;
import static com.github.ferstl.maven.pomenforcers.model.DependencyScope.TEST;
import static java.util.stream.Collectors.toSet;

import java.util.Collection;
import java.util.Map.Entry;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.maven.model.Dependency;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluator;

import com.github.ferstl.maven.pomenforcers.model.ArtifactModel;
import com.github.ferstl.maven.pomenforcers.model.DependencyScope;
import com.github.ferstl.maven.pomenforcers.model.functions.StringToArtifactTransformer;
import com.github.ferstl.maven.pomenforcers.util.CommaSeparatorUtils;
import com.google.common.base.Joiner;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;


/**
 * Enforces that the configured dependencies have to be defined within a specific scope.
 * Wildcards are supported in the following formats (see example below):
 * - Full wildcard: &quot;*&quot;, matches everything
 * - Leading wildcard: &quot;*foo&quot;, matches everything that ends with &quot;foo&quot;
 * - Trailing wildcard: &quot;foo*&quot;, matches everything that starts with &quot;foo&quot;
 * - Containing wildcard: &quot;*foo*&quot;, matches everything that contains with &quot;foo&quot;
 * <pre>
 * ### Example
 *     &lt;rules&gt;
 *       &lt;dependencyScope implementation=&quot;com.github.ferstl.maven.pomenforcers.PedanticDependencyScopeEnforcer&quot;&gt;
 *         &lt;!-- These dependencies can only be defined in test scope --&gt;
 *         &lt;testDependencies&gt;junit:junit,org.hamcrest:*,org.mockito:mockito-core&lt;/testDependencies&gt;
 *         &lt;!-- These dependencies can only be defined in provided scope --&gt;
 *         &lt;providedDependencies&gt;javax.servlet:servlet-api&lt;/providedDependencies&gt;
 *       &lt;/dependencyScope&gt;
 *     &lt;/rules&gt;
 * </pre>
 *
 * @id {@link PedanticEnforcerRule#DEPENDENCY_SCOPE}
 * @since 1.0.0
 */
@Named("dependencyScope")
public class PedanticDependencyScopeEnforcer extends AbstractPedanticEnforcer {

  private final Multimap<DependencyScope, ArtifactModel> scopedDependencies;

  @Inject
  public PedanticDependencyScopeEnforcer(final MavenProject project, final ExpressionEvaluator helper) {
	super(project, helper);
    this.scopedDependencies = HashMultimap.create();
  }

  /**
   * Comma-separated list of <code>compile</code> scope dependencies in the format <code>groupId:artifactId</code>.
   *
   * @param compileDependencies Comma-separated list of <code>compile</code> scope dependencies.
   * @configParam
   * @since 1.0.0
   */
  public void setCompileDependencies(String compileDependencies) {
    addToArtifactMap(createDependencyInfo(compileDependencies), COMPILE);
  }

  /**
   * Comma-separated list of <code>provided</code> scope dependencies in the format <code>groupId:artifactId</code>.
   *
   * @param providedDependencies Comma-separated list of <code>provided</code> scope dependencies.
   * @configParam
   * @since 1.0.0
   */
  public void setProvidedDependencies(String providedDependencies) {
    addToArtifactMap(createDependencyInfo(providedDependencies), PROVIDED);
  }

  /**
   * Comma-separated list of <code>runtime</code> scope dependencies in the format <code>groupId:artifactId</code>.
   *
   * @param runtimeDependencies Comma-separated list of <code>runtime</code> scope dependencies.
   * @configParam
   * @since 1.0.0
   */
  public void setRuntimeDependencies(String runtimeDependencies) {
    addToArtifactMap(createDependencyInfo(runtimeDependencies), RUNTIME);
  }

  /**
   * Comma-separated list of <code>system</code> scope dependencies in the format <code>groupId:artifactId</code>.
   *
   * @param systemDependencies Comma-separated list of <code>system</code> scope dependencies.
   * @configParam
   * @since 1.0.0
   */
  public void setSystemDependencies(String systemDependencies) {
    addToArtifactMap(createDependencyInfo(systemDependencies), SYSTEM);
  }

  /**
   * Comma-separated list of <code>test</code> scope dependencies in the format <code>groupId:artifactId</code>.
   *
   * @param testDependencies Comma-separated list of <code>test</code> scope dependencies.
   * @configParam
   * @since 1.0.0
   */
  public void setTestDependencies(String testDependencies) {
    addToArtifactMap(createDependencyInfo(testDependencies), TEST);
  }

  /**
   * Comma-separated list of <code>import</code> scope dependencies in the format <code>groupId:artifactId</code>.
   *
   * @param importDependencies Comma-separated list of <code>import</code> scope dependencies.
   * @configParam
   * @since 1.0.0
   */
  public void setImportDependencies(String importDependencies) {
    addToArtifactMap(createDependencyInfo(importDependencies), IMPORT);
  }

  @Override
  protected PedanticEnforcerRule getDescription() {
    return PedanticEnforcerRule.DEPENDENCY_SCOPE;
  }

  @Override
  protected void accept(PedanticEnforcerVisitor visitor) {
    visitor.visit(this);
  }

  @Override
  protected void doEnforce(ErrorReport report) {
    Collection<Dependency> dependencies = getMavenProject().getDependencies();

    for (Dependency dependency : dependencies) {
      ArtifactModel artifactModel = new ArtifactModel(dependency.getGroupId(), dependency.getArtifactId(), dependency.getVersion());
      DependencyScope dependencyScope = getScope(dependency);

      Set<DependencyScope> allowedScopes = this.scopedDependencies.entries().stream()
          .filter(entry -> artifactModel.matches(entry.getValue()))
          .map(Entry::getKey)
          .collect(toSet());

      if (!allowedScopes.isEmpty() && !allowedScopes.contains(dependencyScope)) {
        report.formatLine("Allowed Scopes for %s: %s", dependency, Joiner.on(", ").join(allowedScopes));
      }
    }
  }

  private Set<ArtifactModel> createDependencyInfo(String dependencies) {
    Set<ArtifactModel> dependencyInfoSet = Sets.newHashSet();
    CommaSeparatorUtils.splitAndAddToCollection(dependencies, dependencyInfoSet, StringToArtifactTransformer::toArtifactModel);

    return dependencyInfoSet;
  }

  private void addToArtifactMap(Iterable<ArtifactModel> artifactModels, DependencyScope scope) {
    for (ArtifactModel artifactModel : artifactModels) {
      this.scopedDependencies.put(scope, artifactModel);
    }
  }

  private DependencyScope getScope(Dependency dependency) {
    if (dependency.getScope() == null) {
      return COMPILE;
    }

    return DependencyScope.getByScopeName(dependency.getScope());
  }
}
