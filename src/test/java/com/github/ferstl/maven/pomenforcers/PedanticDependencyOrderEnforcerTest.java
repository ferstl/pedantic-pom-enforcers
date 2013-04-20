/*
 * Copyright (c) 2013 by Stefan Ferstl <st.ferstl@gmail.com>
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

import org.junit.Test;

import com.github.ferstl.maven.pomenforcers.model.DependencyScope;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * JUnit tests for {@link PedanticDependencyOrderEnforcer}.
 */
public class PedanticDependencyOrderEnforcerTest extends AbstractPedanticEnforcerTest<PedanticDependencyOrderEnforcer> {

  @Override
  PedanticDependencyOrderEnforcer createRule() {
    return new PedanticDependencyOrderEnforcer();
  }

  @Override
  @Test
  public void getDescription() {
    assertThat(this.testRule.getDescription(), equalTo(PedanticEnforcerRule.DEPENDENCY_ORDER));
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
    addDependency("a.b.c", "a", DependencyScope.COMPILE);
    addDependency("a.b.c", "b", DependencyScope.COMPILE);

    addDependency("d.e.f", "a", DependencyScope.IMPORT);
    addDependency("d.e.f", "b", DependencyScope.IMPORT);

    addDependency("g.h.i", "a", DependencyScope.PROVIDED);
    addDependency("g.h.i", "b", DependencyScope.PROVIDED);

    addDependency("j.k.l", "a", DependencyScope.SYSTEM);
    addDependency("j.k.l", "b", DependencyScope.SYSTEM);

    addDependency("m.n.o", "a", DependencyScope.TEST);
    addDependency("m.n.o", "b", DependencyScope.TEST);

    executeRuleAndCheckReport(false);
  }

  @Test
  public void defaultSettingsWrongScopeOrder() {
    // Test before compile
    addDependency("a.b.c", "a", DependencyScope.TEST);
    addDependency("x.y.z", "z", DependencyScope.COMPILE);

    executeRuleAndCheckReport(true);
  }

  @Test
  public void defaultSettingsWrongGroupIdOrder() {
    addDependency("d.e.f", "a", DependencyScope.COMPILE);
    addDependency("a.b.c", "a", DependencyScope.COMPILE);

    executeRuleAndCheckReport(true);
  }

  @Test
  public void defaultSettingsWrongArtifactIdOrder() {
    addDependency("a.b.c", "b", DependencyScope.COMPILE);
    addDependency("a.b.c", "a", DependencyScope.COMPILE);

    executeRuleAndCheckReport(true);
  }

  @Test
  public void groupIdPriorities() {
    this.testRule.setGroupIdPriorities("u.v.w,x.y.z");

    addDependency("u.v.w", "z", DependencyScope.COMPILE);
    addDependency("x.y.z", "z", DependencyScope.COMPILE);
    addDependency("a.b.c", "a", DependencyScope.COMPILE);

    executeRuleAndCheckReport(false);
  }


  @Test
  public void artifactIdPriorities() {
    this.testRule.setArtifactIdPriorities("z,y");

    addDependency("a.b.c", "z", DependencyScope.COMPILE);
    addDependency("a.b.c", "y", DependencyScope.COMPILE);
    addDependency("a.b.c", "a", DependencyScope.COMPILE);

    executeRuleAndCheckReport(false);
  }

  @Test
  public void scopePriorities() {
    this.testRule.setScopePriorities("system,compile");

    addDependency("x.y.z", "z", DependencyScope.SYSTEM);
    addDependency("a.b.c", "a", DependencyScope.COMPILE);

    executeRuleAndCheckReport(false);
  }

  @Test
  public void orderBy() {
    this.testRule.setOrderBy("groupId,artifactId");

    addDependency("a.b.c", "a", DependencyScope.TEST);
    addDependency("a.b.c", "b", DependencyScope.COMPILE);

    executeRuleAndCheckReport(false);
  }
}
