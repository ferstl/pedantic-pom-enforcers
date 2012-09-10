package com.github.ferstl.maven.pomenforcers.model;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.xml.bind.annotation.XmlElement;

public class PluginsModel {

  @XmlElement(name = "plugin")
  private List<PluginModel> plugins;

  public PluginsModel() {}

  public List<PluginModel> getPlugins() {
    return this.plugins != null ? this.plugins : Collections.<PluginModel>emptyList();
  }

  @Override
  public String toString() {
    return CollectionToStringHelper.toString("Plugins", this.plugins);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (!(obj instanceof PluginsModel)) {
      return false;
    }
    PluginsModel other = (PluginsModel) obj;
    return getPlugins().equals(other.getPlugins());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getPlugins());
  }
}
