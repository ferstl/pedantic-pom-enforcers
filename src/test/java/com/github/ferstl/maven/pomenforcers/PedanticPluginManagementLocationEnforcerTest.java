/*
 * Copyright (c) 2012 - 2015 by Stefan Ferstl <st.ferstl@gmail.com>
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

import org.junit.Before;
import org.junit.Test;

import com.github.ferstl.maven.pomenforcers.model.PluginModel;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * JUnit tests for {@link PedanticPluginManagementLocationEnforcer}.
 */
public class PedanticPluginManagementLocationEnforcerTest extends AbstractPedanticEnforcerTest<PedanticPluginManagementLocationEnforcer> {

  @Override
  PedanticPluginManagementLocationEnforcer createRule() {
    return new PedanticPluginManagementLocationEnforcer();
  }

  @Before
  public void before() {
    when(this.mockMavenProject.getGroupId()).thenReturn("a.b.c");
    when(this.mockMavenProject.getArtifactId()).thenReturn("parent");
    this.projectModel.getManagedPlugins().add(new PluginModel("a.b.c", "a", "1.0"));
  }

  @Override
  @Test
  public void getDescription() {
    assertThat(this.testRule.getDescription(), equalTo(PedanticEnforcerRule.PLUGIN_MANAGEMENT_LOCATION));
  }

  @Override
  @Test
  public void accept() {
    PedanticEnforcerVisitor visitor = mock(PedanticEnforcerVisitor.class);
    this.testRule.accept(visitor);

    verify(visitor).visit(this.testRule);
  }

  @Test
  public void noPluginManagingPomsDeclared() {
    executeRuleAndCheckReport(false);
  }

  @Test
  public void isPluginManagingPom() {
    this.testRule.setPluginManagingPoms("a.b.c:parent");

    executeRuleAndCheckReport(false);
  }

  @Test
  public void isNotPluginManagingPom() {
    this.testRule.setPluginManagingPoms("some.other:pom");

    executeRuleAndCheckReport(true);
  }
}
