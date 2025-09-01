/*
 * Copyright (c) 2012 - 2025 the original author or authors.
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
import com.github.ferstl.maven.pomenforcers.model.DependencyScope;


/**
 * <p>
 * Abstract test for {@link PedanticDependencyOrderEnforcer} and
 * {@link PedanticDependencyManagementOrderEnforcer}. Both classes do the same thing but one works
 * on the managed dependencies and one on the dependencies themselves.
 * </p>
 */
abstract class AbstractPedanticDependencyOrderEnforcerTest<T extends AbstractPedanticDependencyOrderEnforcer>
    extends AbstractPedanticEnforcerTest<T> {

  private DependencyAdder dependencyAdder;

  @BeforeEach
  void setupDependencyAdder() {
    this.dependencyAdder = createDependencyAdder();
  }

  protected abstract DependencyAdder createDependencyAdder();

  @Test
  void defaultSettingsCorrect() {
    this.dependencyAdder.addDependency("d.e.f", "a", DependencyScope.IMPORT);
    this.dependencyAdder.addDependency("d.e.f", "b", DependencyScope.IMPORT);

    this.dependencyAdder.addDependency("a.b.c", "a", DependencyScope.COMPILE);
    this.dependencyAdder.addDependency("a.b.c", "b", DependencyScope.COMPILE);

    this.dependencyAdder.addDependency("g.h.i", "a", DependencyScope.PROVIDED);
    this.dependencyAdder.addDependency("g.h.i", "b", DependencyScope.PROVIDED);

    this.dependencyAdder.addDependency("j.k.l", "a", DependencyScope.SYSTEM);
    this.dependencyAdder.addDependency("j.k.l", "b", DependencyScope.SYSTEM);

    this.dependencyAdder.addDependency("m.n.o", "a", DependencyScope.TEST);
    this.dependencyAdder.addDependency("m.n.o", "b", DependencyScope.TEST);

    executeRuleAndCheckReport(false);
  }

  @Test
  void defaultSettingsWrongScopeOrder() {
    // Test before compile
    this.dependencyAdder.addDependency("a.b.c", "a", DependencyScope.TEST);
    this.dependencyAdder.addDependency("x.y.z", "z", DependencyScope.COMPILE);

    executeRuleAndCheckReport(true);
  }

  @Test
  void defaultSettingsWrongGroupIdOrder() {
    this.dependencyAdder.addDependency("d.e.f", "a", DependencyScope.COMPILE);
    this.dependencyAdder.addDependency("a.b.c", "a", DependencyScope.COMPILE);

    executeRuleAndCheckReport(true);
  }

  @Test
  void defaultSettingsWrongArtifactIdOrder() {
    this.dependencyAdder.addDependency("a.b.c", "b", DependencyScope.COMPILE);
    this.dependencyAdder.addDependency("a.b.c", "a", DependencyScope.COMPILE);

    executeRuleAndCheckReport(true);
  }

  @Test
  void groupIdPriorities() {
    this.testRule.setGroupIdPriorities("u.v.w,x.y.z");

    this.dependencyAdder.addDependency("u.v.w", "z", DependencyScope.COMPILE);
    this.dependencyAdder.addDependency("x.y.z", "z", DependencyScope.COMPILE);
    this.dependencyAdder.addDependency("a.b.c", "a", DependencyScope.COMPILE);

    executeRuleAndCheckReport(false);
  }


  @Test
  void artifactIdPriorities() {
    this.testRule.setArtifactIdPriorities("z,y");

    this.dependencyAdder.addDependency("a.b.c", "z", DependencyScope.COMPILE);
    this.dependencyAdder.addDependency("a.b.c", "y", DependencyScope.COMPILE);
    this.dependencyAdder.addDependency("a.b.c", "a", DependencyScope.COMPILE);

    executeRuleAndCheckReport(false);
  }

  @Test
  void scopePriorities() {
    this.testRule.setScopePriorities("system,compile");

    this.dependencyAdder.addDependency("x.y.z", "z", DependencyScope.SYSTEM);
    this.dependencyAdder.addDependency("a.b.c", "a", DependencyScope.COMPILE);

    executeRuleAndCheckReport(false);
  }

  @Test
  void orderBy() {
    this.testRule.setOrderBy("groupId,artifactId");

    this.dependencyAdder.addDependency("a.b.c", "a", DependencyScope.TEST);
    this.dependencyAdder.addDependency("a.b.c", "b", DependencyScope.COMPILE);

    executeRuleAndCheckReport(false);
  }

  @FunctionalInterface
  interface DependencyAdder {

    void addDependency(String groupId, String artifactId, DependencyScope scope);
  }
}
