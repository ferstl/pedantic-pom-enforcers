/*
 * Copyright (c) 2012 - 2020 the original author or authors.
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
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import io.takari.maven.testing.TestResources;
import io.takari.maven.testing.executor.MavenExecutionResult;
import io.takari.maven.testing.executor.MavenRuntime;
import io.takari.maven.testing.executor.MavenVersions;
import io.takari.maven.testing.executor.junit.MavenJUnitTestRunner;

@RunWith(MavenJUnitTestRunner.class)
@MavenVersions({"3.8.2", "3.3.9"})
public class PedanticPomEnforcersIntegrationTest {

  @Rule
  public final TestResources resources = new TestResources();

  private final MavenRuntime mavenRuntime;


  public PedanticPomEnforcersIntegrationTest(MavenRuntime.MavenRuntimeBuilder builder) throws Exception {
    this.mavenRuntime = builder
        .withCliOptions("-B")
        .build();
  }

  @Test
  public void simpleProject() throws Exception {
    File basedir = this.resources.getBasedir("simple-project");
    MavenExecutionResult result = this.mavenRuntime
        .forProject(basedir)
        .execute("enforcer:enforce");

    result.assertErrorFreeLog();
  }

  @Test
  public void exampleProject() throws Exception {
    File basedir = this.resources.getBasedir("example-project");
    MavenExecutionResult result = this.mavenRuntime
        .forProject(basedir)
        .execute("package", "enforcer:enforce");

    result.assertErrorFreeLog();
  }

  @Test
  public void issue2() throws Exception {
    File basedir = this.resources.getBasedir("issue-2");
    MavenExecutionResult result = this.mavenRuntime
        .forProject(basedir)
        .execute("enforcer:enforce");

    result.assertErrorFreeLog();
  }

  @Test
  public void issue23() throws Exception {
    File basedir = this.resources.getBasedir("issue-23");
    MavenExecutionResult result = this.mavenRuntime
        .forProject(basedir)
        .execute("enforcer:enforce");

    result.assertLogText("Dependency exclusions have to be declared in <dependencyManagement>");
    result.assertLogText("BUILD FAILURE");
  }

  @Test
  public void warnOnly() throws Exception {
    File basedir = this.resources.getBasedir("warn-only");
    MavenExecutionResult result = this.mavenRuntime
        .forProject(basedir)
        .execute("enforcer:enforce");

    result.assertErrorFreeLog();
    result.assertLogText("POM_SECTION_ORDER: ");
    result.assertLogText("DEPENDENCY_ORDER: ");
  }
}
