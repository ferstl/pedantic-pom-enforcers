package com.github.ferstl.maven.pomenforcers;

import java.lang.invoke.MethodHandle;

import org.junit.Before;
import org.junit.Test;

import com.github.ferstl.maven.pomenforcers.model.DependencyScope;

import static org.junit.Assert.fail;


/**
 * Abstract test to test subclasses of {@link AbstractPedanticDependencyOrderEnforcer}. Because of
 * the base class' generic type it is a bit tricky to create this abstract test. The only
 * solution that came to my mind at this time of the day was to use {@link MethodHandle}s to add mock dependencies.
 */
public abstract class AbstractPedanticDependencyOrderEnforcerTest<T extends AbstractPedanticDependencyOrderEnforcer>
extends AbstractPedanticEnforcerTest<T> {

  private MethodHandle dependencyAdder;

  @Before
  public void setupDependencyAdder() throws Exception {
    this.dependencyAdder = createDependencyAdder();
  }

  public abstract MethodHandle createDependencyAdder() throws Exception;

  @Test
  public void defaultSettingsCorrect() {
    invokeDependencyAdder("a.b.c", "a", DependencyScope.COMPILE);
    invokeDependencyAdder("a.b.c", "b", DependencyScope.COMPILE);

    invokeDependencyAdder("d.e.f", "a", DependencyScope.IMPORT);
    invokeDependencyAdder("d.e.f", "b", DependencyScope.IMPORT);

    invokeDependencyAdder("g.h.i", "a", DependencyScope.PROVIDED);
    invokeDependencyAdder("g.h.i", "b", DependencyScope.PROVIDED);

    invokeDependencyAdder("j.k.l", "a", DependencyScope.SYSTEM);
    invokeDependencyAdder("j.k.l", "b", DependencyScope.SYSTEM);

    invokeDependencyAdder("m.n.o", "a", DependencyScope.TEST);
    invokeDependencyAdder("m.n.o", "b", DependencyScope.TEST);

    executeRuleAndCheckReport(false);
  }

  @Test
  public void defaultSettingsWrongScopeOrder() {
    // Test before compile
    invokeDependencyAdder("a.b.c", "a", DependencyScope.TEST);
    invokeDependencyAdder("x.y.z", "z", DependencyScope.COMPILE);

    executeRuleAndCheckReport(true);
  }

  @Test
  public void defaultSettingsWrongGroupIdOrder() {
    invokeDependencyAdder("d.e.f", "a", DependencyScope.COMPILE);
    invokeDependencyAdder("a.b.c", "a", DependencyScope.COMPILE);

    executeRuleAndCheckReport(true);
  }

  @Test
  public void defaultSettingsWrongArtifactIdOrder() {
    invokeDependencyAdder("a.b.c", "b", DependencyScope.COMPILE);
    invokeDependencyAdder("a.b.c", "a", DependencyScope.COMPILE);

    executeRuleAndCheckReport(true);
  }

  @Test
  public void groupIdPriorities() {
    this.testRule.setGroupIdPriorities("u.v.w,x.y.z");

    invokeDependencyAdder("u.v.w", "z", DependencyScope.COMPILE);
    invokeDependencyAdder("x.y.z", "z", DependencyScope.COMPILE);
    invokeDependencyAdder("a.b.c", "a", DependencyScope.COMPILE);

    executeRuleAndCheckReport(false);
  }


  @Test
  public void artifactIdPriorities() {
    this.testRule.setArtifactIdPriorities("z,y");

    invokeDependencyAdder("a.b.c", "z", DependencyScope.COMPILE);
    invokeDependencyAdder("a.b.c", "y", DependencyScope.COMPILE);
    invokeDependencyAdder("a.b.c", "a", DependencyScope.COMPILE);

    executeRuleAndCheckReport(false);
  }

  @Test
  public void scopePriorities() {
    this.testRule.setScopePriorities("system,compile");

    invokeDependencyAdder("x.y.z", "z", DependencyScope.SYSTEM);
    invokeDependencyAdder("a.b.c", "a", DependencyScope.COMPILE);

    executeRuleAndCheckReport(false);
  }

  @Test
  public void orderBy() {
    this.testRule.setOrderBy("groupId,artifactId");

    invokeDependencyAdder("a.b.c", "a", DependencyScope.TEST);
    invokeDependencyAdder("a.b.c", "b", DependencyScope.COMPILE);

    executeRuleAndCheckReport(false);
  }

  private void invokeDependencyAdder(String groupId, String artifactId, DependencyScope scope) {
    try {
      this.dependencyAdder.invoke(this, groupId, artifactId, scope);
    } catch (Throwable e) {
      fail("Could not add dependency: " + e.getMessage());
    }
  }
}
