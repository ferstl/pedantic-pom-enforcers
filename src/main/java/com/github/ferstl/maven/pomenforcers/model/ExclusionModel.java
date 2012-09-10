package com.github.ferstl.maven.pomenforcers.model;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.xml.bind.annotation.XmlElement;

class ExclusionModel {

  @XmlElement(name = "exclusion")
  private List<ArtifactModel> exclusions;

  public List<ArtifactModel> getExclusions() {
    return this.exclusions != null ? this.exclusions : Collections.<ArtifactModel>emptyList();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (!(obj instanceof ExclusionModel)) {
      return false;
    }
    ExclusionModel other = (ExclusionModel) obj;

    return getExclusions().equals(other.getExclusions());
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.exclusions);
  }

  @Override
  public String toString() {
    return CollectionToStringHelper.toString("Exclusions", this.exclusions);
  }
}
