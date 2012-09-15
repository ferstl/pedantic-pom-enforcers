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
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

import com.github.ferstl.maven.pomenforcers.model.DependencyElement;
import com.github.ferstl.maven.pomenforcers.model.DependencyModel;
import com.github.ferstl.maven.pomenforcers.priority.CompoundPriorityOrdering;
import com.github.ferstl.maven.pomenforcers.util.CommaSeparatorUtils;
import com.github.ferstl.maven.pomenforcers.util.EnforcerRuleUtils;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;

import static com.github.ferstl.maven.pomenforcers.model.functions.Transformers.dependencyToDependencyModel;


/**
 * This enforcer makes sure that all artifacts in your dependencies section are
 * ordered. The ordering can be defined by any combination of <code>scope</code>, <code>groupId</code>
 * and <code>artifactId</code>. Each of these attributes may be given a priority.
 *
 * <pre>
 * ### Example
 *     &lt;rules&gt;
 *       &lt;dependencyOrder implementation=&quot;ch.sferstl.maven.pomenforcer.PedanticDependencyOrderEnforcer&quot;&gt;
 *         &lt;!-- order by scope, groupId and artifactId (default) --&gt;
 *         &lt;orderBy&gt;scope,groupId,artifactId&lt;/orderBy&gt;
 *         &lt;!-- runtime scope should occur before provided scope --&gt;
 *         &lt;scopePriorities&gt;compile,runtime,provided&lt;/scopePriorities&gt;
 *         &lt;!-- all group IDs starting with com.myproject and com.mylibs should occur first --&gt;
 *         &lt;groupIdPriorities&gt;com.myproject,com.mylibs&lt;/groupIdPriorities&gt;
 *         &lt;!-- all artifact IDs starting with commons- and utils- should occur first --&gt;
 *         &lt;artifactIdPriorities&gt;commons-,utils-&lt;/artifactIdPriorities&gt;
 *       &lt;/dependencyOrder&gt;
 *     &lt;/rules&gt;
 * </pre>
 *
 * @id {@link PedanticEnforcerRule#DEPENDENCY_ORDER}
 */
public class PedanticDependencyOrderEnforcer extends AbstractPedanticDependencyOrderEnforcer {

  @Override
  protected void doEnforce() throws EnforcerRuleException {
    MavenProject project = EnforcerRuleUtils.getMavenProject(getHelper());
    CompoundPriorityOrdering<DependencyModel, String, DependencyElement> artifactOrdering = getArtifactOrdering();

    Log log = getLog();
    log.info("Enforcing dependency order.");
    log.info("  -> Dependencies have to be ordered by: "
           + CommaSeparatorUtils.join(artifactOrdering.getOrderBy()));
    log.info("  -> Scope priorities: "
           + CommaSeparatorUtils.join(artifactOrdering.getPriorities(DependencyElement.SCOPE)));
    log.info("  -> Group ID priorities: "
           + CommaSeparatorUtils.join(artifactOrdering.getPriorities(DependencyElement.GROUP_ID)));
    log.info("  -> Artifact ID priorities: "
           + CommaSeparatorUtils.join(artifactOrdering.getPriorities(DependencyElement.ARTIFACT_ID)));

    Collection<DependencyModel> declaredDependencies = getProjectModel().getDependencies();
    Collection<DependencyModel> projectDependencies =
        Collections2.transform(project.getDependencies(), dependencyToDependencyModel());

    Collection<DependencyModel> dependencyArtifacts =
        matchDependencies(declaredDependencies, projectDependencies, getHelper());

    if (!artifactOrdering.isOrdered(dependencyArtifacts)) {
      ImmutableList<DependencyModel> sortedDependencies =
          artifactOrdering.immutableSortedCopy(dependencyArtifacts);
      throw new EnforcerRuleException("One does not simply declare dependencies! "
        + "Your dependencies have to be sorted this way: " + sortedDependencies);
    }
  }

  @Override
  protected void accept(PedanticEnforcerVisitor visitor) {
    visitor.visit(this);
  }
}
