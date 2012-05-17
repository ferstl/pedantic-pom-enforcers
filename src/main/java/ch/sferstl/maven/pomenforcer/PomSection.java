package ch.sferstl.maven.pomenforcer;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.collect.Maps;


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
    if (sectionName == null) {
      throw new NullPointerException("Section name is null.");
    }

    PomSection value = pomSectionMap.get(sectionName);
    if (value == null) {
      throw new IllegalArgumentException("POM section " + sectionName + " does not exist.");
    }
    return value;
  }

  private final String sectionName;

  private PomSection(String sectionName) {
    this.sectionName = sectionName;
  }

  public String getSectionName() {
    return this.sectionName;
  }

  public static Comparator<PomSection> createPriorityComparator(Collection<PomSection> priorityList) {
    Function<PomSection, PomSection> identity = Functions.identity();
      return new PriorityComparator<>(priorityList, identity);
  }

}
