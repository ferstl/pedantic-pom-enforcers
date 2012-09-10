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
import java.util.List;

import org.apache.maven.enforcer.rule.api.EnforcerRuleException;
import org.apache.maven.enforcer.rule.api.EnforcerRuleHelper;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Exclusion;
import org.apache.maven.monitor.logging.DefaultLog;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.logging.console.ConsoleLogger;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;

import com.github.ferstl.maven.pomenforcers.model.ArtifactModel;
import com.github.ferstl.maven.pomenforcers.model.DependencyModel;
import com.github.ferstl.maven.pomenforcers.reader.PomSerializer;
import com.github.ferstl.maven.pomenforcers.util.XmlUtils;
import com.google.common.base.Function;
import com.google.common.collect.Lists;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PedanticDependencyOrderEnforcerTest {

  private static final File ALL_DEPENDENCIES_FILE =
      new File("target/test-classes/all-dependencies.xml");
  private static final File TEST_DIRECTORY =
      new File("target/test-classes/PedanticDependencyOrderEnforcer");

  private EnforcerRuleHelper mockHelper;
  private MavenProject mockProject;

  @Before
  public void setUp() throws Exception {
    Document allDepsPom = XmlUtils.parseXml(ALL_DEPENDENCIES_FILE);

    // Read all dependencies and convert them to artifacts (classifier = "", ArtifactHandler = null)
    List<DependencyModel> allDeps = new PomSerializer(allDepsPom).read().getDependencies();
    List<Dependency> dependencies = Lists.transform(allDeps, new DependencyModelTransformer());

    this.mockHelper = mock(EnforcerRuleHelper.class);
    this.mockProject = mock(MavenProject.class);
    when(this.mockProject.getDependencies()).thenReturn(dependencies);
    ConsoleLogger plexusLogger = new ConsoleLogger(Logger.LEVEL_DEBUG, "testLogger");
    when(this.mockHelper.getLog()).thenReturn(new DefaultLog(plexusLogger));
    when(this.mockHelper.evaluate("${project}")).thenReturn(this.mockProject);

  }

  @Test
  public void testDefaultSettings() throws Exception {
    File testPomFile = new File(TEST_DIRECTORY, "dependency-order-default-correct.xml");
    when(this.mockProject.getFile()).thenReturn(testPomFile);

    PedanticDependencyOrderEnforcer rule = new PedanticDependencyOrderEnforcer();
    rule.execute(this.mockHelper);
  }

  @Test(expected = EnforcerRuleException.class)
  public void testDefaultSettingsWrongOrder() throws Exception {
    File testPomFile = new File(TEST_DIRECTORY, "dependency-order-default-wrong.xml");
    when(this.mockProject.getFile()).thenReturn(testPomFile);

    PedanticDependencyOrderEnforcer rule = new PedanticDependencyOrderEnforcer();
    rule.execute(this.mockHelper);
  }

  @Test
  public void testCustomSettings() throws Exception {
    File testPomFile = new File(TEST_DIRECTORY, "dependency-order-custom-correct.xml");
    when(this.mockProject.getFile()).thenReturn(testPomFile);

    AbstractPedanticDependencyOrderEnforcer rule = new PedanticDependencyOrderEnforcer();
    rule.setOrderBy("scope,artifactId,groupId");
    rule.setScopePriorities("test,provided");
    rule.setArtifactIdPriorities("junit,lambdaj");
    rule.setGroupIdPriorities("org.apache.commons");
    rule.execute(this.mockHelper);
  }

  private static class DependencyModelTransformer implements Function<DependencyModel, Dependency> {
    @Override
    public Dependency apply(DependencyModel input) {
      Dependency dependency = new Dependency();
      dependency.setGroupId(input.getGroupId());
      dependency.setArtifactId(input.getArtifactId());
      dependency.setVersion(input.getVersion());
      dependency.setClassifier(input.getClassifier());
      dependency.setScope(input.getScope());
      dependency.setType("jar");

      for (ArtifactModel exclusionModel : input.getExclusions()) {
        Exclusion exclusion = new Exclusion();
        exclusion.setGroupId(exclusionModel.getGroupId());
        exclusion.setArtifactId(exclusionModel.getArtifactId());
        dependency.addExclusion(exclusion);
      }
      return dependency;
    }
  }
}
