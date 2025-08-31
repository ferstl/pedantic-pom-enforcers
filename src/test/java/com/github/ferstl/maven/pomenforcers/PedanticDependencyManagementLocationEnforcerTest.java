/*
 * Copyright (c) 2012 - 2023 the original author or authors.
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
import com.github.ferstl.maven.pomenforcers.model.DependencyModel;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * JUnit tests for {@link PedanticDependencyManagementLocationEnforcer}.
 */
class PedanticDependencyManagementLocationEnforcerTest extends AbstractPedanticEnforcerTest<PedanticDependencyManagementLocationEnforcer> {

  @Override
  PedanticDependencyManagementLocationEnforcer createRule() {
    return new PedanticDependencyManagementLocationEnforcer(this.mockMavenProject, this.mockHelper);
  }

  @BeforeEach
  void before() {
    when(this.mockMavenProject.getGroupId()).thenReturn("a.b.c");
    when(this.mockMavenProject.getArtifactId()).thenReturn("parent");
    this.projectModel.getManagedDependencies().add(new DependencyModel("a.b.c", "a", "1.0", null, null, null));
  }

  @Override
  @Test
  void getDescription() {
    assertThat(this.testRule.getDescription()).isEqualTo(PedanticEnforcerRule.DEPENDENCY_MANAGEMENT_LOCATION);
  }

  @Override
  @Test
  void accept() {
    PedanticEnforcerVisitor visitor = mock(PedanticEnforcerVisitor.class);
    this.testRule.accept(visitor);

    verify(visitor).visit(this.testRule);
  }

  @Test
  void noDependencyManagingPomsDeclared() {
    executeRuleAndCheckReport(false);
  }

  @Test
  void noDependencyManagementDeclared() {
    this.projectModel.getManagedDependencies().clear();

    executeRuleAndCheckReport(false);
  }

  @Test
  void isDependencyManagingPom() {
    this.testRule.setDependencyManagingPoms("a.b.c:parent");

    executeRuleAndCheckReport(false);
  }

  @Test
  void isNotDependencyManagingPom() {
    this.testRule.setDependencyManagingPoms("some.other:pom");

    executeRuleAndCheckReport(true);
  }

  void dependencyManagementAllowedInParentPom() {
    when(this.mockMavenProject.getPackaging()).thenReturn("pom");

    executeRuleAndCheckReport(true);
  }

  void dependencyManagementNotAllowedInParentPom() {
    when(this.mockMavenProject.getPackaging()).thenReturn("pom");
    this.testRule.setAllowParentPoms(false);

    executeRuleAndCheckReport(true);
  }

  @Test
  void dependencyManagementInNonParentPom() {
    when(this.mockMavenProject.getPackaging()).thenReturn("jar");

    executeRuleAndCheckReport(false);
  }

}
