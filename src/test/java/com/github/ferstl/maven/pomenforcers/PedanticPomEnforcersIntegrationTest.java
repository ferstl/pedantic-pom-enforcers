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

import java.io.File;
import org.junit.jupiter.api.extension.RegisterExtension;
import io.takari.maven.testing.TestResources5;
import io.takari.maven.testing.executor.MavenExecutionResult;
import io.takari.maven.testing.executor.MavenRuntime;
import io.takari.maven.testing.executor.MavenVersions;
import io.takari.maven.testing.executor.junit.MavenPluginTest;

// Enforcer plugin requires at least Maven version 3.6.3
@MavenVersions({"3.9.11", "3.6.3"})
class PedanticPomEnforcersIntegrationTest {

  @RegisterExtension
  public final TestResources5 resources = new TestResources5();

  private final MavenRuntime mavenRuntime;


  PedanticPomEnforcersIntegrationTest(MavenRuntime.MavenRuntimeBuilder builder) throws Exception {
    this.mavenRuntime = builder
        .withCliOptions("-B")
        .build();
  }

  @MavenPluginTest
  void simpleProject() throws Exception {
    File basedir = this.resources.getBasedir("simple-project");
    MavenExecutionResult result = this.mavenRuntime
        .forProject(basedir)
        .execute("enforcer:enforce");

    result.assertErrorFreeLog();
  }

  @MavenPluginTest
  void exampleProject() throws Exception {
    File basedir = this.resources.getBasedir("example-project");
    MavenExecutionResult result = this.mavenRuntime
        .forProject(basedir)
        .execute("package", "enforcer:enforce");

    result.assertErrorFreeLog();
  }

  @MavenPluginTest
  void issue2() throws Exception {
    File basedir = this.resources.getBasedir("issue-2");
    MavenExecutionResult result = this.mavenRuntime
        .forProject(basedir)
        .execute("enforcer:enforce");

    result.assertErrorFreeLog();
  }

  @MavenPluginTest
  void issue23() throws Exception {
    File basedir = this.resources.getBasedir("issue-23");
    MavenExecutionResult result = this.mavenRuntime
        .forProject(basedir)
        .execute("enforcer:enforce");

    result.assertLogText("Dependency exclusions have to be declared in <dependencyManagement>");
    result.assertLogText("BUILD FAILURE");
  }

  @MavenPluginTest
  void warnOnly() throws Exception {
    File basedir = this.resources.getBasedir("warn-only");
    MavenExecutionResult result = this.mavenRuntime
        .forProject(basedir)
        .execute("enforcer:enforce");

    result.assertErrorFreeLog();
    result.assertLogText("POM_SECTION_ORDER: ");
    result.assertLogText("DEPENDENCY_ORDER: ");
  }
}
