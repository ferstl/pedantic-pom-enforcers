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

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * JUnit tests for {@link PedanticDependencyOrderEnforcer}.
 */
class PedanticDependencyOrderEnforcerTest extends AbstractPedanticDependencyOrderEnforcerTest<PedanticDependencyOrderEnforcer> {

  @Override
  PedanticDependencyOrderEnforcer createRule() {
    return new PedanticDependencyOrderEnforcer(this.mockMavenProject, this.mockHelper);
  }

  @Override
  @Test
  void getDescription() {
    assertThat(this.testRule.getDescription()).isEqualTo(PedanticEnforcerRule.DEPENDENCY_ORDER);
  }

  @Override
  @Test
  void accept() {
    PedanticEnforcerVisitor visitor = mock(PedanticEnforcerVisitor.class);
    this.testRule.accept(visitor);

    verify(visitor).visit(this.testRule);
  }

  @Override
  protected DependencyAdder createDependencyAdder() {
    return this::addDependency;
  }

}
