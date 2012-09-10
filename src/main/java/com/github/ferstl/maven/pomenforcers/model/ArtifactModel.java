package com.github.ferstl.maven.pomenforcers.model;

import java.util.Objects;

import javax.xml.bind.annotation.XmlElement;

import com.google.common.base.Joiner;

import static com.google.common.base.Objects.equal;

public class ArtifactModel {

  private static final Joiner TO_STRING_JOINER = Joiner.on(":");

  @XmlElement(name = "groupId")
  private String groupId;

  @XmlElement(name = "artifactId")
  private String artifactId;

  @XmlElement(name = "version")
  private String version;

  ArtifactModel() {}

  public ArtifactModel(String groupId, String artifactId, String version) {
    this.groupId = groupId;
    this.artifactId = artifactId;
    this.version = version;
  }

  public String getGroupId() {
    return this.groupId;
  }

  public String getArtifactId() {
    return this.artifactId;
  }

  public String getVersion() {
    return this.version;
  }

  @Override
  public String toString() {
    return TO_STRING_JOINER.join(
        this.groupId != null ? this.groupId : "<no groupId>",
        this.artifactId,
        this.version != null ? this.version : "<no version>");
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof ArtifactModel)) {
      return false;
    }

    ArtifactModel other = (ArtifactModel) obj;
    return equal(this.groupId, other.groupId)
        && equal(this.artifactId, other.artifactId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.groupId, this.artifactId);
  }
}
