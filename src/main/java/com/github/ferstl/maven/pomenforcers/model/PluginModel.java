package com.github.ferstl.maven.pomenforcers.model;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.xml.bind.annotation.XmlElement;

import com.google.common.base.Joiner;

public class PluginModel extends ArtifactModel {

  private static final Joiner TO_STRING_JOINER = Joiner.on(":").skipNulls();

  @XmlElement(name = "configuration")
  private PluginConfigurationModel configuration;

  @XmlElement(name = "dependencies")
  private DependenciesModel dependencies;

  PluginModel() {}

  public PluginModel(String groupId, String artifactId, String version) {
    super(groupId, artifactId, version);
  }

  public boolean isConfigured() {
    return this.configuration != null && this.configuration.isConfigured();
  }

  public List<DependencyModel> getDependencies() {
    return this.dependencies != null ? this.dependencies.getDependencies() : Collections.<DependencyModel>emptyList();
  }

  @Override
  public String toString() {
    return TO_STRING_JOINER.join(
        super.toString(),
        isConfigured() ? "<no configuration>" : "<contains configuration>",
        this.dependencies != null ? this.dependencies : "<no dependenices>");
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (!(obj instanceof PluginModel)) {
      return false;
    }

    PluginModel other = (PluginModel) obj;
    return super.equals(other)
        && Objects.equals(this.configuration, other.configuration)
        && Objects.equals(this.dependencies, other.dependencies);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), this.configuration, this.dependencies);
  }
}
