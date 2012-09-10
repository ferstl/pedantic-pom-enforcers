package com.github.ferstl.maven.pomenforcers.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.xml.bind.annotation.XmlElement;

class DependenciesModel {

  @XmlElement(name = "dependency")
  private List<DependencyModel> dependencies;

  // Constructor used by JAXB
  DependenciesModel() {}

  public DependenciesModel(Collection<DependencyModel> dependencies) {
    this.dependencies = new ArrayList<>();
    this.dependencies.addAll(dependencies);
  }


  public List<DependencyModel> getDependencies() {
    return this.dependencies != null ? this.dependencies : Collections.<DependencyModel>emptyList();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (!(obj instanceof DependenciesModel)) {
      return false;
    }

    DependenciesModel other = (DependenciesModel) obj;
    return getDependencies().equals(other.getDependencies());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getDependencies());
  }

  @Override
  public String toString() {
    return CollectionToStringHelper.toString("Dependencies", this.dependencies);
  }
}
