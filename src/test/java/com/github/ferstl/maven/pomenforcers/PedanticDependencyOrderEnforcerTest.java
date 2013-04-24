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
public class PedanticDependencyOrderEnforcerTest extends AbstractPedanticDependencyOrderEnforcerTest<PedanticDependencyOrderEnforcer> {

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

  @Override
  public DependencyAdder createDependencyAdder() {
    return new DependencyAdder() {

      @Override
      public void addDependency(String groupId, String artifactId, DependencyScope scope) {
        PedanticDependencyOrderEnforcerTest.this.addDependency(groupId, artifactId, scope);
      }};
  }

}
