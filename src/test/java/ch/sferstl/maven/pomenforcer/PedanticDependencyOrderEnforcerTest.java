package ch.sferstl.maven.pomenforcer;

import java.io.File;
import java.util.List;

import org.apache.maven.enforcer.rule.api.EnforcerRuleHelper;
import org.apache.maven.model.Dependency;
import org.apache.maven.monitor.logging.DefaultLog;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.logging.console.ConsoleLogger;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.google.common.collect.Lists;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class PedanticDependencyOrderEnforcerTest {

  private EnforcerRuleHelper mockHelper;

  @Before
  public void setUp() throws Exception {
    List<Dependency> projectDependencies = Lists.newArrayList(
        this.createDependency("commons-lang", "commons-lang", "compile"),
        this.createDependency("commons-foo", "commons-lang", "compile"),
        this.createDependency("commons-codec", "commons-codec", "compile"),
        this.createDependency("com.googlecode.lambdaj", "lambdaj", "compile"),
        this.createDependency("junit", "junit", "test"),
        this.createDependency("org.hamcrest", "hamcrest-library", "test")
        );

    this.mockHelper = mock(EnforcerRuleHelper.class);
    MavenProject mockProject = mock(MavenProject.class);
    when(mockProject.getFile()).thenReturn(new File("target/test-classes/test-pom.xml"));
    when(mockProject.getDependencies()).thenReturn(projectDependencies);
    ConsoleLogger plexusLogger = new ConsoleLogger(Logger.LEVEL_DEBUG, "testLogger");
    when(this.mockHelper.getLog()).thenReturn(new DefaultLog(plexusLogger));
    when(this.mockHelper.evaluate("${project}")).thenReturn(mockProject);

  }

  @Test
  public void testWithPriorizedGroupIds() throws Exception {
    PedanticDependencyOrderEnforcer rule = new PedanticDependencyOrderEnforcer();
    rule.setGroupIdPriorities("commons-,org.hamcrest");
    rule.execute(this.mockHelper);
  }

  @Test
  @Ignore
  public void testNoPriorizedGroupIds() throws Exception {
    PedanticDependencyOrderEnforcer rule = new PedanticDependencyOrderEnforcer();
    rule.execute(this.mockHelper);
  }

  private Dependency createDependency(String groupId, String artifactId, String scope) {
    Dependency dependency = new Dependency();
    dependency.setGroupId(groupId);
    dependency.setArtifactId(artifactId);
    dependency.setScope(scope);
    return dependency;
  }

}
