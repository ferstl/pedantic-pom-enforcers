package com.github.ferstl.maven.pomenforcers.artifact;

import com.google.common.base.Equivalence;

public class ArtifactInfo {
  private final String groupId;
  private final String artifactId;

  public ArtifactInfo(String groupId, String artifactId) {
    this.groupId = groupId;
    this.artifactId = artifactId;
  }

  public String getGroupId() {
    return this.groupId;
  }

  public String getArtifactId() {
    return this.artifactId;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof ArtifactInfo) {
      return new ArtifactInfoEquivalence().equivalent(this, (ArtifactInfo) obj);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return new ArtifactInfoEquivalence().hash(this);
  }

  @Override
  public String toString() {
    return this.groupId + ":" + this.artifactId;
  }

  private class ArtifactInfoEquivalence extends Equivalence<ArtifactInfo> {

    @Override
    protected boolean doEquivalent(ArtifactInfo a, ArtifactInfo b) {
      return a.groupId.equals(b.groupId) && a.artifactId.equals(b.artifactId);
    }

    @Override
    protected int doHash(ArtifactInfo t) {
      return (t.groupId + ":" + t.artifactId).hashCode();
    }

  }
}