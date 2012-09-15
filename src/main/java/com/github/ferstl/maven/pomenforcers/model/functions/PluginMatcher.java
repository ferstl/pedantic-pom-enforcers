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
package com.github.ferstl.maven.pomenforcers.model.functions;

import java.util.Collection;

import org.apache.maven.enforcer.rule.api.EnforcerRuleHelper;

import com.github.ferstl.maven.pomenforcers.model.PluginModel;
import com.google.common.base.Function;

import static com.github.ferstl.maven.pomenforcers.util.EnforcerRuleUtils.evaluateProperties;
import static com.google.common.base.Objects.equal;
import static com.google.common.collect.Collections2.transform;

public class PluginMatcher {

  private final MatchFunction matchFunction;

  public PluginMatcher(Collection<PluginModel> superset, EnforcerRuleHelper helper) {
    this.matchFunction = new MatchFunction(superset, helper);
  }

  public Collection<PluginModel> match(Collection<PluginModel> subset) {
    return transform(subset, this.matchFunction);
  }


  private static class MatchFunction implements Function<PluginModel, PluginModel> {

    private final Collection<PluginModel> superset;
    private final EnforcerRuleHelper helper;

    public MatchFunction(Collection<PluginModel> superset, EnforcerRuleHelper helper) {
      this.superset = superset;
      this.helper = helper;
    }

    @Override
    public PluginModel apply(PluginModel plugin) {
      String groupId = evaluateProperties(plugin.getGroupId(), this.helper);
      String artifactId = evaluateProperties(plugin.getArtifactId(), this.helper);
      for (PluginModel supersetPlugin : this.superset) {
        if (equal(supersetPlugin.getGroupId(), groupId)
         && equal(supersetPlugin.getArtifactId(), artifactId)) {
          return new PluginModel(
              supersetPlugin.getGroupId(),
              supersetPlugin.getArtifactId(),
              supersetPlugin.getVersion());
        }
      }
      throw new IllegalStateException(
          "Could not match plugin '" + plugin + "' with superset '." + this.superset + "'.");
    }
  }
}
