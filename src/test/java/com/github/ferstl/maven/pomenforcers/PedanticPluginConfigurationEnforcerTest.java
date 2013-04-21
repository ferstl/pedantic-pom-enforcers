package com.github.ferstl.maven.pomenforcers;

import java.util.Arrays;

import org.junit.Test;

import com.github.ferstl.maven.pomenforcers.model.DependencyModel;
import com.github.ferstl.maven.pomenforcers.model.PluginModel;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * JUnit tests for {@link PedanticPluginConfigurationEnforcer}.
 */
public class PedanticPluginConfigurationEnforcerTest extends AbstractPedanticEnforcerTest<PedanticPluginConfigurationEnforcer> {

  @Override
  PedanticPluginConfigurationEnforcer createRule() {
    return new PedanticPluginConfigurationEnforcer();
  }

  @Override
  @Test
  public void getDescription() {
    assertThat(this.testRule.getDescription(), equalTo(PedanticEnforcerRule.PLUGIN_CONFIGURATION));
  }

  @Override
  @Test
  public void accept() {
    PedanticEnforcerVisitor visitor = mock(PedanticEnforcerVisitor.class);
    this.testRule.accept(visitor);

    verify(visitor).visit(this.testRule);
  }

  @Test
  public void defaultSettingsCorrect() {
    addPlugin(false, false, false);

    executeRuleAndCheckReport(false);
  }

  @Test
  public void allowedUnmanagedConfiguration() {
    this.testRule.setManageConfigurations(false);
    addPlugin(false, true, false);

    executeRuleAndCheckReport(false);
  }

  @Test
  public void forbiddenUnmanagedConfiguration() {
    this.testRule.setManageConfigurations(true);
    addPlugin(false, true, false);

    executeRuleAndCheckReport(true);
  }

  @Test
  public void allowedUnmanagedDependencies() {
    this.testRule.setManageDependencies(false);
    addPlugin(false, false, true);

    executeRuleAndCheckReport(false);
  }

  @Test
  public void forbiddenUnmanagedDependencies() {
    this.testRule.setManageDependencies(true);
    addPlugin(false, false, true);

    executeRuleAndCheckReport(true);
  }

  @Test
  public void allowedManagedVersion() {
    this.testRule.setManageVersions(false);
    addPlugin(true, false, false);

    executeRuleAndCheckReport(false);
  }

  @Test
  public void forbiddenManagedVersion() {
    this.testRule.setManageVersions(true);
    addPlugin(true, false, false);

    executeRuleAndCheckReport(true);
  }

  private void addPlugin(boolean withVersion, boolean withConfiguration, boolean withDependencies) {
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
          Arrays.asList(new DependencyModel("x.y.z", "z", "1.0", null, null, null)));
    }

    this.testRule.getProjectModel().getPlugins().add(plugin);
  }
}
