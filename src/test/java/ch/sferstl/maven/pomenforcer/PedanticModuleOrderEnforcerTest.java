/*
 * Copyright (c) 2012 by The Authors of the Pedantic POM Enforcers
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
package ch.sferstl.maven.pomenforcer;

import java.io.File;

import org.apache.maven.enforcer.rule.api.EnforcerRuleHelper;
import org.apache.maven.monitor.logging.DefaultLog;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.logging.console.ConsoleLogger;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class PedanticModuleOrderEnforcerTest {

  private EnforcerRuleHelper mockHelper;

  @Before
  public void setUp() throws Exception {
    this.mockHelper = mock(EnforcerRuleHelper.class);
    MavenProject mockProject = mock(MavenProject.class);
    when(mockProject.getFile()).thenReturn(new File("target/test-classes/test-pom.xml"));
    ConsoleLogger plexusLogger = new ConsoleLogger(Logger.LEVEL_DEBUG, "testLogger");
    when(this.mockHelper.getLog()).thenReturn(new DefaultLog(plexusLogger));
    when(this.mockHelper.evaluate("${project}")).thenReturn(mockProject);
  }

  @Test
  public void test() throws Exception {
    PedanticModuleOrderEnforcer rule = new PedanticModuleOrderEnforcer();
    rule.execute(this.mockHelper);
  }

}
