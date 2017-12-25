package com.github.ferstl.maven.pomenforcers;

import java.nio.file.Paths;
import org.apache.maven.enforcer.rule.api.EnforcerRuleHelper;
import org.junit.Test;
import org.w3c.dom.Document;
import com.github.ferstl.maven.pomenforcers.model.ProjectModel;
import com.github.ferstl.maven.pomenforcers.util.XmlUtils;

import static org.mockito.Mockito.mock;

public class PedanticDependencyElementEnforcerTest {

  @Test
  public void test() {
    PedanticDependencyElementEnforcer enforcer = new PedanticDependencyElementEnforcer();
    Document document = XmlUtils.parseXml(Paths.get("src/test/projects/example-project/module1/pom.xml").toFile());

    enforcer.initialize(mock(EnforcerRuleHelper.class), document, new ProjectModel());
    ErrorReport report = new ErrorReport(PedanticEnforcerRule.DEPENDENCY_ELEMENT);
    enforcer.doEnforce(report);

    System.out.println(report);
  }
}
