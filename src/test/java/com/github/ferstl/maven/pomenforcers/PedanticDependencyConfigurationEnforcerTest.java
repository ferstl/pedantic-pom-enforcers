package com.github.ferstl.maven.pomenforcers;

import java.util.Collections;

import org.junit.Test;

import com.github.ferstl.maven.pomenforcers.model.ArtifactModel;
import com.github.ferstl.maven.pomenforcers.model.DependencyModel;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * JUnit tests for {@link PedanticDependencyConfigurationEnforcer}.
 */
public class PedanticDependencyConfigurationEnforcerTest extends AbstractPedanticEnforcerTest<PedanticDependencyConfigurationEnforcer> {

  @Override
  PedanticDependencyConfigurationEnforcer createRule() {
    return new PedanticDependencyConfigurationEnforcer();
  }

  @Override
  @Test
  public void getDescription() {
    assertThat(this.testRule.getDescription(), equalTo(PedanticEnforcerRule.DEPENDENCY_CONFIGURATION));
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
