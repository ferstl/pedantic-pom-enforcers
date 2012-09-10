package com.github.ferstl.maven.pomenforcers.model;


import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.github.ferstl.maven.pomenforcers.DependencyScope;
import com.google.common.base.Joiner;

import static com.google.common.base.Objects.equal;

@XmlRootElement(name = "dependency")
public class DependencyModel extends ArtifactModel {

  private static final Joiner TO_STRING_JOINER = Joiner.on(":");

  @XmlElement(name = "scope")
  private String scope;

  @XmlElement(name = "classifier")
  private String classifier;

  @XmlElement(name = "exclusions")
  private ExclusionModel exclusions;

  // Constructor used by JAXB
  DependencyModel() {}

  public DependencyModel(String groupId, String artifactId, String version, String scope, String classifier) {
    super(groupId, artifactId, version);
    this.scope = scope;
    this.classifier = classifier;
  }

  public String getScope() {
    if (this.scope == null) {
      return DependencyScope.COMPILE.getScopeName();
    }
    return this.scope;
  }

  public String getClassifier() {
    return this.classifier;
  }

  public List<ArtifactModel> getExclusions() {
    return this.exclusions != null ? this.exclusions.getExclusions() : Collections.<ArtifactModel>emptyList();
  }

  @Override
  public String toString() {
    return TO_STRING_JOINER.join(
        super.toString(),
        this.scope != null ? this.scope : "compile",
        this.classifier != null ? this.classifier : "<no classifier>",
        this.exclusions != null ? this.exclusions : "<no exclusions>");
  }

  // Note that this equals() implementation breaks the symmetry contract!
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof DependencyModel)) {
      return false;
    }

    DependencyModel other = (DependencyModel) obj;
    return super.equals(other)
        && equal(this.classifier, other.classifier)
        && equal(this.scope, other.scope)
        && equal(this.exclusions, other.exclusions);
  }

  @Override
  public int hashCode() {
   return Objects.hash(super.hashCode(), this.classifier, this.scope);
  }

}
