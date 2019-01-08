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
@MavenVersions({"3.6.0"})
public class PedanticPomEnforcersIntegrationTest {

  @Rule
  public final TestResources resources = new TestResources();

  private final MavenRuntime mavenRuntime;


  public PedanticPomEnforcersIntegrationTest(MavenRuntime.MavenRuntimeBuilder builder) throws Exception {
    this.mavenRuntime = builder
        .withCliOptions("-B")
        .build();
  }

  public void simpleProject() throws Exception {
    File basedir = this.resources.getBasedir("simple-project");
    MavenExecutionResult result = this.mavenRuntime
        .forProject(basedir)
        .execute("enforcer:enforce");

    result.assertErrorFreeLog();
  }

  public void exampleProject() throws Exception {
    File basedir = this.resources.getBasedir("example-project");
    MavenExecutionResult result = this.mavenRuntime
        .forProject(basedir)
        .execute("package", "enforcer:enforce");

    result.assertErrorFreeLog();
  }

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
}
