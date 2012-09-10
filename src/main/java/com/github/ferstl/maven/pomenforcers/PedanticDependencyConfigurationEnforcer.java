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

import com.github.ferstl.maven.pomenforcers.model.DependencyModel;
import com.github.ferstl.maven.pomenforcers.reader.PomSerializer;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

/**
 * This enforcer makes sure that dependency versions and exclusions are declared in the
 * <code>&lt;dependencyManagement&gt;</code> section.
 *
 * <pre>
 * ### Example
 *     &lt;rules&gt;
 *       &lt;dependencConfiguration implementation=&quot;com.github.ferstl.maven.pomenforcers.PedanticDependencyConfigurationEnforcer&quot;&gt;
 *         &lt;!-- Manage dependency versions in dependency management --&gt;
 *         &lt;manageVersions&gt;true&lt;/manageVersions&gt;
 *         &lt;!-- allow ${project.version} outside dependency management --&gt;
 *         &lt;allowUnmanagedProjectVersions&gt;true&lt;/allowUnmanagedProjectVersions&gt;
 *         &lt;!-- all dependency exclusions must be defined in dependency managment --&gt;
 *         &lt;manageExclusions&gt;true&lt;/manageExclusions&gt;
 *       &lt;/dependencyConfiguration&gt;
 *     &lt;/rules&gt;
 * </pre>
 *
 * @id {@link PedanticEnforcerRule#DEPENDENCY_ORDER}
 */
public class PedanticDependencyConfigurationEnforcer extends AbstractPedanticEnforcer {

  /** If enabled, dependency versions have to be declared in <code>&lt;dependencyManagement&gt;</code>. */
  private boolean manageVersions = true;

  /** Allow <code>${project.version}</code> or ${version} as dependency version. */
  private boolean allowUnmangedProjectVersions = true;

  /** If enabled, dependency exclusions have to be declared in <code>&lt;dependencyManagement&gt;</code>. */
  private boolean manageExclusions = true;

  /**
   * If set to <code>true</code>, all dependency versions have to be defined in the dependency management.
   * @param manageVersions Manage dependency versions in the dependency management.
   * @configParam
   * @default <code>true</code>
   */
  public void setManageVersions(boolean manageVersions) {
    this.manageVersions = manageVersions;
  }

  /**
   * If set to <code>true</code>, <code><version>${project.version}</version></code> may be used within
   * the dependencies section.
   * @param allowUnmangedProjectVersions Allow project versions outside of the dependencies section.
   * @configParam
   * @default <code>true</code>
   */
  public void setAllowUnmanagedProjectVersions(boolean allowUnmangedProjectVersions) {
    this.allowUnmangedProjectVersions = allowUnmangedProjectVersions;
  }

  /**
   * If set to <code>true</code>, all dependency exclusions must be declared in the dependency management.
   * @param manageExclusions Manage exclusion in dependency management.
   * @configParam
   * @default <code>true</code>
   */
  public void setManageExclusions(boolean manageExclusions) {
    this.manageExclusions = manageExclusions;
  }

  @Override
  protected void doEnforce(EnforcerRuleHelper helper, Document pom) throws EnforcerRuleException {
    Log log = helper.getLog();

    if (this.manageVersions) {
      log.info("Enforcing managed dependency versions");
      enforceManagedVersions(pom);
    }
    if (this.manageExclusions) {
      log.info("Enforcing managed dependency exclusions");
      enforceManagedExclusion(pom);
    }
  }

  private void enforceManagedVersions(Document pom) throws EnforcerRuleException {
    Collection<DependencyModel> versionedDependencies =
        searchForDependencies(pom, new DependencyVersionPredicate());

    // Filter all project versions if allowed
    if (this.allowUnmangedProjectVersions) {
      versionedDependencies = Collections2.filter(versionedDependencies, new DependencyWithProjectVersionPredicate());
    }

    if (versionedDependencies.size() > 0) {
      throw new EnforcerRuleException("One does not simply set versions on dependencies. Dependency versions have " +
          "to be declared in <dependencyManagement>: " + versionedDependencies);
    }
  }

  private void enforceManagedExclusion(Document pom) throws EnforcerRuleException {
    Collection<DependencyModel> depsWithExclusions =
        searchForDependencies(pom, new DependencyWithExclusionPredicate());

    if (depsWithExclusions.size() > 0) {
      throw new EnforcerRuleException("One does not simply define exclusions on dependencies. Dependency exclusions " +
           "have to be declared in <dependencyManagement>: " + depsWithExclusions);
    }
  }

  private Collection<DependencyModel> searchForDependencies(Document pom, Predicate<DependencyModel> predicate) {
    List<DependencyModel> dependencies = new PomSerializer(pom).read().getDependencies();
    return Collections2.filter(dependencies, predicate);
  }

  @Override
  protected void accept(PedanticEnforcerVisitor visitor) {
    visitor.visit(this);
  }

  private static class DependencyWithExclusionPredicate implements Predicate<DependencyModel> {
    @Override
    public boolean apply(DependencyModel input) {
      return !input.getExclusions().isEmpty();
    }
  }

  private static class DependencyVersionPredicate implements Predicate<DependencyModel> {
    @Override
    public boolean apply(DependencyModel input) {
      return input.getVersion() != null;
    }
  }

  private static class DependencyWithProjectVersionPredicate implements Predicate<DependencyModel> {
    @Override
    public boolean apply(DependencyModel input) {
      return !"${project.version}".equals(input.getVersion())
          && !"${version}".equals(input.getVersion());
    }
  }
}
