/*
 * Copyright (c) 2012 by The Author(s)
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

import java.io.File;

import org.apache.maven.enforcer.rule.api.EnforcerRuleException;
import org.apache.maven.enforcer.rule.api.EnforcerRuleHelper;
import org.apache.maven.monitor.logging.DefaultLog;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.logging.console.ConsoleLogger;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PedanticPomSectionOrderEnforcerTest {

  private static final File TEST_DIRECTORY =
      new File("target/test-classes/PedanticPomSectionOrderEnforcer");

  private EnforcerRuleHelper mockHelper;
  private MavenProject mockProject;

  @Before
  public void setUp() throws Exception {
    this.mockHelper = mock(EnforcerRuleHelper.class);
    this.mockProject = mock(MavenProject.class);
    ConsoleLogger plexusLogger = new ConsoleLogger(Logger.LEVEL_DEBUG, "testLogger");
    when(this.mockHelper.getLog()).thenReturn(new DefaultLog(plexusLogger));
    when(this.mockHelper.evaluate("${project}")).thenReturn(this.mockProject);

  }

  @Test
  public void testDefaultSettings() throws Exception {
    File testPomFile = new File(TEST_DIRECTORY, "pom-section-order-default-correct.xml");
    when(this.mockProject.getFile()).thenReturn(testPomFile);

    PedanticPomSectionOrderEnforcer rule = new PedanticPomSectionOrderEnforcer();
    rule.execute(this.mockHelper);
  }

  @Test(expected = EnforcerRuleException.class)
  public void testDefaultSettingsWrongOrder() throws Exception {
    File testPomFile = new File(TEST_DIRECTORY, "pom-section-order-default-wrong.xml");
    when(this.mockProject.getFile()).thenReturn(testPomFile);

    PedanticPomSectionOrderEnforcer rule = new PedanticPomSectionOrderEnforcer();
    rule.execute(this.mockHelper);
  }

  @Test
  public void testCustomSettings() throws Exception {
    File testPomFile = new File(TEST_DIRECTORY, "pom-section-order-custom-correct.xml");
    when(this.mockProject.getFile()).thenReturn(testPomFile);

    PedanticPomSectionOrderEnforcer rule = new PedanticPomSectionOrderEnforcer();
    rule.setSectionPriorities("modelVersion,groupId,artifactId,version,packaging,name,description,url,parent");
    rule.execute(this.mockHelper);
  }

}
