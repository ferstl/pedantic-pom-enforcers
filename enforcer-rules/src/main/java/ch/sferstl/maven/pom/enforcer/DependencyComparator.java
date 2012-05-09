package ch.sferstl.maven.pom.enforcer;

import java.util.Comparator;

import org.apache.maven.model.Dependency;

public enum DependencyComparator implements Comparator<Dependency> {
  SCOPE {
    @Override
    public int compare(Dependency d1, Dependency d2) {
      String d1Scope = d1.getScope() != null ? d1.getScope() : "compile";
      String d2Scope = d2.getScope() != null ? d2.getScope() : "compile";
      return d1Scope.compareTo(d2Scope);
    }
  },
  GROUP_ID {
    @Override
    public int compare(Dependency d1, Dependency d2) {
      return d1.getGroupId().compareTo(d2.getGroupId());
    }
  },
  ARTIFACT_ID {
    @Override
    public int compare(Dependency d1, Dependency d2) {
      return d1.getArtifactId().compareTo(d2.getArtifactId());
    }
  },
}