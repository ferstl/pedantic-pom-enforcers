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
package com.github.ferstl.maven.pomenforcers.model;

import java.util.Map;
import com.google.common.collect.Maps;
import static java.util.Objects.requireNonNull;


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

  private static final Map<String, PomSection> pomSectionMap;

  static {
    pomSectionMap = Maps.newHashMap();
    for (PomSection pomSection : values()) {
      pomSectionMap.put(pomSection.getSectionName(), pomSection);
    }
  }

  public static PomSection getBySectionName(String sectionName) {
    requireNonNull(sectionName, "Section name is null.");

    PomSection value = pomSectionMap.get(sectionName);
    if (value == null) {
      throw new IllegalArgumentException("POM section " + sectionName + " does not exist.");
    }

    return value;
  }

  private final String sectionName;

  PomSection(String sectionName) {
    this.sectionName = sectionName;
  }

  public String getSectionName() {
    return this.sectionName;
  }
}
