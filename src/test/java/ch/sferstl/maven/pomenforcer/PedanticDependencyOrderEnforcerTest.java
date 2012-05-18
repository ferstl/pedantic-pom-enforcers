package ch.sferstl.maven.pomenforcer;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.List;

import org.apache.maven.enforcer.rule.api.EnforcerRuleException;
import org.apache.maven.enforcer.rule.api.EnforcerRuleHelper;
import org.apache.maven.model.Dependency;
import org.apache.maven.monitor.logging.DefaultLog;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.logging.console.ConsoleLogger;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;

import ch.sferstl.maven.pomenforcer.reader.DeclaredDependenciesReader;


public class PedanticDependencyOrderEnforcerTest {

  private static final File ALL_DEPENDENCIES_FILE =
      new File("target/test-classes/all-dependencies.xml");
  private static final File TEST_DIRECTORY =
      new File("target/test-classes/PedanticDependencyOrderEnforcer");
  
  private EnforcerRuleHelper mockHelper;
  private MavenProject mockProject;

  @Before
  public void setUp() throws Exception {
    Document allDepsPom = XmlParser.parseXml(ALL_DEPENDENCIES_FILE);
    List<Dependency> allDeps = new DeclaredDependenciesReader(allDepsPom).read();
    
    this.mockHelper = mock(EnforcerRuleHelper.class);
    mockProject = mock(MavenProject.class);
    when(mockProject.getDependencies()).thenReturn(allDeps);
    ConsoleLogger plexusLogger = new ConsoleLogger(Logger.LEVEL_DEBUG, "testLogger");
    when(this.mockHelper.getLog()).thenReturn(new DefaultLog(plexusLogger));
    when(this.mockHelper.evaluate("${project}")).thenReturn(mockProject);

  }

  @Test
  public void testDefaultSettings() throws Exception {
    File testPomFile = new File(TEST_DIRECTORY, "dependency-order-default-correct.xml");
    when(mockProject.getFile()).thenReturn(testPomFile);
    
    PedanticDependencyOrderEnforcer rule = new PedanticDependencyOrderEnforcer();
    rule.execute(this.mockHelper);
  }
  
  @Test(expected = EnforcerRuleException.class)
  public void testDefaultSettingsWrongOrder() throws Exception {
    File testPomFile = new File(TEST_DIRECTORY, "dependency-order-default-wrong.xml");
    when(mockProject.getFile()).thenReturn(testPomFile);
    
    PedanticDependencyOrderEnforcer rule = new PedanticDependencyOrderEnforcer();
    rule.execute(this.mockHelper);
  }

  @Test
  public void testCustomSettings() throws Exception {
    File testPomFile = new File(TEST_DIRECTORY, "dependency-order-custom-correct.xml");
    when(mockProject.getFile()).thenReturn(testPomFile);
    
    PedanticDependencyOrderEnforcer rule = new PedanticDependencyOrderEnforcer();
    rule.setOrderBy("scope,artifactId,groupId");
    rule.setScopePriorities("test,provided");
    rule.setArtifactIdPriorities("junit,lambdaj");
    rule.setGroupIdPriorities("org.apache.commons");
    rule.execute(this.mockHelper);
  }
}
