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

import java.util.List;
import java.util.Set;

import org.apache.maven.enforcer.rule.api.EnforcerRuleException;
import org.apache.maven.enforcer.rule.api.EnforcerRuleHelper;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.w3c.dom.Document;

import com.github.ferstl.maven.pomenforcers.reader.DeclaredModulesReader;
import com.github.ferstl.maven.pomenforcers.reader.XPathExpressions;
import com.github.ferstl.maven.pomenforcers.util.CommaSeparatorUtils;
import com.github.ferstl.maven.pomenforcers.util.EnforcerRuleUtils;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;



public class PedanticModuleOrderEnforcer extends AbstractPedanticEnforcer {

  /** All modules in this set won't be checked for the correct order. */
  private final Set<String> ignoredModules;

  public PedanticModuleOrderEnforcer() {
    this.ignoredModules = Sets.newLinkedHashSet();
  }

  public void setIgnoredModules(String ignoredModules) {
    CommaSeparatorUtils.splitAndAddToCollection(ignoredModules, this.ignoredModules);
  }

  @Override
  protected void doEnforce(EnforcerRuleHelper helper, Document pom) throws EnforcerRuleException {
    MavenProject project = EnforcerRuleUtils.getMavenProject(helper);
    // Do nothing if the project is not a parent project
    if (!isPomProject(project)) {
      return;
    }

    Log log = helper.getLog();
    log.info("Enforcing alphabetical module order.");
    log.info("  -> These modules are ignored: " + CommaSeparatorUtils.join(this.ignoredModules));

    // Remove all modules to be ignored.
    List<String> declaredModules = new DeclaredModulesReader(pom).read(XPathExpressions.POM_MODULES);
    declaredModules.removeAll(this.ignoredModules);

    // Enforce the module order
    Ordering<String> moduleOrdering = Ordering.natural();
    if (!moduleOrdering.isOrdered(declaredModules)) {
      ImmutableList<String> orderedModules = moduleOrdering.immutableSortedCopy(declaredModules);
      String message = "One does not simply declare modules! "
          + "You have to sort your modules alphabetically: " + orderedModules;
      if (!this.ignoredModules.isEmpty()) {
        message += " You may place these modules in any order: " + this.ignoredModules;
      }
      throw new EnforcerRuleException(message);
    }
  }

  @Override
  protected void accept(PedanticEnforcerVisitor visitor) {
    visitor.visit(this);
  }

  private boolean isPomProject(MavenProject project) {
    return "pom".equals(project.getPackaging());
  }
}
