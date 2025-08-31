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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.github.ferstl.maven.pomenforcers.model.PluginModel;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * JUnit tests for {@link PedanticPluginManagementLocationEnforcer}.
 */
class PedanticPluginManagementLocationEnforcerTest extends AbstractPedanticEnforcerTest<PedanticPluginManagementLocationEnforcer> {

  @Override
  PedanticPluginManagementLocationEnforcer createRule() {
    return new PedanticPluginManagementLocationEnforcer(this.mockMavenProject, this.mockHelper);
  }

  @BeforeEach
  void before() {
    when(this.mockMavenProject.getGroupId()).thenReturn("a.b.c");
    when(this.mockMavenProject.getArtifactId()).thenReturn("parent");
    this.projectModel.getManagedPlugins().add(new PluginModel("a.b.c", "a", "1.0"));
  }

  @Override
  @Test
  void getDescription() {
    assertThat(this.testRule.getDescription()).isEqualTo(PedanticEnforcerRule.PLUGIN_MANAGEMENT_LOCATION);
  }

  @Override
  @Test
  void accept() {
    PedanticEnforcerVisitor visitor = mock(PedanticEnforcerVisitor.class);
    this.testRule.accept(visitor);

    verify(visitor).visit(this.testRule);
  }

  @Test
  void noPluginManagingPomsDeclared() {
    executeRuleAndCheckReport(false);
  }

  @Test
  void isPluginManagingPom() {
    this.testRule.setPluginManagingPoms("a.b.c:parent");

    executeRuleAndCheckReport(false);
  }

  @Test
  void isNotPluginManagingPom() {
    this.testRule.setPluginManagingPoms("some.other:pom");

    executeRuleAndCheckReport(true);
  }

  void pluginManagementAllowedInParentPom() {
    when(this.mockMavenProject.getPackaging()).thenReturn("pom");

    executeRuleAndCheckReport(true);
  }

  void pluginManagementNotAllowedInParentPom() {
    when(this.mockMavenProject.getPackaging()).thenReturn("pom");
    this.testRule.setAllowParentPoms(false);

    executeRuleAndCheckReport(true);
  }

  @Test
  void pluginManagementInNonParentPom() {
    when(this.mockMavenProject.getPackaging()).thenReturn("jar");

    executeRuleAndCheckReport(false);
  }
}
