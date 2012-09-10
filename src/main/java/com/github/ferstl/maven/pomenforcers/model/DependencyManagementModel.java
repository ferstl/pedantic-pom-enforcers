package com.github.ferstl.maven.pomenforcers.model;

import java.util.Collections;
import java.util.List;
import java.util.Objects;


class DependencyManagementModel {

  private DependenciesModel dependencies;

  public List<DependencyModel> getDependencies() {
    return this.dependencies != null ? this.dependencies.getDependencies() : Collections.<DependencyModel>emptyList();
  }

  @Override
  public String toString() {
    return "DependencyManagement->" + Objects.toString(this.dependencies, "none");
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (!(obj instanceof DependencyManagementModel)) {
      return false;
    }
    DependencyManagementModel other = (DependencyManagementModel) obj;
    return Objects.equals(this.dependencies, other.dependencies);
  }

  @Override
  public int hashCode() {
    return this.dependencies != null ? this.dependencies.hashCode() : 0;
  }
}
