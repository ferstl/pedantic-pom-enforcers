package com.github.ferstl.maven.pomenforcers;

import org.junit.Before;
import org.junit.Test;

import com.github.ferstl.maven.pomenforcers.model.DependencyModel;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * JUnit tests for {@link PedanticDependencyManagementLocationEnforcer}.
 */
public class PedanticDependencyManagementLocationEnforcerTest extends AbstractPedanticEnforcerTest<PedanticDependencyManagementLocationEnforcer> {

  @Override
  PedanticDependencyManagementLocationEnforcer createRule() {
    return new PedanticDependencyManagementLocationEnforcer();
  }

  @Before
  public void before() {
    when(this.mockMavenProject.getGroupId()).thenReturn("a.b.c");
    when(this.mockMavenProject.getArtifactId()).thenReturn("parent");
    this.projectModel.getManagedDependencies().add(new DependencyModel("a.b.c", "a", "1.0", null, null, null));
  }

  @Override
  @Test
  public void getDescription() {
    assertThat(this.testRule.getDescription(), equalTo(PedanticEnforcerRule.DEPENDENCY_MANAGEMENT_LOCATION));
  }

  @Override
  @Test
  public void accept() {
    PedanticEnforcerVisitor visitor = mock(PedanticEnforcerVisitor.class);
    this.testRule.accept(visitor);

    verify(visitor).visit(this.testRule);
  }

  @Test
  public void noDependencyManagingPomsDeclared() {
    executeRuleAndCheckReport(false);
  }

  @Test
  public void noDependencyManagementDeclared() {
    this.projectModel.getManagedDependencies().clear();

    executeRuleAndCheckReport(false);
  }

  @Test
  public void isDependencyManagingPom() {
    this.testRule.setDependencyManagingPoms("a.b.c:parent");

    executeRuleAndCheckReport(false);
  }

  @Test
  public void isNotDependencyManagingPom() {
    this.testRule.setDependencyManagingPoms("some.other:pom");

    executeRuleAndCheckReport(true);
  }

}
