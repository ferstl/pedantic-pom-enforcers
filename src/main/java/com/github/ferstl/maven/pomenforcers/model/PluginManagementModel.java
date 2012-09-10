package com.github.ferstl.maven.pomenforcers.model;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class PluginManagementModel {

  private PluginsModel plugins;

  public List<PluginModel> getPlugins() {
    return this.plugins != null ? this.plugins.getPlugins() : Collections.<PluginModel>emptyList();
  }

  @Override
  public String toString() {
    return "PluginManagement->" + Objects.toString(this.plugins, "none");
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (!(obj instanceof PluginsModel)) {
      return false;
    }
    PluginManagementModel other = (PluginManagementModel) obj;
    return Objects.equals(this.plugins, other.plugins);
  }

  @Override
  public int hashCode() {
    return this.plugins != null ? this.plugins.hashCode() : 0;
  }
}
