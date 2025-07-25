/*
 * Copyright (c) 2012 - 2023 the original author or authors.
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.github.ferstl.maven.pomenforcers.model.PomSection;

/**
 * JUnit tests for {@link PedanticPomSectionOrderEnforcer}.
 */
public class PedanticPomSectionOrderEnforcerTest extends AbstractPedanticEnforcerTest<PedanticPomSectionOrderEnforcer> {

  @Override
  PedanticPomSectionOrderEnforcer createRule() {
    return new PedanticPomSectionOrderEnforcer(mockMavenProject, mockHelper);
  }

  @Override
  @Test
  public void getDescription() {
    assertThat(this.testRule.getDescription()).isSameAs(PedanticEnforcerRule.POM_SECTION_ORDER);
  }

  @Override
  @Test
  public void accept() {
    PedanticEnforcerVisitor visitor = mock(PedanticEnforcerVisitor.class);
    this.testRule.accept(visitor);

    verify(visitor).visit(this.testRule);
  }

  @Test
  public void defaultSettingsCorrect() {
    configurePom(Arrays.asList(PomSection.values()));

    executeRuleAndCheckReport(false);
  }

  @Test
  public void defaultSettingsWrongOrder() {
    // Put <dependencyManagement> and <dependencies> in the wrong order.
    List<PomSection> sections = Arrays.asList(PomSection.values());
    swapSections(sections, PomSection.DEPENDENCY_MANAGEMENT, PomSection.DEPENDENCIES);
    configurePom(sections);

    executeRuleAndCheckReport(true);
  }

  @Test
  public void customSettingsCorrect() {
    // Parent declaration after project coordinates
    this.testRule.setSectionPriorities("modelVersion,groupId,artifactId,version,packaging,name,description,url,parent");

    // Re-arrange the POM sections according to the enforcer configuration.
    List<PomSection> sections = new ArrayList<>(Arrays.asList(PomSection.values()));
    List<PomSection> reorderedSections = Arrays.asList(
        PomSection.MODEL_VERSION, PomSection.GROUP_ID, PomSection.ARTIFACT_ID, PomSection.VERSION, PomSection.PACKAGING,
        PomSection.NAME, PomSection.DESCRIPTION, PomSection.URL, PomSection.PARENT);

    sections.removeAll(reorderedSections);
    sections.addAll(0, reorderedSections);

    configurePom(sections);

    executeRuleAndCheckReport(false);
  }


  @Test
  public void customSettingsWrongOrder() {
    this.testRule.setSectionPriorities("modelVersion,groupId,artifactId,version,packaging,name,description,url,parent");

    configurePom(Arrays.asList(PomSection.values()));

    executeRuleAndCheckReport(true);
  }

  private void configurePom(Collection<PomSection> sections) {
    Document pom = this.testRule.getPom();
    Element root = pom.getDocumentElement();

    for (PomSection section : sections) {
      root.appendChild(pom.createElement(section.getSectionName()));
    }
  }

  private void swapSections(List<PomSection> sections, PomSection first, PomSection second) {
    int iFirst = sections.indexOf(first);
    int iSecond = sections.indexOf(second);

    sections.set(iFirst, second);
    sections.set(iSecond, first);
  }

}
