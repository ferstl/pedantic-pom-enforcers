/*
 * Copyright (c) 2012 - 2015 by Stefan Ferstl <st.ferstl@gmail.com>
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
import java.util.Set;

import org.apache.maven.model.Dependency;

import com.github.ferstl.maven.pomenforcers.model.ArtifactModel;
import com.github.ferstl.maven.pomenforcers.model.DependencyScope;
import com.github.ferstl.maven.pomenforcers.util.CommaSeparatorUtils;
import com.github.ferstl.maven.pomenforcers.util.EnforcerRuleUtils;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

import static com.github.ferstl.maven.pomenforcers.model.DependencyScope.COMPILE;
import static com.github.ferstl.maven.pomenforcers.model.DependencyScope.IMPORT;
import static com.github.ferstl.maven.pomenforcers.model.DependencyScope.PROVIDED;
import static com.github.ferstl.maven.pomenforcers.model.DependencyScope.RUNTIME;
import static com.github.ferstl.maven.pomenforcers.model.DependencyScope.SYSTEM;
import static com.github.ferstl.maven.pomenforcers.model.DependencyScope.TEST;
import static com.github.ferstl.maven.pomenforcers.model.functions.StringToArtifactTransformer.stringToArtifactModel;


/**
 * Enforces that the configured dependencies have to be defined within a specific scope.
 * <pre>
 * ### Example
 *     &lt;rules&gt;
 *       &lt;dependencyScope implementation=&quot;com.github.ferstl.maven.pomenforcers.PedanticDependencyScopeEnforcer&quot;&gt;
 *         &lt;!-- These dependencies can only be defined in test scope --&gt;
 *         &lt;testDependencies&gt;junit:junit,org.hamcrest:hamcrest-library,org.mockito:mockito-core&lt;/testDependencies&gt;
 *
 *         &lt;!-- These dependencies can only be defined in provided scope --&gt;
 *         &lt;providedDependencies&gt;javax.servlet:servlet-api&lt;/providedDependencies&gt;
 *       &lt;/dependencyScope&gt;
 *     &lt;/rules&gt;
 * </pre>
 *
 * @id {@link PedanticEnforcerRule#DEPENDENCY_SCOPE}
 * @since 1.0.0
 */
public class PedanticDependencyScopeEnforcer extends AbstractPedanticEnforcer {

  private final Multimap<ArtifactModel, DependencyScope> scopedDependencies;

  public PedanticDependencyScopeEnforcer() {
    this.scopedDependencies = HashMultimap.create();
  }

  /**
   * Comma-separated list of <code>compile</code> scope dependencies in the format <code>groupId:artifactId</code>.
   * @param compileDependencies Comma-separated list of <code>compile</code> scope dependencies.
   * @configParam
   * @since 1.0.0
   */
  public void setCompileDependencies(String compileDependencies) {
    addToArtifactMap(createDependencyInfo(compileDependencies), COMPILE);
  }

  /**
   * Comma-separated list of <code>provided</code> scope dependencies in the format <code>groupId:artifactId</code>.
   * @param providedDependencies Comma-separated list of <code>provided</code> scope dependencies.
   * @configParam
   * @since 1.0.0
   */
  public void setProvidedDependencies(String providedDependencies) {
    addToArtifactMap(createDependencyInfo(providedDependencies), PROVIDED);
  }

  /**
   * Comma-separated list of <code>runtime</code> scope dependencies in the format <code>groupId:artifactId</code>.
   * @param runtimeDependencies Comma-separated list of <code>runtime</code> scope dependencies.
   * @configParam
   * @since 1.0.0
   */
  public void setRuntimeDependencies(String runtimeDependencies) {
    addToArtifactMap(createDependencyInfo(runtimeDependencies), RUNTIME);
  }

  /**
   * Comma-separated list of <code>system</code> scope dependencies in the format <code>groupId:artifactId</code>.
   * @param systemDependencies Comma-separated list of <code>system</code> scope dependencies.
   * @configParam
   * @since 1.0.0
   */
  public void setSystemDependencies(String systemDependencies) {
    addToArtifactMap(createDependencyInfo(systemDependencies), SYSTEM);
  }

  /**
   * Comma-separated list of <code>test</code> scope dependencies in the format <code>groupId:artifactId</code>.
   * @param testDependencies Comma-separated list of <code>test</code> scope dependencies.
   * @configParam
   * @since 1.0.0
   */
  public void setTestDependencies(String testDependencies) {
    addToArtifactMap(createDependencyInfo(testDependencies), TEST);
  }

  /**
   * Comma-separated list of <code>import</code> scope dependencies in the format <code>groupId:artifactId</code>.
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
    Collection<Dependency> dependencies = EnforcerRuleUtils.getMavenProject(getHelper()).getDependencies();

    for (Dependency dependency : dependencies) {
      ArtifactModel artifactModel = DependencyToArtifactTransformer.INSTANCE.apply(dependency);
      Collection<DependencyScope> allowedScopes = this.scopedDependencies.get(artifactModel);
      DependencyScope dependencyScope = getScope(dependency);

      if (!allowedScopes.isEmpty() && !allowedScopes.contains(dependencyScope)) {
        report.formatLine("%s -> %s", dependency, Joiner.on(", ").join(allowedScopes));
      }
    }
  }

  private Set<ArtifactModel> createDependencyInfo(String dependencies) {
    Set<ArtifactModel> dependencyInfoSet = Sets.newHashSet();
    CommaSeparatorUtils.splitAndAddToCollection(dependencies, dependencyInfoSet, stringToArtifactModel());

    return dependencyInfoSet;
  }

  private void addToArtifactMap(Iterable<ArtifactModel> artifactModels, DependencyScope scope) {
    for (ArtifactModel artifactModel : artifactModels) {
      this.scopedDependencies.put(artifactModel, scope);
    }
  }

  private DependencyScope getScope(Dependency dependency) {
    if (dependency.getScope() == null) {
      return COMPILE;
    }
    return DependencyScope.getByScopeName(dependency.getScope());
  }

  private static enum DependencyToArtifactTransformer implements Function<Dependency, ArtifactModel> {
    INSTANCE;

    @Override
    public ArtifactModel apply(Dependency input) {
      return new ArtifactModel(input.getGroupId(), input.getArtifactId(), input.getVersion());
    }

  }
}
