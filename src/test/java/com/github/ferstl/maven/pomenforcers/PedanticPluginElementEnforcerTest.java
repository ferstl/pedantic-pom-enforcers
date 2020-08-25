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

public class PedanticPluginElementEnforcerTest {

  private ErrorReport errorReport;

  @Before
  public void before() {
    this.errorReport = new ErrorReport(PedanticEnforcerRule.PLUGIN_ELEMENT);
  }

  @Test
  public void defaultOrdering() {
    // arrange
    Path pomFile = Paths.get("src/test/projects/plugins/pom.xml");
    PedanticPluginElementEnforcer enforcer = createEnforcer(pomFile);

    // act
    enforcer.doEnforce(this.errorReport);

    // assert
    assertThat(this.errorReport, hasNoErrors());
  }

  @Test
  public void customOrderingForPlugins() {
    // arrange
    Path pomFile = Paths.get("src/test/projects/plugins/pom.xml");
    PedanticPluginElementEnforcer enforcer = createEnforcer(pomFile);

    enforcer.setElementPriorities("artifactId,groupId");

    // act
    enforcer.doEnforce(this.errorReport);

    // assert
    assertThat(this.errorReport, hasErrors());
  }

  @Test
  public void customOrderingForPluginManagement() {
    // arrange
    Path pomFile = Paths.get("src/test/projects/plugins/pom.xml");
    PedanticPluginElementEnforcer enforcer = createEnforcer(pomFile);

    enforcer.setElementPriorities("version");

    // act
    enforcer.doEnforce(this.errorReport);

    // assert
    assertThat(this.errorReport, hasErrors());
  }

  private PedanticPluginElementEnforcer createEnforcer(Path pomFile) {
    Document document = XmlUtils.parseXml(pomFile.toFile());
    PedanticPluginElementEnforcer enforcer = new PedanticPluginElementEnforcer();

    enforcer.initialize(mock(EnforcerRuleHelper.class), document, new ProjectModel());
    return enforcer;
  }


}
