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

import org.apache.maven.enforcer.rule.api.EnforcerRuleException;
import org.apache.maven.enforcer.rule.api.EnforcerRuleHelper;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.logging.Log;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.github.ferstl.maven.pomenforcers.reader.DeclaredDependenciesReader;
import com.github.ferstl.maven.pomenforcers.reader.XPathExpressions;
import com.github.ferstl.maven.pomenforcers.util.XmlUtils;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

/**
 * This enforcer makes sure that all artifacts in your dependencies section are
 * ordered. The ordering can be defined by any combination of `scope`, `groupId`
 * and `artifactId`. Each of these attributes may be given a priority.
 *
 * <pre>
 * ### Example
 *     <rules>
 *       <dependencConfiguration implementation="com.github.ferstl.maven.pomenforcers.PedanticDependencyConfigurationEnforcer">
 *         <!-- Manage dependency versions in dependency management -->
 *         <manageVersions>true</manageVersions>
 *         <!-- allow ${project.version} outside dependency management -->
 *         <allowUnmanagedProjectVersions>true</allowUnmanagedProjectVersions>
 *         <!-- all dependency exclusions must be defined in dependency managment -->
 *         <manageExclusions>true</manageExclusions>
 *       </dependencyConfiguration>
 *     </rules>
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
    Collection<Dependency> versionedDependencies =
        searchForDependencies(pom, XPathExpressions.POM_VERSIONED_DEPENDENCIES);

    // Filter all project versions if allowed
    if (this.allowUnmangedProjectVersions) {
      versionedDependencies = Collections2.filter(versionedDependencies, new ProjectVersionPredicate());
    }

    if (versionedDependencies.size() > 0) {
      throw new EnforcerRuleException("One does not simply set versions on dependencies. Dependency versions have " +
          "to be declared in <dependencyManagement>: " + versionedDependencies);
    }
  }

  private void enforceManagedExclusion(Document pom) throws EnforcerRuleException {
    Collection<Dependency> depsWithExclusions =
        searchForDependencies(pom, XPathExpressions.POM_DEPENDENCIES_WITH_EXCLUSIONS);

    if (depsWithExclusions.size() > 0) {
      throw new EnforcerRuleException("One does not simply define exclusions on dependencies. Dependency exclusions " +
           "have to be declared in <dependencyManagement>: " + depsWithExclusions);
    }
  }

  private Collection<Dependency> searchForDependencies(Document pom, String xpath) {
    NodeList dependencies = XmlUtils.evaluateXPathAsNodeList(xpath, pom);
    Document dependenciesDoc = XmlUtils.createDocument("dependencies", dependencies);
    return new DeclaredDependenciesReader(dependenciesDoc).read(XPathExpressions.STANDALONE_DEPENDENCIES);
  }

  @Override
  protected void accept(PedanticEnforcerVisitor visitor) {
    visitor.visit(this);
  }

  private static class ProjectVersionPredicate implements Predicate<Dependency> {
    @Override
    public boolean apply(Dependency input) {
      if ("${project.version}".equals(input.getVersion()) || "${version}".equals(input.getVersion())) {
        return false;
      }
      return true;
    }
  }
}
