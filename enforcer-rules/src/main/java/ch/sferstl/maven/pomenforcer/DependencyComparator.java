package ch.sferstl.maven.pomenforcer;

import java.util.Comparator;

import org.apache.maven.model.Dependency;

public enum DependencyComparator implements Comparator<Dependency> {
  SCOPE {
    @Override
    public int compare(Dependency d1, Dependency d2) {
      return d1.getScope().compareTo(d2.getScope());
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