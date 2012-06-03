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
package ch.sferstl.maven.pomenforcer;

import java.util.Collection;

import org.apache.maven.enforcer.rule.api.EnforcerRuleException;
import org.apache.maven.enforcer.rule.api.EnforcerRuleHelper;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.w3c.dom.Document;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Ordering;

import ch.sferstl.maven.pomenforcer.artifact.DependencyElement;
import ch.sferstl.maven.pomenforcer.reader.DeclaredDependenciesReader;
import ch.sferstl.maven.pomenforcer.util.CommaSeparatorUtils;
import ch.sferstl.maven.pomenforcer.util.EnforcerRuleUtils;


public class PedanticDependencyOrderEnforcer extends AbstractPedanticDependencyOrderEnforcer {

  @Override
  protected void doEnforce(EnforcerRuleHelper helper, Document pom) throws EnforcerRuleException {
    MavenProject project = EnforcerRuleUtils.getMavenProject(helper);

    Log log = helper.getLog();
    log.info("Enforcing dependency order.");
    log.info("  -> Dependencies have to be ordered by: "
           + CommaSeparatorUtils.join(getArtifactSorter().getOrderBy()));
    log.info("  -> Scope priorities: "
           + CommaSeparatorUtils.join(getArtifactSorter().getPriorities(DependencyElement.SCOPE)));
    log.info("  -> Group ID priorities: "
           + CommaSeparatorUtils.join(getArtifactSorter().getPriorities(DependencyElement.GROUP_ID)));
    log.info("  -> Artifact ID priorities: "
           + CommaSeparatorUtils.join(getArtifactSorter().getPriorities(DependencyElement.ARTIFACT_ID)));

    Collection<Dependency> declaredDependencies = new DeclaredDependenciesReader(pom).read();
    Collection<Dependency> projectDependencies = project.getDependencies();

    Collection<Dependency> dependencyArtifacts =
        matchDependencies(declaredDependencies, projectDependencies, helper);
    Ordering<Dependency> dependencyOrdering = getArtifactSorter().createOrdering();

    if (!dependencyOrdering.isOrdered(dependencyArtifacts)) {
      ImmutableList<Dependency> sortedDependencies =
          dependencyOrdering.immutableSortedCopy(dependencyArtifacts);
      throw new EnforcerRuleException("One does not simply declare dependencies! "
        + "Your dependencies have to be sorted this way: " + sortedDependencies);
    }
  }

  @Override
  protected void accept(PedanticEnforcerVisitor visitor) {
    visitor.visit(this);
  }
}
