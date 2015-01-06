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

import org.apache.maven.model.Dependency;
import org.apache.maven.project.MavenProject;

import com.github.ferstl.maven.pomenforcers.model.DependencyModel;
import com.github.ferstl.maven.pomenforcers.util.SideBySideDiffUtil;
import com.google.common.collect.Collections2;

import static com.google.common.base.Functions.toStringFunction;


/**
 * This enforcer makes sure that all artifacts in your dependencies section are
 * ordered. The ordering can be defined by any combination of <code>scope</code>, <code>groupId</code>
 * and <code>artifactId</code>. Each of these attributes may be given a priority.
 *
 * <pre>
 * ### Example
 *     &lt;rules&gt;
 *       &lt;dependencyOrder implementation=&quot;com.github.ferstl.maven.pomenforcers.PedanticDependencyOrderEnforcer&quot;&gt;
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
  protected void accept(PedanticEnforcerVisitor visitor) {
    visitor.visit(this);
  }

  @Override
  protected PedanticEnforcerRule getDescription() {
    return PedanticEnforcerRule.DEPENDENCY_ORDER;
  }

  @Override
  protected Collection<DependencyModel> getDeclaredDependencies() {
    return getProjectModel().getDependencies();
  }

  @Override
  protected Collection<Dependency> getMavenDependencies(MavenProject project) {
    return project.getDependencies();
  }

  @Override
  protected void reportError(ErrorReport report, Collection<DependencyModel> resolvedDependencies, Collection<DependencyModel> sortedDependencies) {
    Collection<String> resolvedDependenciesAsString = Collections2.transform(resolvedDependencies, toStringFunction());
    Collection<String> sortedDependenciesAsString = Collections2.transform(sortedDependencies, toStringFunction());

    report.addLine("Your dependencies have to be sorted this way:")
          .emptyLine()
          .addLine(SideBySideDiffUtil.diff(resolvedDependenciesAsString, sortedDependenciesAsString, "Actual Order", "Required Order"));
  }
}
