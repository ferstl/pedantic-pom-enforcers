/*
 * Copyright (c) 2012 - 2019 the original author or authors.
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

import java.util.Objects;

import org.apache.maven.enforcer.rule.api.EnforcerRuleHelper;
import org.apache.maven.model.Plugin;

import com.github.ferstl.maven.pomenforcers.model.PluginModel;

import static com.github.ferstl.maven.pomenforcers.util.EnforcerRuleUtils.evaluateProperties;

/**
 * Matches Maven {@link Plugin} objects with {@link PluginModel} objects.
 */
public class PluginMatcher extends AbstractOneToOneMatcher<Plugin, PluginModel> {

  public PluginMatcher(EnforcerRuleHelper helper) {
    super(helper);
  }

  @Override
  protected PluginModel transform(Plugin mavenPlugin) {
    return new PluginModel(mavenPlugin.getGroupId(), mavenPlugin.getArtifactId(), mavenPlugin.getVersion());
  }

  @Override
  protected boolean matches(PluginModel supersetItem, PluginModel subsetItem) {
    String groupId = evaluateProperties(subsetItem.getGroupId(), getHelper());
    String artifactId = evaluateProperties(subsetItem.getArtifactId(), getHelper());

    return Objects.equals(supersetItem.getGroupId(), groupId)
        && Objects.equals(supersetItem.getArtifactId(), artifactId);
  }

}
