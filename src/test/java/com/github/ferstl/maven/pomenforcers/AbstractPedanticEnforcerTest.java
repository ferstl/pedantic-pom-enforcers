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

import java.util.LinkedList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.DependencyManagement;
import org.apache.maven.model.PluginManagement;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import com.github.ferstl.maven.pomenforcers.model.DependencyModel;
import com.github.ferstl.maven.pomenforcers.model.DependencyScope;
import com.github.ferstl.maven.pomenforcers.model.ProjectModel;
import static com.github.ferstl.maven.pomenforcers.ErrorReportAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


abstract class AbstractPedanticEnforcerTest<T extends AbstractPedanticEnforcer> {

  private static final String DEFAULT_VERSION = "1.0";

  ProjectModel projectModel;
  MavenProject mockMavenProject;
  T testRule;
  ErrorReport report;

  ExpressionEvaluator mockHelper;

  @BeforeEach
  void setup() throws Exception {
    this.mockHelper = mock(ExpressionEvaluator.class);
    this.projectModel = mock(ProjectModel.class);
    this.mockMavenProject = mock(MavenProject.class);

    when(this.projectModel.getDependencies()).thenReturn(new LinkedList<>());
    when(this.projectModel.getManagedDependencies()).thenReturn(new LinkedList<>());
    when(this.projectModel.getPlugins()).thenReturn(new LinkedList<>());
    when(this.projectModel.getManagedPlugins()).thenReturn(new LinkedList<>());
    when(this.mockMavenProject.getDependencies()).thenReturn(new LinkedList<>());
    DependencyManagement depMgmtMock = mock(DependencyManagement.class);
    PluginManagement pluginMgmtMock = mock(PluginManagement.class);
    when(depMgmtMock.getDependencies()).thenReturn(new LinkedList<>());
    when(pluginMgmtMock.getPlugins()).thenReturn(new LinkedList<>());
    when(this.mockMavenProject.getDependencyManagement()).thenReturn(depMgmtMock);
    when(this.mockMavenProject.getPluginManagement()).thenReturn(pluginMgmtMock);

    when(this.mockHelper.evaluate("${project}")).thenReturn(this.mockMavenProject);

    this.testRule = createRule();
    this.testRule.initialize(createEmptyPom(), this.projectModel);
    this.report = new ErrorReport(this.testRule.getDescription());
  }

  abstract T createRule();

  @Test
  abstract void getDescription();

  @Test
  abstract void accept();

  protected void executeRuleAndCheckReport(boolean hasErrors) {
    this.testRule.doEnforce(this.report);

    if (hasErrors) {
      assertThat(this.report).hasErrors();
    } else {
      assertThat(this.report).hasNoErrors();
    }
  }

  protected void addDependency(String groupId, String artifactId, DependencyScope scope) {
    String version = DEFAULT_VERSION;

    Dependency mavenDependency = createMavenDependency(groupId, artifactId, scope, version);
    DependencyModel dependency = createDependencyModel(groupId, artifactId, scope, version);

    this.mockMavenProject.getDependencies().add(mavenDependency);
    this.projectModel.getDependencies().add(dependency);
  }

  protected void addManagedDependency(String groupId, String artifactId, DependencyScope scope) {
    String version = DEFAULT_VERSION;

    Dependency mavenDependency = createMavenDependency(groupId, artifactId, scope, version);
    DependencyModel dependency = createDependencyModel(groupId, artifactId, version);

    this.mockMavenProject.getDependencyManagement().getDependencies().add(mavenDependency);
    this.projectModel.getManagedDependencies().add(dependency);
  }

  private static DependencyModel createDependencyModel(String groupId, String artifactId, String version) {
    return createDependencyModel(groupId, artifactId, null, version);
  }

  private static DependencyModel createDependencyModel(String groupId, String artifactId, DependencyScope scope, String version) {
    return new DependencyModel(groupId, artifactId, version, scope != null ? scope.getScopeName() : null, null, null);
  }

  private static Dependency createMavenDependency(String groupId, String artifactId, DependencyScope scope, String version) {
    Dependency mavenDependency = new Dependency();
    mavenDependency.setGroupId(groupId);
    mavenDependency.setArtifactId(artifactId);
    mavenDependency.setVersion(version);
    mavenDependency.setScope(scope.getScopeName());
    return mavenDependency;
  }

  private static Document createEmptyPom() {
    DocumentBuilder docBuilder = createDocumentBuilder();
    Document document = docBuilder.newDocument();
    Element rootElement = document.createElement("project");

    document.appendChild(rootElement);

    return document;
  }

  private static DocumentBuilder createDocumentBuilder() {
    try {
      return DocumentBuilderFactory.newInstance().newDocumentBuilder();
    } catch (ParserConfigurationException e) {
      throw new IllegalStateException("Cannot create document builder", e);
    }
  }
}
