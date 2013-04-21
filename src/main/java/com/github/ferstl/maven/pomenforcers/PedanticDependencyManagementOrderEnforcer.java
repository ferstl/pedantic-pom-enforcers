/*
 * Copyright (c) 2013 by Stefan Ferstl <st.ferstl@gmail.com>
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
import java.util.Collections;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.DependencyManagement;
import org.apache.maven.project.MavenProject;

import com.github.ferstl.maven.pomenforcers.model.DependencyElement;
import com.github.ferstl.maven.pomenforcers.model.DependencyModel;
import com.github.ferstl.maven.pomenforcers.model.ProjectModel;
import com.github.ferstl.maven.pomenforcers.priority.CompoundPriorityOrdering;
import com.github.ferstl.maven.pomenforcers.util.EnforcerRuleUtils;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableList;

import static com.github.ferstl.maven.pomenforcers.ErrorReport.toList;


/**
 * This enforcer makes sure that all artifacts in your dependency management are
 * ordered. The ordering can be defined by any combination of <code>scope</code>,
 * <code>groupId</code> and <code>artifactId</code>. Each of these attributes
 * may be given a priority.
 *
 * <pre>
 * ### Example
 *     &lt;rules&gt;
 *       &lt;dependencyManagementOrder implementation=&quot;com.github.ferstl.maven.pomenforcers.PedanticDependencyManagementOrderEnforcer&quot;&gt;
 *         &lt;!-- order by scope, groupId and artifactId (default) --&gt;
 *         &lt;orderBy&gt;scope,groupId,artifactId&lt;/orderBy&gt;
 *         &lt;!-- runtime scope should occur before provided scope --&gt;
 *         &lt;scopePriorities&gt;compile,runtime,provided&lt;/scopePriorities&gt;
 *         &lt;!-- all group IDs starting with com.myproject and com.mylibs should occur first --&gt;
 *         &lt;groupIdPriorities&gt;com.myproject,com.mylibs&lt;/groupIdPriorities&gt;
 *         &lt;!-- all artifact IDs starting with commons- and utils- should occur first --&gt;
 *         &lt;artifactIdPriorities&gt;commons-,utils-&lt;/artifactIdPriorities&gt;
 *       &lt;/dependencyManagementOrder&gt;
 *     &lt;/rules&gt;
 * </pre>
 *
 * @id {@link PedanticEnforcerRule#DEPENDENCY_MANAGEMENT_ORDER}
 */
public class PedanticDependencyManagementOrderEnforcer extends AbstractPedanticDependencyOrderEnforcer {

  @Override
  protected PedanticEnforcerRule getDescription() {
    return PedanticEnforcerRule.DEPENDENCY_MANAGEMENT_ORDER;
  }

  @Override
  protected void accept(PedanticEnforcerVisitor visitor) {
    visitor.visit(this);
  }

  @Override
  protected void doEnforce(ErrorReport report) {
    MavenProject project = EnforcerRuleUtils.getMavenProject(getHelper());
    CompoundPriorityOrdering<DependencyModel, String, DependencyElement> artifactOrdering = getArtifactOrdering();

    ProjectModel projectModel = getProjectModel();
    Collection<DependencyModel> declaredManagedDependencies = projectModel.getManagedDependencies();

    BiMap<DependencyModel, DependencyModel> managedDependencies =
        matchDependencies(declaredManagedDependencies, getManagedDependencies(project));

    if (!artifactOrdering.isOrdered(managedDependencies.values())) {
      ImmutableList<DependencyModel> sortedDependencies =
          artifactOrdering.immutableSortedCopy(managedDependencies.values());
      report.addLine("Your dependency management has to be ordered this way:")
            .addLine(toList(sortedDependencies));
    }
  }

  private Collection<Dependency> getManagedDependencies(MavenProject project) {
    DependencyManagement dependencyManagement = project.getDependencyManagement();
    if (dependencyManagement != null) {
      return dependencyManagement.getDependencies();
    } else {
      return Collections.emptyList();
    }
  }
}
