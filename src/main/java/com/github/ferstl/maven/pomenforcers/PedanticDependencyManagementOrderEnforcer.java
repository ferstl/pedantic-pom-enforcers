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
import org.apache.maven.model.DependencyManagement;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.w3c.dom.Document;

import com.github.ferstl.maven.pomenforcers.artifact.DependencyElement;
import com.github.ferstl.maven.pomenforcers.reader.DeclaredDependenciesReader;
import com.github.ferstl.maven.pomenforcers.reader.XPathExpressions;
import com.github.ferstl.maven.pomenforcers.util.CommaSeparatorUtils;
import com.github.ferstl.maven.pomenforcers.util.EnforcerRuleUtils;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;

/**
 * This enforcer makes sure that all artifacts in your dependency management are
 * ordered. The ordering can be defined by any combination of <code>scope</code>,
 * <code>groupId</code> and <code>artifactId</code>. Each of these attributes
 * may be given a priority.
 *
 * <pre>
 * ### Example
 *     <rules>
 *       <dependencyManagementOrder implementation="ch.sferstl.maven.pomenforcer.PedanticDependencyManagementOrderEnforcer">
 *       <!-- order by scope, groupId and artifactId (default) -->
 *       <orderBy>scope,groupId,artifactId</orderBy>
 *       <!-- runtime scope should occur before provided scope -->
 *       <scopePriorities>compile,runtime,provided</scopePriorities>
 *       <!-- all group IDs starting with com.myproject and com.mylibs should occur first -->
 *       <groupIdPriorities>com.myproject,com.mylibs</groupIdPriorities>
 *       <!-- all artifact IDs starting with commons- and utils- should occur first -->
 *       <artifactIdPriorities>commons-,utils-</artifactIdPriorities>
 *     </rules>
 * </pre>
 *
 * @id {@link PedanticEnforcerRule#DEPENDENCY_MANAGEMENT_ORDER}
 */
public class PedanticDependencyManagementOrderEnforcer extends AbstractPedanticDependencyOrderEnforcer {

  @Override
  protected void doEnforce(EnforcerRuleHelper helper, Document pom) throws EnforcerRuleException {
    MavenProject project = EnforcerRuleUtils.getMavenProject(helper);

    Log log = helper.getLog();
    log.info("Enforcing dependency management order.");
    log.info("  -> Dependencies have to be ordered by: "
           + CommaSeparatorUtils.join(getArtifactSorter().getOrderBy()));
    log.info("  -> Scope priorities: "
           + CommaSeparatorUtils.join(getArtifactSorter().getPriorities(DependencyElement.SCOPE)));
    log.info("  -> Group ID priorities: "
           + CommaSeparatorUtils.join(getArtifactSorter().getPriorities(DependencyElement.GROUP_ID)));
    log.info("  -> Artifact ID priorities: "
           + CommaSeparatorUtils.join(getArtifactSorter().getPriorities(DependencyElement.ARTIFACT_ID)));

    Collection<Dependency> declaredDependencyManagement =
        new DeclaredDependenciesReader(pom).read(XPathExpressions.POM_MANAGED_DEPENDENCIES);

    Collection<Dependency> managedDependencyArtifacts =
        matchDependencies(declaredDependencyManagement, getManagedDependencies(project), helper);

    Ordering<Dependency> dependencyOrdering = getArtifactSorter().createOrdering();

    if (!dependencyOrdering.isOrdered(managedDependencyArtifacts)) {
      ImmutableList<Dependency> sortedDependencies =
          dependencyOrdering.immutableSortedCopy(managedDependencyArtifacts);
      throw new EnforcerRuleException("One does not simply declare dependency management! "
          + "Your dependency management has to be ordered this way:" + sortedDependencies);
    }
  }

  @Override
  protected void accept(PedanticEnforcerVisitor visitor) {
    visitor.visit(this);
  }

  private Collection<Dependency> getManagedDependencies(MavenProject project) {
    DependencyManagement dependencyManagement = project.getDependencyManagement();
    Collection<Dependency> managedDependencies;
    if (dependencyManagement != null) {
      managedDependencies = dependencyManagement.getDependencies();
    } else {
      managedDependencies = Lists.newArrayList();
    }
    return managedDependencies;
  }
}
