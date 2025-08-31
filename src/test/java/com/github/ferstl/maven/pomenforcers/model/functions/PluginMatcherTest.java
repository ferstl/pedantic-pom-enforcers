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
package com.github.ferstl.maven.pomenforcers.model.functions;

import org.apache.maven.model.Plugin;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.github.ferstl.maven.pomenforcers.model.PluginModel;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

public class PluginMatcherTest {

  private PluginMatcher pluginMatcher;

  @BeforeEach
  public void before() {
    this.pluginMatcher = new PluginMatcher(mock(ExpressionEvaluator.class));
  }

  @Test
  public void transform() {
    // arrange
    Plugin plugin = new Plugin();
    plugin.setGroupId("a");
    plugin.setArtifactId("b");
    plugin.setVersion("c");

    // act
    PluginModel pluginModel = this.pluginMatcher.transform(plugin);

    // assert
    assertEquals("a", pluginModel.getGroupId());
    assertEquals("b", pluginModel.getArtifactId());
    assertEquals("c", pluginModel.getVersion());
  }

  @Test
  public void matchWithAllGavParameters() {
    PluginModel supersetPlugin = new PluginModel("a", "b", "c");
    PluginModel subsetPlugin = new PluginModel("a", "b", "c");

    assertTrue(this.pluginMatcher.matches(supersetPlugin, subsetPlugin));
  }

  @Test
  public void matchWithDefaultGroupIdForNull() {
    PluginModel supersetPlugin = new PluginModel("org.apache.maven.plugins", "b", "c");
    PluginModel subsetPlugin = new PluginModel(null, "b", "c");

    assertTrue(this.pluginMatcher.matches(supersetPlugin, subsetPlugin));
  }

  @Test
  public void matchWithDefaultGroupIdForEmpty() {
    PluginModel supersetPlugin = new PluginModel("org.apache.maven.plugins", "b", "c");
    PluginModel subsetPlugin = new PluginModel("", "b", "c");

    assertTrue(this.pluginMatcher.matches(supersetPlugin, subsetPlugin));
  }
}
