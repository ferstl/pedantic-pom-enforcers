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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.junit.jupiter.api.Test;

import com.github.ferstl.maven.pomenforcers.model.ArtifactModel;
import com.github.ferstl.maven.pomenforcers.model.DependencyModel;

/**
 * JUnit tests for {@link PedanticDependencyConfigurationEnforcer}.
 */
public class PedanticDependencyConfigurationEnforcerTest extends AbstractPedanticEnforcerTest<PedanticDependencyConfigurationEnforcer> {

  @Override
  PedanticDependencyConfigurationEnforcer createRule() {
    return new PedanticDependencyConfigurationEnforcer(mockMavenProject, mockHelper);
  }

  @Override
  @Test
  public void getDescription() {
    assertThat(this.testRule.getDescription()).isEqualTo(PedanticEnforcerRule.DEPENDENCY_CONFIGURATION);
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
    this.projectModel.getDependencies().add(createDependency(false, false));

    executeRuleAndCheckReport(false);
  }

  @Test
  public void allowedProjectVersion1() {
    DependencyModel dependency = createDependency(false, false);
    when(dependency.getVersion()).thenReturn("${project.version}");
    this.projectModel.getDependencies().add(dependency);

    executeRuleAndCheckReport(false);
  }

  @Test
  public void allowedProjectVersion2() {
    DependencyModel dependency = createDependency(false, false);
    when(dependency.getVersion()).thenReturn("${version}");
    this.projectModel.getDependencies().add(dependency);

    executeRuleAndCheckReport(false);
  }

  @Test
  public void forbiddenProjectVersion() {
    this.testRule.setAllowUnmanagedProjectVersions(false);
    DependencyModel dependency = createDependency(false, false);
    when(dependency.getVersion()).thenReturn("${project.version}");
    this.projectModel.getDependencies().add(dependency);

    executeRuleAndCheckReport(true);
  }

  @Test
  public void forbiddenManagedExclusion() {
    this.projectModel.getDependencies().add(createDependency(false, true));

    executeRuleAndCheckReport(true);
  }

  @Test
  public void allowedManagedExclusion() {
    this.testRule.setManageExclusions(false);
    this.projectModel.getDependencies().add(createDependency(false, true));

    executeRuleAndCheckReport(false);
  }

  @Test
  public void forbiddenVersion() {
    this.projectModel.getDependencies().add(createDependency(true, false));

    executeRuleAndCheckReport(true);
  }

  @Test
  public void allowedVersion() {
    this.testRule.setManageVersions(false);
    this.projectModel.getDependencies().add(createDependency(true, false));

    executeRuleAndCheckReport(false);
  }

  @Test
  public void allowedVersionWithProps() {
    this.testRule.setAllowedUnmanagedProjectVersionProperties("some.version");
    this.projectModel.getDependencies().add(createDependency(false, false));

    executeRuleAndCheckReport(false);
  }

  @Test
  public void allowedVersionWithDisabledProps1() {
    this.testRule.setManageVersions(false);
    this.testRule.setAllowedUnmanagedProjectVersionProperties("some.version");
    this.projectModel.getDependencies().add(createDependency(true, false));

    executeRuleAndCheckReport(false);
  }

  @Test
  public void allowedVersionWithDisabledProps2() {
    this.testRule.setAllowUnmanagedProjectVersions(false);
    this.testRule.setAllowedUnmanagedProjectVersionProperties("some.version");
    this.projectModel.getDependencies().add(createDependency(false, false));

    executeRuleAndCheckReport(false);
  }

  @Test
  public void forbiddenVersionWithCustomProps1() {
    this.testRule.setAllowedUnmanagedProjectVersionProperties("some.version");
    this.projectModel.getDependencies().add(createDependency(true, false));

    executeRuleAndCheckReport(true);
  }

  @Test
  public void forbiddenVersionWithCustomProps2() {
    this.testRule.setAllowedUnmanagedProjectVersionProperties("some.version");
    DependencyModel dependency = createDependency(true, false);
    when(dependency.getVersion()).thenReturn("${project.version}");
    this.projectModel.getDependencies().add(dependency);

    executeRuleAndCheckReport(true);
  }

  @Test
  public void allowedVersionWithActiveCustomProps1() {
    this.testRule.setAllowedUnmanagedProjectVersionProperties("some.version");
    DependencyModel dependency = createDependency(true, false);
    when(dependency.getVersion()).thenReturn("${some.version}");
    this.projectModel.getDependencies().add(dependency);

    executeRuleAndCheckReport(false);
  }
  @Test
  public void allowedVersionWithActiveCustomProps2() {
    this.testRule.setAllowedUnmanagedProjectVersionProperties("some.version,some.other.version");
    DependencyModel dependency = createDependency(true, false);
    when(dependency.getVersion()).thenReturn("${some.other.version}");
    this.projectModel.getDependencies().add(dependency);

    executeRuleAndCheckReport(false);
  }

  private DependencyModel createDependency(boolean withVersion, boolean withExclusion) {
    DependencyModel dependency = mock(DependencyModel.class);

    if (withVersion) {
      when(dependency.getVersion()).thenReturn("1.0");
    }

    if (withExclusion) {
      when(dependency.getExclusions()).thenReturn(Collections.singletonList(new ArtifactModel("a.b.c", "a")));
    }

    return dependency;
  }
}
