package ch.sferstl.maven.enforcerrules;

import java.io.File;

import org.apache.maven.enforcer.rule.api.EnforcerRuleHelper;
import org.apache.maven.monitor.logging.DefaultLog;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.logging.console.ConsoleLogger;
import org.junit.Before;
import org.junit.Test;

import ch.sferstl.maven.pom.enforcer.TestRule;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class TestRuleTest {

  private EnforcerRuleHelper mockHelper;

  /**
   * @throws java.lang.Exception
   */
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
    TestRule rule = new TestRule();
    rule.execute(this.mockHelper);
  }

}
