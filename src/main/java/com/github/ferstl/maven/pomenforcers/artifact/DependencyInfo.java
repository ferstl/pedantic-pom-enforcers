package com.github.ferstl.maven.pomenforcers.artifact;


import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.github.ferstl.maven.pomenforcers.DependencyScope;

import static com.google.common.base.Objects.equal;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "dependency")
public class DependencyInfo extends Artifact {

  @XmlElement(name = "scope")
  private String scope;

  @XmlElement(name = "classifier")
  private String classifier;

  @XmlElement(name = "version")
  private String version;


  // Constructor used by JAXB
  DependencyInfo() {}

  public DependencyInfo(String groupId, String artifactId, String version, String scope, String classifier) {
    super(groupId, artifactId);
    this.version = version;
    this.scope = scope;
    this.classifier = classifier;
  }

  public String getVersion() {
    return this.version;
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

  @Override
  public String toString() {
    return super.toString() + ":" + this.version + ":" + this.classifier + ":" + this.scope;
  }

  // Note that this equals() implementation breaks the symmetry contract!
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof DependencyInfo)) {
      return false;
    }

    DependencyInfo other = (DependencyInfo) obj;
    return super.equals(other)
        && equal(this.version, other.version)
        && equal(this.classifier, other.classifier)
        && equal(this.scope, other.scope);
  }

  @Override
  public int hashCode() {
   return Objects.hash(super.hashCode(), this.version, this.classifier, this.scope);
  }

}
