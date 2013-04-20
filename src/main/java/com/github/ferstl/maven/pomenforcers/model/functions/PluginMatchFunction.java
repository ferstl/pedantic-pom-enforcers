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
package com.github.ferstl.maven.pomenforcers.model.functions;

import java.util.Collection;
import java.util.Map.Entry;

import org.apache.maven.enforcer.rule.api.EnforcerRuleHelper;
import org.apache.maven.model.Plugin;

import com.github.ferstl.maven.pomenforcers.model.PluginModel;
import com.google.common.base.Function;
import com.google.common.collect.Maps;

import static com.github.ferstl.maven.pomenforcers.util.EnforcerRuleUtils.evaluateProperties;
import static com.google.common.base.Objects.equal;

public class PluginMatchFunction implements Function<PluginModel, Entry<PluginModel, PluginModel>> {

  private final Collection<Plugin> superset;
  private final EnforcerRuleHelper helper;

  public PluginMatchFunction(Collection<Plugin> superset, EnforcerRuleHelper helper) {
    this.superset = superset;
    this.helper = helper;
  }

  @Override
  public Entry<PluginModel, PluginModel> apply(PluginModel plugin) {
    String groupId = evaluateProperties(plugin.getGroupId(), this.helper);
    String artifactId = evaluateProperties(plugin.getArtifactId(), this.helper);

    for (Plugin supersetPlugin : this.superset) {
      PluginModel supersetPluginModel = createPluginModel(supersetPlugin);

      if (equal(supersetPlugin.getGroupId(), groupId)
       && equal(supersetPlugin.getArtifactId(), artifactId)) {
        return Maps.immutableEntry(supersetPluginModel, plugin);
      }
    }

    throw new IllegalStateException(
        "Could not match plugin '" + plugin + "' with superset '." + this.superset + "'.");
  }

  private static PluginModel createPluginModel(Plugin plugin) {
    return new PluginModel(plugin.getGroupId(), plugin.getArtifactId(), plugin.getVersion());
  }
}