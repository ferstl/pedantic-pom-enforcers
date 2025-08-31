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

import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import com.github.ferstl.maven.pomenforcers.model.DependencyModel;
import com.github.ferstl.maven.pomenforcers.model.PluginModel;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * JUnit tests for {@link PedanticPluginConfigurationEnforcer}.
 */
@TestInstance(Lifecycle.PER_CLASS)
class PedanticPluginConfigurationEnforcerTest extends AbstractPedanticEnforcerTest<PedanticPluginConfigurationEnforcer> {

  @Override
  PedanticPluginConfigurationEnforcer createRule() {
    return new PedanticPluginConfigurationEnforcer(this.mockMavenProject, this.mockHelper);
  }

  @Override
  @Test
  void getDescription() {
    assertThat(this.testRule.getDescription()).isEqualTo(PedanticEnforcerRule.PLUGIN_CONFIGURATION);
  }

  @Override
  @Test
  void accept() {
    PedanticEnforcerVisitor visitor = mock(PedanticEnforcerVisitor.class);
    this.testRule.accept(visitor);

    verify(visitor).visit(this.testRule);
  }

  @Test
  void defaultSettingsCorrect() {
    addPlugin(false, false, false);

    executeRuleAndCheckReport(false);
  }

  @Test
  void allowedUnmanagedConfiguration() {
    this.testRule.setManageConfigurations(false);
    addPlugin(false, true, false);

    executeRuleAndCheckReport(false);
  }

  @Test
  void forbiddenUnmanagedConfiguration() {
    this.testRule.setManageConfigurations(true);
    addPlugin(false, true, false);

    executeRuleAndCheckReport(true);
  }

  @Test
  void allowedUnmanagedDependencies() {
    this.testRule.setManageDependencies(false);
    addPlugin(false, false, true);

    executeRuleAndCheckReport(false);
  }

  @Test
  void forbiddenUnmanagedDependencies() {
    this.testRule.setManageDependencies(true);
    addPlugin(false, false, true);

    executeRuleAndCheckReport(true);
  }

  @Test
  void allowedManagedVersion() {
    this.testRule.setManageVersions(false);
    addPlugin(true, false, false);

    executeRuleAndCheckReport(false);
  }

  @Test
  void forbiddenManagedVersion() {
    this.testRule.setManageVersions(true);
    addPlugin(true, false, false);

    executeRuleAndCheckReport(true);
  }

  @Test
  void allowedProjectVersion1() {
    this.testRule.setAllowUnmanagedProjectVersions(true);
    PluginModel plugin = addPlugin(false, false, false);
    when(plugin.getVersion()).thenReturn("${project.version}");

    executeRuleAndCheckReport(false);
  }

  @Test
  void allowedProjectVersion2() {
    PluginModel plugin = addPlugin(false, false, false);
    when(plugin.getVersion()).thenReturn("${version}");

    executeRuleAndCheckReport(false);
  }

  @Test
  void forbiddenProjectVersion() {
    this.testRule.setAllowUnmanagedProjectVersions(false);
    PluginModel plugin = addPlugin(false, false, false);
    when(plugin.getVersion()).thenReturn("${project.version}");

    executeRuleAndCheckReport(true);
  }

  @Test
  void allowedVersionWithProps() {
    this.testRule.setAllowedUnmanagedProjectVersionProperties("some.version");
    addPlugin(false, false, false);

    executeRuleAndCheckReport(false);
  }

  @Test
  void allowedVersionWithDisabledProps1() {
    this.testRule.setManageVersions(false);
    this.testRule.setAllowedUnmanagedProjectVersionProperties("some.version");
    addPlugin(true, false, false);

    executeRuleAndCheckReport(false);
  }

  @Test
  void allowedVersionWithDisabledProps2() {
    this.testRule.setAllowUnmanagedProjectVersions(false);
    this.testRule.setAllowedUnmanagedProjectVersionProperties("some.version");
    addPlugin(false, false, false);

    executeRuleAndCheckReport(false);
  }

  @Test
  void forbiddenVersionWithCustomProps1() {
    this.testRule.setAllowedUnmanagedProjectVersionProperties("some.version");
    addPlugin(true, false, false);

    executeRuleAndCheckReport(true);
  }

  @Test
  void forbiddenVersionWithCustomProps2() {
    this.testRule.setAllowedUnmanagedProjectVersionProperties("some.version");
    PluginModel plugin = addPlugin(true, false, false);
    when(plugin.getVersion()).thenReturn("${project.version}");

    executeRuleAndCheckReport(true);
  }

  @Test
  void allowedVersionWithActiveCustomProps1() {
    this.testRule.setAllowedUnmanagedProjectVersionProperties("some.version");
    PluginModel plugin = addPlugin(true, false, false);
    when(plugin.getVersion()).thenReturn("${some.version}");

    executeRuleAndCheckReport(false);
  }

  @Test
  void allowedVersionWithActiveCustomProps2() {
    this.testRule.setAllowedUnmanagedProjectVersionProperties("some.version,some.other.version");
    PluginModel plugin = addPlugin(true, false, false);
    when(plugin.getVersion()).thenReturn("${some.other.version}");

    executeRuleAndCheckReport(false);
  }

  private PluginModel addPlugin(boolean withVersion, boolean withConfiguration, boolean withDependencies) {
    PluginModel plugin = mock(PluginModel.class);

    when(plugin.getGroupId()).thenReturn("a.b.c");
    when(plugin.getArtifactId()).thenReturn("a");

    if (withVersion) {
      when(plugin.getVersion()).thenReturn("1.0");
    }

    if (withConfiguration) {
      when(plugin.isConfigured()).thenReturn(true);
    }

    if (withDependencies) {
      when(plugin.getDependencies()).thenReturn(
          Collections.singletonList(new DependencyModel("x.y.z", "z", "1.0", null, null, null)));
    }

    this.testRule.getProjectModel().getPlugins().add(plugin);
    return plugin;
  }
}
