/*
 * Copyright (c) 2012 by The Author(s)
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

import java.util.Collection;
import java.util.Map;

import com.github.ferstl.maven.pomenforcers.priority.PriorityOrdering;
import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;


public enum PomSection {
  MODEL_VERSION("modelVersion"),
  PREREQUISITES("prerequisites"),
  PARENT("parent"),
  GROUP_ID("groupId"),
  ARTIFACT_ID("artifactId"),
  VERSION("version"),
  PACKAGING("packaging"),
  NAME("name"),
  DESCRIPTION("description"),
  URL("url"),
  LICENSES("licenses"),
  ORGANIZATION("organization"),
  INCEPTION_YEAR("inceptionYear"),
  CI_MANAGEMENT("ciManagement"),
  MAILING_LISTS("mailingLists"),
  ISSUE_MANAGEMENT("issueManagement"),
  DEVELOPERS("developers"),
  CONTRIBUTORS("contributors"),
  SCM("scm"),
  REPOSITORIES("repositories"),
  PLUGIN_REPOSITORIES("pluginRepositories"),
  DISTRIBUTION_MANAGEMENT("distributionManagement"),
  MODULES("modules"),
  PROPERTIES("properties"),
  DEPENDENCY_MANAGEMENT("dependencyManagement"),
  DEPENDENCIES("dependencies"),
  BUILD("build"),
  PROFILES("profiles"),
  REPORTING("reporting"),
  REPORTS("reports");

  private static final Function<String, PomSection> STRING_TO_POM_SECTION = new StringToPomSectionTransformer();
  private static final Function<PomSection, String> POM_SECTION_TO_STRING = new PomSectionToStringTransformer();
  private static final Map<String, PomSection> pomSectionMap;

  static {
    pomSectionMap = Maps.newHashMap();
    for (PomSection pomSection : values()) {
      pomSectionMap.put(pomSection.getSectionName(), pomSection);
    }
  }

  public static PomSection getBySectionName(String sectionName) {
    if (sectionName == null) {
      throw new NullPointerException("Section name is null.");
    }

    PomSection value = pomSectionMap.get(sectionName);
    if (value == null) {
      throw new IllegalArgumentException("POM section " + sectionName + " does not exist.");
    }
    return value;
  }

  public static Function<String, PomSection> stringToPomSection() {
    return STRING_TO_POM_SECTION;
  }

  public static Function<PomSection, String> pomSectionToString() {
    return POM_SECTION_TO_STRING;
  }

  private final String sectionName;

  public static Ordering<PomSection> createPriorityOrdering(Collection<PomSection> priorityCollection) {
    return new PriorityOrdering<>(priorityCollection, Functions.<PomSection>identity());
  }

  private PomSection(String sectionName) {
    this.sectionName = sectionName;
  }

  public String getSectionName() {
    return this.sectionName;
  }

  private static class StringToPomSectionTransformer implements Function<String, PomSection> {
    @Override
    public PomSection apply(String input) {
      return getBySectionName(input);
    }
  }

  private static class PomSectionToStringTransformer implements Function<PomSection, String> {
    @Override
    public String apply(PomSection input) {
      return input.getSectionName();
    }
  }

}
