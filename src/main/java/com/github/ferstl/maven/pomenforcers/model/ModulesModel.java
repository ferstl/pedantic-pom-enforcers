package com.github.ferstl.maven.pomenforcers.model;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.xml.bind.annotation.XmlElement;

class ModulesModel {

  @XmlElement(name = "module")
  private List<String> modules;

  ModulesModel() {}

  public List<String> getModules() {
    return this.modules != null ? this.modules : Collections.<String>emptyList();
  }

  @Override
  public String toString() {
    return CollectionToStringHelper.toString("Modules", this.modules);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (!(obj instanceof ModulesModel)) {
      return false;
    }

    ModulesModel other = (ModulesModel) obj;
    return getModules().equals(other.getModules());
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.modules);
  }


}
