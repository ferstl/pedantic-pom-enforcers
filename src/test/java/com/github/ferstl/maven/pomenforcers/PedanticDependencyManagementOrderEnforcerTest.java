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
public class PedanticDependencyManagementOrderEnforcerTest extends AbstractPedanticEnforcerTest<PedanticDependencyManagementOrderEnforcer> {

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

  @Test
  public void defaultSettingsCorrect() {
    addManagedDependency("a.b.c", "a", DependencyScope.COMPILE);
    addManagedDependency("a.b.c", "b", DependencyScope.COMPILE);

    addManagedDependency("d.e.f", "a", DependencyScope.IMPORT);
    addManagedDependency("d.e.f", "b", DependencyScope.IMPORT);

    addManagedDependency("g.h.i", "a", DependencyScope.PROVIDED);
    addManagedDependency("g.h.i", "b", DependencyScope.PROVIDED);

    addManagedDependency("j.k.l", "a", DependencyScope.SYSTEM);
    addManagedDependency("j.k.l", "b", DependencyScope.SYSTEM);

    addManagedDependency("m.n.o", "a", DependencyScope.TEST);
    addManagedDependency("m.n.o", "b", DependencyScope.TEST);

    executeRuleAndCheckReport(false);
  }

  @Test
  public void defaultSettingsWrongScopeOrder() {
    // Test before compile
    addManagedDependency("a.b.c", "a", DependencyScope.TEST);
    addManagedDependency("x.y.z", "z", DependencyScope.COMPILE);

    executeRuleAndCheckReport(true);
  }

  @Test
  public void defaultSettingsWrongGroupIdOrder() {
    addManagedDependency("d.e.f", "a", DependencyScope.COMPILE);
    addManagedDependency("a.b.c", "a", DependencyScope.COMPILE);

    executeRuleAndCheckReport(true);
  }

  @Test
  public void defaultSettingsWrongArtifactIdOrder() {
    addManagedDependency("a.b.c", "b", DependencyScope.COMPILE);
    addManagedDependency("a.b.c", "a", DependencyScope.COMPILE);

    executeRuleAndCheckReport(true);
  }

  @Test
  public void groupIdPriorities() {
    this.testRule.setGroupIdPriorities("u.v.w,x.y.z");

    addManagedDependency("u.v.w", "z", DependencyScope.COMPILE);
    addManagedDependency("x.y.z", "z", DependencyScope.COMPILE);
    addManagedDependency("a.b.c", "a", DependencyScope.COMPILE);

    executeRuleAndCheckReport(false);
  }


  @Test
  public void artifactIdPriorities() {
    this.testRule.setArtifactIdPriorities("z,y");

    addManagedDependency("a.b.c", "z", DependencyScope.COMPILE);
    addManagedDependency("a.b.c", "y", DependencyScope.COMPILE);
    addManagedDependency("a.b.c", "a", DependencyScope.COMPILE);

    executeRuleAndCheckReport(false);
  }

  @Test
  public void scopePriorities() {
    this.testRule.setScopePriorities("system,compile");

    addManagedDependency("x.y.z", "z", DependencyScope.SYSTEM);
    addManagedDependency("a.b.c", "a", DependencyScope.COMPILE);

    executeRuleAndCheckReport(false);
  }

  @Test
  public void orderBy() {
    this.testRule.setOrderBy("groupId,artifactId");

    addManagedDependency("a.b.c", "a", DependencyScope.TEST);
    addManagedDependency("a.b.c", "b", DependencyScope.COMPILE);

    executeRuleAndCheckReport(false);
  }
}
