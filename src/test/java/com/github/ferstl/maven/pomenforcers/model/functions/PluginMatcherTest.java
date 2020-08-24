package com.github.ferstl.maven.pomenforcers.model.functions;

import org.apache.maven.enforcer.rule.api.EnforcerRuleHelper;
import org.apache.maven.model.Plugin;
import org.junit.Before;
import org.junit.Test;
import com.github.ferstl.maven.pomenforcers.model.PluginModel;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class PluginMatcherTest {

  private PluginMatcher pluginMatcher;

  @Before
  public void before() {
    this.pluginMatcher = new PluginMatcher(mock(EnforcerRuleHelper.class));
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
