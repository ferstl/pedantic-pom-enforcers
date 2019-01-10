/*
 * Copyright (c) 2012 - 2019 the original author or authors.
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

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.codehaus.plexus.util.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import com.github.ferstl.maven.pomenforcers.model.DependencyScope;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Collections2;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * JUnit tests for {@link PedanticDependencyScopeEnforcer}.
 */
@RunWith(Theories.class)
public class PedanticDependencyScopeEnforcerTest extends AbstractPedanticEnforcerTest<PedanticDependencyScopeEnforcer> {

  /**
   * Creates test data for each possible dependency scope. The data will be used as theory to test the enforcer rule
   * with different settings. This prevents writing a test method for each dependency scope.
   */
  @DataPoints
  public static RuleConfiguration[] ruleConfigurations() throws Exception {
    List<RuleConfiguration> ruleConfig = new ArrayList<>(2 * DependencyScope.values().length);

    for (DependencyScope scope : DependencyScope.values()) {
      String configMethodName = "set" + StringUtils.capitalise(scope.getScopeName()) + "Dependencies";
      MethodType configMethodType = MethodType.methodType(void.class, String.class);
      MethodHandle configMethod = MethodHandles.publicLookup().findVirtual(
          PedanticDependencyScopeEnforcer.class, configMethodName, configMethodType);

      ruleConfig.add(new RuleConfiguration(configMethod, createCorrectConfiguration(scope), false));
      ruleConfig.add(new RuleConfiguration(configMethod, createWrongConfiguration(scope), true));
    }
    return ruleConfig.toArray(new RuleConfiguration[ruleConfig.size()]);
  }

  @Override
  PedanticDependencyScopeEnforcer createRule() {
    return new PedanticDependencyScopeEnforcer();
  }

  @Before
  public void before() {
    addDependenciesForAllScopes();
  }

  @Override
  @Test
  public void getDescription() {
    assertThat(this.testRule.getDescription(), equalTo(PedanticEnforcerRule.DEPENDENCY_SCOPE));
  }

  @Override
  @Test
  public void accept() {
    PedanticEnforcerVisitor visitor = mock(PedanticEnforcerVisitor.class);
    this.testRule.accept(visitor);

    verify(visitor).visit(this.testRule);
  }

  @Test
  public void nothingConfigured() {
    executeRuleAndCheckReport(false);
  }

  @Theory
  public void allConfigurations(RuleConfiguration param) throws Throwable {
    param.configureRule(this.testRule);

    executeRuleAndCheckReport(param.resultHasErrors);
  }

  @Test
  public void correctCompileDependencies() {
    this.testRule.setCompileDependencies("a.b.c:dep-compile");

    executeRuleAndCheckReport(false);
  }

  @Test
  public void wrongCompileDependencies() {
    this.testRule.setCompileDependencies("a.b.c:dep-test,a.b.c:dep-runtime");

    executeRuleAndCheckReport(true);
  }


  private void addDependenciesForAllScopes() {
    for (DependencyScope scope : DependencyScope.values()) {
      addDependency("a.b.c", "dep-" + scope.getScopeName(), scope);
    }
  }

  private static String createWrongConfiguration(DependencyScope scope) {
    ArrayList<DependencyScope> scopes = new ArrayList<>(Arrays.asList(DependencyScope.values()));
    scopes.remove(scope);

    return Joiner.on(",").join(Collections2.transform(scopes, ScopeToDependencyString.INSTANCE));
  }

  private static String createCorrectConfiguration(DependencyScope scope) {
    return ScopeToDependencyString.INSTANCE.apply(scope);
  }

  static enum ScopeToDependencyString implements Function<DependencyScope, String> {
    INSTANCE;

    @Override
    public String apply(DependencyScope input) {
      return "a.b.c:dep-" + input.getScopeName();
    }
  }

  static class RuleConfiguration {
    private final MethodHandle configMethod;
    private final String configArgument;
    private final boolean resultHasErrors;

    public RuleConfiguration(MethodHandle configMethod, String configArgument, boolean resultHasErrors) {
      this.configMethod = configMethod;
      this.configArgument = configArgument;
      this.resultHasErrors = resultHasErrors;
    }

    public void configureRule(PedanticDependencyScopeEnforcer rule) throws Throwable {
      this.configMethod.invoke(rule, this.configArgument);
    }
  }
}
