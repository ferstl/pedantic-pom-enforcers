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
import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

import com.github.ferstl.maven.pomenforcers.artifact.ArtifactOrdering;
import com.github.ferstl.maven.pomenforcers.artifact.PluginElement;
import com.github.ferstl.maven.pomenforcers.artifact.PluginMatcher;
import com.github.ferstl.maven.pomenforcers.model.PluginModel;
import com.github.ferstl.maven.pomenforcers.util.CommaSeparatorUtils;
import com.github.ferstl.maven.pomenforcers.util.EnforcerRuleUtils;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;

import static com.github.ferstl.maven.pomenforcers.artifact.PluginElement.ARTIFACT_ID;
import static com.github.ferstl.maven.pomenforcers.artifact.PluginElement.GROUP_ID;

/**
 * This enforcer makes sure that all plugins in your plugin management section
 * are ordered. The ordering can be defined by any combination of groupId and
 * artifactId. Each of these attributes may be given a priority.
 * <pre>
 * ### Example
 *     &lt;rules&gt;
 *       &lt;pluginManagementOrder implementation=&quot;ch.sferstl.maven.pomenforcer.PedanticPluginManagementOrderEnforcer&quot;&gt;
 *         &lt;!-- order by groupId and artifactId (default) --&gt;
 *         &lt;orderBy&gt;groupId,artifactId&lt;/orderBy&gt;
 *         &lt;!-- all group IDs starting with com.myproject.plugins and com.myproject.testplugins should occur first --&gt;
 *         &lt;groupIdPriorities&gt;com.myproject.plugins,com.myproject.testplugins&lt;/groupIdPriorities&gt;
 *         &lt;!-- all artifact IDs starting with mytest and myintegrationtest should occur first --&gt;
 *         &lt;artifactIdPriorities&gt;mytest-,myintegrationtest-&lt;/artifactIdPriorities&gt;
 *       &lt;/pluginManagementOrder&gt;
 *     &lt;/rules&gt;
 * </pre>
 * @id {@link PedanticEnforcerRule#PLUGIN_MANAGEMENT_ORDER}
 */
public class PedanticPluginManagementOrderEnforcer extends AbstractPedanticEnforcer {

  private final ArtifactOrdering<PluginModel, String, PluginElement> artifactOrdering;

  public PedanticPluginManagementOrderEnforcer() {
    this.artifactOrdering = ArtifactOrdering.orderBy(GROUP_ID, ARTIFACT_ID);
  }

  /**
   * Comma-separated list of plugin elements that defines the ordering.
   * @param pluginElements Comma-separated list of plugin elements that defines the ordering.
   * @configParam
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
    this.artifactOrdering.redefineOrderBy(orderBy);
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
    this.artifactOrdering.setPriorities(PluginElement.GROUP_ID, groupIdPriorities);
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
    this.artifactOrdering.setPriorities(PluginElement.ARTIFACT_ID, artifactIdPriorities);
  }

  @Override
  protected void doEnforce() throws EnforcerRuleException {
    MavenProject project = EnforcerRuleUtils.getMavenProject(getHelper());
    Log log = getLog();
    log.info("Enforcing plugin management order.");
    log.info("  -> Plugins have to be ordered by: "
           + CommaSeparatorUtils.join(this.artifactOrdering.getOrderBy()));
    log.info("  -> Group ID priorities: "
           + CommaSeparatorUtils.join(this.artifactOrdering.getPriorities(PluginElement.GROUP_ID)));
    log.info("  -> ArtifactModel ID priorities: "
           + CommaSeparatorUtils.join(this.artifactOrdering.getPriorities(PluginElement.ARTIFACT_ID)));

    Collection<PluginModel> declaredPluginManagement = getProjectModel().getManagedPlugins();

    // TODO use project model directly
    Collection<PluginModel> managedPlugins =
        Collections2.transform(project.getPluginManagement().getPlugins(), new Function<Plugin, PluginModel>() {
      @Override
      public PluginModel apply(Plugin input) {
        return new PluginModel(input.getGroupId(), input.getArtifactId(), input.getVersion());
      }
    });
    Collection<PluginModel> declaredManagedPlugins = matchPlugins(declaredPluginManagement, managedPlugins);

    if (!this.artifactOrdering.isOrdered(declaredManagedPlugins)) {
      ImmutableList<PluginModel> sortedDependencies = this.artifactOrdering.immutableSortedCopy(declaredManagedPlugins);
      throw new EnforcerRuleException("One does not simply declare plugin management! "
          + "Your plugin management has to be ordered this way:" + sortedDependencies);
    }
  }

  @Override
  protected void accept(PedanticEnforcerVisitor visitor) {
    visitor.visit(this);
  }

  private Collection<PluginModel> matchPlugins(
      Collection<PluginModel> subset, Collection<PluginModel> superset) {
    return new PluginMatcher(superset, getHelper()).match(subset);
  }
}
