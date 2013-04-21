package com.github.ferstl.maven.pomenforcers;

import org.junit.Before;
import org.junit.Test;

import com.github.ferstl.maven.pomenforcers.model.PluginModel;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * JUnit tests for {@link PedanticPluginManagementLocationEnforcer}.
 */
public class PedanticPluginManagementLocationEnforcerTest extends AbstractPedanticEnforcerTest<PedanticPluginManagementLocationEnforcer> {

  @Override
  PedanticPluginManagementLocationEnforcer createRule() {
    return new PedanticPluginManagementLocationEnforcer();
  }

  @Before
  public void before() {
    when(this.mockMavenProject.getGroupId()).thenReturn("a.b.c");
    when(this.mockMavenProject.getArtifactId()).thenReturn("parent");
    this.projectModel.getManagedPlugins().add(new PluginModel("a.b.c", "a", "1.0"));
  }

  @Override
  @Test
  public void getDescription() {
    assertThat(this.testRule.getDescription(), equalTo(PedanticEnforcerRule.PLUGIN_MANAGEMENT_LOCATION));
  }

  @Override
  @Test
  public void accept() {
    PedanticEnforcerVisitor visitor = mock(PedanticEnforcerVisitor.class);
    this.testRule.accept(visitor);

    verify(visitor).visit(this.testRule);
  }

  @Test
  public void noPluginManagingPomsDeclared() {
    executeRuleAndCheckReport(false);
  }

  @Test
  public void isPluginManagingPom() {
    this.testRule.setPluginManagingPoms("a.b.c:parent");

    executeRuleAndCheckReport(false);
  }

  @Test
  public void isNotPluginManagingPom() {
    this.testRule.setPluginManagingPoms("some.other:pom");

    executeRuleAndCheckReport(true);
  }
}
