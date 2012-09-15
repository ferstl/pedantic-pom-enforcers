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
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.maven.enforcer.rule.api.EnforcerRuleHelper;

import com.github.ferstl.maven.pomenforcers.artifact.DependencyElement;
import com.github.ferstl.maven.pomenforcers.artifact.DependencyMatcher;
import com.github.ferstl.maven.pomenforcers.model.DependencyModel;
import com.github.ferstl.maven.pomenforcers.priority.CompoundPriorityOrdering;
import com.github.ferstl.maven.pomenforcers.util.CommaSeparatorUtils;
import com.google.common.collect.Sets;

import static com.github.ferstl.maven.pomenforcers.artifact.DependencyElement.ARTIFACT_ID;
import static com.github.ferstl.maven.pomenforcers.artifact.DependencyElement.GROUP_ID;
import static com.github.ferstl.maven.pomenforcers.artifact.DependencyElement.SCOPE;
import static com.github.ferstl.maven.pomenforcers.functions.Transformers.stringToDependencyElement;


public abstract class AbstractPedanticDependencyOrderEnforcer extends AbstractPedanticEnforcer {

  private final CompoundPriorityOrdering<DependencyModel, String, DependencyElement> artifactOrdering;

  public AbstractPedanticDependencyOrderEnforcer() {
    this.artifactOrdering = CompoundPriorityOrdering.orderBy(SCOPE, GROUP_ID, ARTIFACT_ID);
  }

  /**
   * Comma-separated list of dependency elements that defines the ordering.
   * @param dependencyElements Comma-separated list of dependency elements that defines the ordering.
   * @configParam
   * @default scope,groupId,artifactId
   */
  public void setOrderBy(String dependencyElements) {
    Set<DependencyElement> orderBy = new LinkedHashSet<>();
    CommaSeparatorUtils.splitAndAddToCollection(dependencyElements, orderBy, stringToDependencyElement());
    this.artifactOrdering.redefineOrderBy(orderBy);
  }

  /**
   * Comma-separated list of group IDs that should be listed first in the
   * dependencies declaration. All group IDs that <strong>start with</strong>
   * any of the priorized group IDs in the given list, are required to be
   * located first in the dependencies section.
   *
   * @param groupIds Comma separated list of group IDs.
   * @configParam
   * @default n/a
   */
  public void setGroupIdPriorities(String groupIds) {
    LinkedHashSet<String> groupIdPriorities = Sets.newLinkedHashSet();
    CommaSeparatorUtils.splitAndAddToCollection(groupIds, groupIdPriorities);
    this.artifactOrdering.setPriorities(DependencyElement.GROUP_ID, groupIdPriorities);
  }

  /**
   * Comma-separated list of artifact IDs that should be listed first in the
   * dependencies declaration. All artifact IDs that <strong>start with</strong>
   * any of the priorized IDs in the given list, are required to be located
   * first in the dependencies section.
   *
   * @param artifactIds Comma separated list of artifact IDs.
   * @configParam
   * @default n/a
   */
  public void setArtifactIdPriorities(String artifactIds) {
    LinkedHashSet<String> artifactIdPriorities = Sets.newLinkedHashSet();
    CommaSeparatorUtils.splitAndAddToCollection(artifactIds, artifactIdPriorities);
    this.artifactOrdering.setPriorities(DependencyElement.ARTIFACT_ID, artifactIdPriorities);
  }

  /**
   * Comma-separated list of scopes that should be listed first in the
   * dependencies declaration. All scopes that equal any of the scopes in the
   * given list, are required to be located first in the dependencies section.
   *
   * @param scopes Comma separated list of scopes.
   * @configParam
   * @default n/a
   */
  public void setScopePriorities(String scopes) {
    LinkedHashSet<String> scopePriorities = Sets.newLinkedHashSet();
    CommaSeparatorUtils.splitAndAddToCollection(scopes, scopePriorities);
    this.artifactOrdering.setPriorities(DependencyElement.SCOPE, scopePriorities);
  }

  protected CompoundPriorityOrdering<DependencyModel, String, DependencyElement> getArtifactOrdering() {
    return this.artifactOrdering;
  }

  protected Collection<DependencyModel> matchDependencies(
      final Collection<DependencyModel> subset,
      final Collection<DependencyModel> superset,
      final EnforcerRuleHelper helper) {
    return new DependencyMatcher(superset, helper).match(subset);
  }
}