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

import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluationException;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import com.github.ferstl.maven.pomenforcers.model.ProjectModel;
import com.github.ferstl.maven.pomenforcers.util.XmlUtils;
import static com.github.ferstl.maven.pomenforcers.ErrorReportAssert.assertThat;
import static org.mockito.Mockito.mock;

class PedanticPluginElementEnforcerTest {

  private ErrorReport errorReport;
  private MavenProject mockMavenProject;
  private ExpressionEvaluator mockHelper;

  @BeforeEach
  void before() throws ExpressionEvaluationException {
    this.mockHelper = mock(ExpressionEvaluator.class);
    this.mockMavenProject = mock(MavenProject.class);
    this.errorReport = new ErrorReport(PedanticEnforcerRule.PLUGIN_ELEMENT);
  }

  @Test
  void defaultOrdering() {
    // arrange
    Path pomFile = Paths.get("src/test/projects/plugins/pom.xml");
    PedanticPluginElementEnforcer enforcer = createEnforcer(pomFile);

    // act
    enforcer.doEnforce(this.errorReport);

    // assert
    assertThat(this.errorReport).hasNoErrors();
  }

  @Test
  void customOrderingForPlugins() {
    // arrange
    Path pomFile = Paths.get("src/test/projects/plugins/pom.xml");
    PedanticPluginElementEnforcer enforcer = createEnforcer(pomFile);

    enforcer.setElementPriorities("artifactId,groupId");

    // act
    enforcer.doEnforce(this.errorReport);

    // assert
    assertThat(this.errorReport).hasErrors();
  }

  @Test
  void customOrderingForPluginManagement() {
    // arrange
    Path pomFile = Paths.get("src/test/projects/plugins/pom.xml");
    PedanticPluginElementEnforcer enforcer = createEnforcer(pomFile);

    enforcer.setElementPriorities("version");

    // act
    enforcer.doEnforce(this.errorReport);

    // assert
    assertThat(this.errorReport).hasErrors();
  }

  private PedanticPluginElementEnforcer createEnforcer(Path pomFile) {
    Document document = XmlUtils.parseXml(pomFile.toFile());
    PedanticPluginElementEnforcer enforcer = new PedanticPluginElementEnforcer(this.mockMavenProject, this.mockHelper);

    enforcer.initialize(document, new ProjectModel());
    return enforcer;
  }


}
