/*
 * Copyright (c) 2012 - 2020 the original author or authors.
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

import org.apache.maven.model.Plugin;
import org.junit.Test;

import com.github.ferstl.maven.pomenforcers.model.PluginModel;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * JUnit tests for {@link PedanticPluginManagementOrderEnforcer}.
 */
public class PedanticPluginManagementOrderEnforcerTest extends AbstractPedanticEnforcerTest<PedanticPluginManagementOrderEnforcer> {

  @Override
  PedanticPluginManagementOrderEnforcer createRule() {
    return new PedanticPluginManagementOrderEnforcer();
  }

  @Test
  @Override
  public void getDescription() {
    assertThat(this.testRule.getDescription(), equalTo(PedanticEnforcerRule.PLUGIN_MANAGEMENT_ORDER));
  }

  @Test
  @Override
  public void accept() {
    PedanticEnforcerVisitor visitor = mock(PedanticEnforcerVisitor.class);
    this.testRule.accept(visitor);

    verify(visitor).visit(this.testRule);
  }

  @Test
  public void defaultSettingsCorrect() {
    addManagedPlugin("a.b.c", "a");
    addManagedPlugin("a.b.c", "b");

    executeRuleAndCheckReport(false);
  }

  @Test
  public void defaultSettingsWrongGroupIdOrder() {
    addManagedPlugin("d.e.f", "a");
    addManagedPlugin("a.b.c", "a");

    executeRuleAndCheckReport(true);
  }

  @Test
  public void defaultSettingsWrongArtifactIdOrder() {
    addManagedPlugin("a.b.c", "b");
    addManagedPlugin("a.b.c", "a");

    executeRuleAndCheckReport(true);
  }

  @Test
  public void groupIdPriorities() {
    this.testRule.setGroupIdPriorities("x.y.z,u.v.w");

    addManagedPlugin("x.y.z", "a");
    addManagedPlugin("u.v.w", "a");
    addManagedPlugin("a.b.c", "a");

    executeRuleAndCheckReport(false);
  }

  @Test
  public void artifactIdPriorities() {
    this.testRule.setArtifactIdPriorities("z,y");

    addManagedPlugin("a.b.c", "z");
    addManagedPlugin("a.b.c", "y");
    addManagedPlugin("a.b.c", "a");

    executeRuleAndCheckReport(false);
  }

  @Test
  public void orderBy() {
    this.testRule.setOrderBy("artifactId,groupId");

    addManagedPlugin("x.y.z", "a");
    addManagedPlugin("a.b.c", "b");

    executeRuleAndCheckReport(false);
  }

  private void addManagedPlugin(String groupId, String artifactId) {
    String defaultVersion = "1.0";
    PluginModel pluginModel = new PluginModel(groupId, artifactId, defaultVersion);
    Plugin mavenPlugin = new Plugin();
    mavenPlugin.setGroupId(groupId);
    mavenPlugin.setArtifactId(artifactId);
    mavenPlugin.setVersion(defaultVersion);

    this.projectModel.getManagedPlugins().add(pluginModel);
    this.mockMavenProject.getPluginManagement().getPlugins().add(mavenPlugin);
  }
}
