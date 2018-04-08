package com.github.ferstl.maven.pomenforcers;

import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.maven.enforcer.rule.api.EnforcerRuleHelper;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import com.github.ferstl.maven.pomenforcers.model.ProjectModel;
import com.github.ferstl.maven.pomenforcers.util.XmlUtils;

import static com.github.ferstl.maven.pomenforcers.ErrorReportMatcher.hasErrors;
import static com.github.ferstl.maven.pomenforcers.ErrorReportMatcher.hasNoErrors;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

public class PedanticDependencyElementEnforcerTest {

  private ErrorReport errorReport;

  @Before
  public void before() {
    this.errorReport = new ErrorReport(PedanticEnforcerRule.DEPENDENCY_ELEMENT);
  }

  @Test
  public void defaultOrdering() {
    // arrange
    Path pomFile = Paths.get("src/test/projects/example-project/module1/pom.xml");
    PedanticDependencyElementEnforcer enforcer = createEnforcer(pomFile);

    // act
    enforcer.doEnforce(this.errorReport);

    // assert
    assertThat(this.errorReport, hasNoErrors());
  }

  @Test
  public void customOrdering() {
    // arrange
    Path pomFile = Paths.get("src/test/projects/example-project/module1/pom.xml");
    PedanticDependencyElementEnforcer enforcer = createEnforcer(pomFile);

    enforcer.setElementPriorities("artifactId,groupId");

    // act
    enforcer.doEnforce(this.errorReport);

    // assert
    assertThat(this.errorReport, hasErrors());
  }

  private PedanticDependencyElementEnforcer createEnforcer(Path pomFile) {
    Document document = XmlUtils.parseXml(pomFile.toFile());
    PedanticDependencyElementEnforcer enforcer = new PedanticDependencyElementEnforcer();

    enforcer.initialize(mock(EnforcerRuleHelper.class), document, new ProjectModel());
    return enforcer;
  }


}
