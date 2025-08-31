/*
 * Copyright (c) 2012 - 2023 the original author or authors.
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluator;
import com.github.ferstl.maven.pomenforcers.util.CommaSeparatorUtils;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;
import static com.github.ferstl.maven.pomenforcers.ErrorReport.toList;


/**
 * This enforcer makes sure that your <code>modules</code> section is sorted
 * alphabetically. Modules that should occur at a specific position in the
 * <code>&lt;modules&gt;</code> section can be ignored.
 * <pre>
 * ### Example
 *     &lt;rules&gt;
 *       &lt;moduleOrder implementation=&quot;com.github.ferstl.maven.pomenforcers.PedanticModuleOrderEnforcer&quot;&gt;
 *         &lt;!-- These modules may occur at any place in the modules section --&gt;
 *         &lt;ignoredModules&gt;dist-deb,dist-rpm&lt;/ignoredModules&gt;
 *        &lt;/moduleOrder&gt;
 *     &lt;/rules&gt;
 * </pre>
 *
 * @id {@link PedanticEnforcerRule#MODULE_ORDER}
 * @since 1.0.0
 */
@Named("moduleOrder")
public class PedanticModuleOrderEnforcer extends AbstractPedanticEnforcer {

  /**
   * All modules in this set won't be checked for the correct order.
   */
  private final Set<String> ignoredModules;

  @Inject
  public PedanticModuleOrderEnforcer(final MavenProject project, final ExpressionEvaluator helper) {
    super(project, helper);
    this.ignoredModules = Sets.newLinkedHashSet();
  }

  /**
   * Comma-separated list of ignored modules. All modules in this list may occur at any place in the
   * <code>modules</code> section.
   *
   * @param ignoredModules Comma-separated list of ignored modules.
   * @configParam
   * @since 1.0.0
   */
  public void setIgnoredModules(String ignoredModules) {
    CommaSeparatorUtils.splitAndAddToCollection(ignoredModules, this.ignoredModules);
  }

  @Override
  protected PedanticEnforcerRule getDescription() {
    return PedanticEnforcerRule.MODULE_ORDER;
  }

  @Override
  protected void accept(PedanticEnforcerVisitor visitor) {
    visitor.visit(this);
  }

  @Override
  protected void doEnforce(ErrorReport report) {
    MavenProject project = getMavenProject();
    // Do nothing if the project is not a parent project
    if (!isPomProject(project)) {
      return;
    }

    // Remove all modules to be ignored.
    List<String> declaredModules = new ArrayList<>(getProjectModel().getModules());
    declaredModules.removeAll(this.ignoredModules);

    // Enforce the module order
    Ordering<String> moduleOrdering = Ordering.natural();
    if (!moduleOrdering.isOrdered(declaredModules)) {
      reportError(report, declaredModules, moduleOrdering.immutableSortedCopy(declaredModules));
    }
  }

  private boolean isPomProject(MavenProject project) {
    return "pom".equals(project.getPackaging());
  }

  private void reportError(ErrorReport report, Collection<String> declaredModules, Collection<String> orderedModules) {
    report.addLine("You have to sort your modules alphabetically:")
        .emptyLine()
        .addDiff(declaredModules, orderedModules, "Actual Order", "Required Order");
    if (!this.ignoredModules.isEmpty()) {
      report.emptyLine()
          .addLine("You may place these modules anywhere in your <modules> section:")
          .addLine(toList(this.ignoredModules));
    }
  }
}
