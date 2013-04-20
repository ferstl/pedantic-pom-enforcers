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

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * JUnit tests for {@link PedanticModuleOrderEnforcer}.
 */
public class PedanticModuleOrderEnforcerTest extends AbstractPedanticEnforcerTest<PedanticModuleOrderEnforcer> {

  @Override
  PedanticModuleOrderEnforcer createRule() {
    return new PedanticModuleOrderEnforcer();
  }

  @Before
  public void before() throws Exception {
    when(this.mockMavenProject.getPackaging()).thenReturn("pom");
  }

  @Override
  @Test
  public void getDescription() {
    assertThat(this.testRule.getDescription(), equalTo(PedanticEnforcerRule.MODULE_ORDER));
  }

  @Override
  @Test
  public void accept() {
    PedanticEnforcerVisitor visitor = mock(PedanticEnforcerVisitor.class);
    this.testRule.accept(visitor);

    verify(visitor).visit(this.testRule);
  }

  @Test
  public void correctOrder() {
    when(this.projectModel.getModules()).thenReturn(Arrays.asList("m1", "m2", "m3"));

    executeRuleAndCheckReport(false);
  }

  @Test
  public void correctOrderWithIgnores() {
    when(this.projectModel.getModules()).thenReturn(Arrays.asList("m9", "m8", "m1", "m2", "m7", "m3"));
    this.testRule.setIgnoredModules("m9,m8,m7");

    executeRuleAndCheckReport(false);
  }

  @Test
  public void noPomPackaging() {
    when(this.mockMavenProject.getPackaging()).thenReturn("jar");
    when(this.projectModel.getModules()).thenReturn(null);

    executeRuleAndCheckReport(false);
  }

  @Test
  public void incorrectOrder() {
    when(this.projectModel.getModules()).thenReturn(Arrays.asList("m2", "m1"));

    executeRuleAndCheckReport(true);
  }

  @Test
  public void incorrectOrderWithIgnores() {
    when(this.projectModel.getModules()).thenReturn(Arrays.asList("m9", "m2", "m1"));
    this.testRule.setIgnoredModules("m9");

    executeRuleAndCheckReport(true);
  }

}
