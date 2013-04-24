package com.github.ferstl.maven.pomenforcers;

import org.junit.Before;
import org.junit.Test;

import com.github.ferstl.maven.pomenforcers.model.DependencyScope;


/**
 * <p>
 * Abstract test for {@link PedanticDependencyOrderEnforcer} and
 * {@link PedanticDependencyManagementOrderEnforcer}. Both classes do the same thing but one works
 * on the managed dependencies and one on the dependencies themselves.
 * </p>
 */
public abstract class AbstractPedanticDependencyOrderEnforcerTest<T extends AbstractPedanticDependencyOrderEnforcer>
extends AbstractPedanticEnforcerTest<T> {

  private DependencyAdder dependencyAdder;

  @Before
  public void setupDependencyAdder() {
    this.dependencyAdder = createDependencyAdder();
  }

  public abstract DependencyAdder createDependencyAdder();

  @Test
  public void defaultSettingsCorrect() {
    this.dependencyAdder.addDependency("a.b.c", "a", DependencyScope.COMPILE);
    this.dependencyAdder.addDependency("a.b.c", "b", DependencyScope.COMPILE);

    this.dependencyAdder.addDependency("d.e.f", "a", DependencyScope.IMPORT);
    this.dependencyAdder.addDependency("d.e.f", "b", DependencyScope.IMPORT);

    this.dependencyAdder.addDependency("g.h.i", "a", DependencyScope.PROVIDED);
    this.dependencyAdder.addDependency("g.h.i", "b", DependencyScope.PROVIDED);

    this.dependencyAdder.addDependency("j.k.l", "a", DependencyScope.SYSTEM);
    this.dependencyAdder.addDependency("j.k.l", "b", DependencyScope.SYSTEM);

    this.dependencyAdder.addDependency("m.n.o", "a", DependencyScope.TEST);
    this.dependencyAdder.addDependency("m.n.o", "b", DependencyScope.TEST);

    executeRuleAndCheckReport(false);
  }

  @Test
  public void defaultSettingsWrongScopeOrder() {
    // Test before compile
    this.dependencyAdder.addDependency("a.b.c", "a", DependencyScope.TEST);
    this.dependencyAdder.addDependency("x.y.z", "z", DependencyScope.COMPILE);

    executeRuleAndCheckReport(true);
  }

  @Test
  public void defaultSettingsWrongGroupIdOrder() {
    this.dependencyAdder.addDependency("d.e.f", "a", DependencyScope.COMPILE);
    this.dependencyAdder.addDependency("a.b.c", "a", DependencyScope.COMPILE);

    executeRuleAndCheckReport(true);
  }

  @Test
  public void defaultSettingsWrongArtifactIdOrder() {
    this.dependencyAdder.addDependency("a.b.c", "b", DependencyScope.COMPILE);
    this.dependencyAdder.addDependency("a.b.c", "a", DependencyScope.COMPILE);

    executeRuleAndCheckReport(true);
  }

  @Test
  public void groupIdPriorities() {
    this.testRule.setGroupIdPriorities("u.v.w,x.y.z");

    this.dependencyAdder.addDependency("u.v.w", "z", DependencyScope.COMPILE);
    this.dependencyAdder.addDependency("x.y.z", "z", DependencyScope.COMPILE);
    this.dependencyAdder.addDependency("a.b.c", "a", DependencyScope.COMPILE);

    executeRuleAndCheckReport(false);
  }


  @Test
  public void artifactIdPriorities() {
    this.testRule.setArtifactIdPriorities("z,y");

    this.dependencyAdder.addDependency("a.b.c", "z", DependencyScope.COMPILE);
    this.dependencyAdder.addDependency("a.b.c", "y", DependencyScope.COMPILE);
    this.dependencyAdder.addDependency("a.b.c", "a", DependencyScope.COMPILE);

    executeRuleAndCheckReport(false);
  }

  @Test
  public void scopePriorities() {
    this.testRule.setScopePriorities("system,compile");

    this.dependencyAdder.addDependency("x.y.z", "z", DependencyScope.SYSTEM);
    this.dependencyAdder.addDependency("a.b.c", "a", DependencyScope.COMPILE);

    executeRuleAndCheckReport(false);
  }

  @Test
  public void orderBy() {
    this.testRule.setOrderBy("groupId,artifactId");

    this.dependencyAdder.addDependency("a.b.c", "a", DependencyScope.TEST);
    this.dependencyAdder.addDependency("a.b.c", "b", DependencyScope.COMPILE);

    executeRuleAndCheckReport(false);
  }

  static interface DependencyAdder {
    void addDependency(String groupId, String artifactId, DependencyScope scope);
  }
}
