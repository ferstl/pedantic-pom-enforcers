package com.github.ferstl.maven.pomenforcers;

import org.junit.Test;

import com.github.ferstl.maven.pomenforcers.model.DependencyScope;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * JUnit tests for {@link PedanticDependencyManagementOrderEnforcer}:
 */
public class PedanticDependencyManagementOrderEnforcerTest
extends AbstractPedanticDependencyOrderEnforcerTest<PedanticDependencyManagementOrderEnforcer> {

  @Override
  PedanticDependencyManagementOrderEnforcer createRule() {
    return new PedanticDependencyManagementOrderEnforcer();
  }

  @Test
  @Override
  public void getDescription() {
    assertThat(this.testRule.getDescription(), equalTo(PedanticEnforcerRule.DEPENDENCY_MANAGEMENT_ORDER));
  }

  @Test
  @Override
  public void accept() {
    PedanticEnforcerVisitor visitor = mock(PedanticEnforcerVisitor.class);
    this.testRule.accept(visitor);

    verify(visitor).visit(this.testRule);
  }

  @Override
  public DependencyAdder createDependencyAdder() {
    return new DependencyAdder() {

      @Override
      public void addDependency(String groupId, String artifactId, DependencyScope scope) {
        PedanticDependencyManagementOrderEnforcerTest.this.addManagedDependency(groupId, artifactId, scope);
      }

    };
  }
}
