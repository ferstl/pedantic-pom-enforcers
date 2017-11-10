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
@MavenVersions({"3.5.0", "3.3.9"})
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
  public void warnOnly() throws Exception {
    File basedir = this.resources.getBasedir("warn-only");
    MavenExecutionResult result = this.mavenRuntime
        .forProject(basedir)
        .execute("enforcer:enforce");

    result.assertErrorFreeLog();
  }
}
