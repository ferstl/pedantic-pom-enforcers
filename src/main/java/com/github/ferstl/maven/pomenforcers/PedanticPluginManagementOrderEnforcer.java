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

import org.apache.maven.enforcer.rule.api.EnforcerRuleException;
import org.apache.maven.enforcer.rule.api.EnforcerRuleHelper;
import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.w3c.dom.Document;

import com.github.ferstl.maven.pomenforcers.artifact.ArtifactSorter;
import com.github.ferstl.maven.pomenforcers.artifact.PluginElement;
import com.github.ferstl.maven.pomenforcers.artifact.PluginMatcher;
import com.github.ferstl.maven.pomenforcers.reader.DeclaredPluginsReader;
import com.github.ferstl.maven.pomenforcers.reader.XPathExpressions;
import com.github.ferstl.maven.pomenforcers.util.CommaSeparatorUtils;
import com.github.ferstl.maven.pomenforcers.util.EnforcerRuleUtils;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;

/**
 * This enforcer makes sure that all plugins in your plugin management section
 * are ordered. The ordering can be defined by any combination of groupId and
 * artifactId. Each of these attributes may be given a priority.
 * <pre>
 * ### Example
 *
 *     <rules>
 *       <pluginManagementOrder implementation="ch.sferstl.maven.pomenforcer.PedanticPluginManagementOrderEnforcer">
 *       <!-- order by groupId and artifactId (default) -->
 *       <orderBy>groupId,artifactId</orderBy>
 *       <!-- all group IDs starting with com.myproject.plugins and com.myproject.testplugins should occur first -->
 *       <groupIdPriorities>com.myproject.plugins,com.myproject.testplugins</groupIdPriorities>
 *       <!-- all artifact IDs starting with mytest and myintegrationtest should occur first -->
 *       <artifactIdPriorities>mytest-,myintegrationtest-</artifactIdPriorities>
 *     </rules>
 * </pre>
 * @id {@link PedanticEnforcerRule#PLUGIN_MANAGEMENT_ORDER}
 */
public class PedanticPluginManagementOrderEnforcer extends AbstractPedanticEnforcer {

  private final ArtifactSorter<Plugin, PluginElement> artifactSorter;

  public PedanticPluginManagementOrderEnforcer() {
    Set<PluginElement> orderBy = Sets.newLinkedHashSet();
    orderBy.add(PluginElement.GROUP_ID);
    orderBy.add(PluginElement.ARTIFACT_ID);
    this.artifactSorter = new ArtifactSorter<>();
    this.artifactSorter.orderBy(orderBy);
  }

  /**
   * Comma-separated list of plugin elements that defines the ordering.
   * @param pluginElements Comma-separated list of plugin elements that defines the ordering.
   * @configParameter
   * @default groupId,artifactId
   */
  public void setOrderBy(String pluginElements) {
    Set<PluginElement> orderBy = Sets.newLinkedHashSet();
    Function<String, PluginElement> transformer = new Function<String, PluginElement>() {
      @Override
      public PluginElement apply(String input) {
        return PluginElement.getByElementName(input);
      }
    };
    CommaSeparatorUtils.splitAndAddToCollection(pluginElements, orderBy, transformer);
    this.artifactSorter.orderBy(orderBy);
  }

  /**
   * Comma-separated list of group IDs that should be listed first in the
   * plugins declaration. All group IDs that <strong>start with</strong>
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
    this.artifactSorter.setPriorities(PluginElement.GROUP_ID, groupIdPriorities);
  }

  /**
   * Comma-separated list of artifact IDs that should be listed first in the
   * plugins declaration. All artifact IDs that <strong>start with</strong>
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
    this.artifactSorter.setPriorities(PluginElement.ARTIFACT_ID, artifactIdPriorities);
  }

  @Override
  protected void doEnforce(EnforcerRuleHelper helper, Document pom) throws EnforcerRuleException {
    MavenProject project = EnforcerRuleUtils.getMavenProject(helper);
    Log log = helper.getLog();
    log.info("Enforcing plugin management order.");
    log.info("  -> Plugins have to be ordered by: "
           + CommaSeparatorUtils.join(this.artifactSorter.getOrderBy()));
    log.info("  -> Group ID priorities: "
           + CommaSeparatorUtils.join(this.artifactSorter.getPriorities(PluginElement.GROUP_ID)));
    log.info("  -> Artifact ID priorities: "
           + CommaSeparatorUtils.join(this.artifactSorter.getPriorities(PluginElement.ARTIFACT_ID)));

    Collection<Plugin> declaredPluginManagement =
        new DeclaredPluginsReader(pom).read(XPathExpressions.POM_MANAGED_PLUGINS);

    Collection<Plugin> managedPlugins = matchPlugins(declaredPluginManagement, project.getPluginManagement().getPlugins());

    Ordering<Plugin> pluginOrdering = this.artifactSorter.createOrdering();

    if (!pluginOrdering.isOrdered(managedPlugins)) {
      ImmutableList<Plugin> sortedDependencies = pluginOrdering.immutableSortedCopy(managedPlugins);
      throw new EnforcerRuleException("One does not simply declare plugin management! "
          + "Your plugin management has to be ordered this way:" + sortedDependencies);
    }
  }

  @Override
  protected void accept(PedanticEnforcerVisitor visitor) {
    visitor.visit(this);
  }

  private Collection<Plugin> matchPlugins(Collection<Plugin> subset, Collection<Plugin> superset) {
    return new PluginMatcher(superset).match(subset);
  }
}
