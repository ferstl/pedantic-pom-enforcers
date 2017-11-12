/*
 * Copyright (c) 2012 - 2015 by Stefan Ferstl <st.ferstl@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.ferstl.maven.pomenforcers.model;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import com.google.common.base.Joiner;

@XmlRootElement(name = "project")
public class ProjectModel {

  private static final Joiner TO_STRING_JOINER = Joiner.on("\n").skipNulls();

  @XmlElement(namespace = "http://maven.apache.org/POM/4.0.0")
  public String groupId;
  @XmlElement(namespace = "http://maven.apache.org/POM/4.0.0")
  public String artifactId;

  @XmlElementWrapper(namespace = "http://maven.apache.org/POM/4.0.0")
  @XmlElement(name = "module", namespace = "http://maven.apache.org/POM/4.0.0")
  public List<String> modules;

  @XmlElement(namespace = "http://maven.apache.org/POM/4.0.0")
  public DependencyManagementModel dependencyManagement;

  @XmlElement(namespace = "http://maven.apache.org/POM/4.0.0")
  public DependenciesModel dependencies;

  @XmlElement(namespace = "http://maven.apache.org/POM/4.0.0")
  public BuildModel build;

  public List<String> getModules() {
    return this.modules != null ? this.modules : Collections.<String>emptyList();
  }

  public List<DependencyModel> getManagedDependencies() {
    return this.dependencyManagement != null ?
        this.dependencyManagement.getDependencies() : Collections.<DependencyModel>emptyList();
  }

  public List<DependencyModel> getDependencies() {
    return this.dependencies != null ?
        this.dependencies.getDependencies() : Collections.<DependencyModel>emptyList();
  }

  public List<PluginModel> getManagedPlugins() {
    return this.build != null ? this.build.getManagedPlugins() : Collections.<PluginModel>emptyList();
  }

  public List<PluginModel> getPlugins() {
    return this.build != null ? this.build.getPlugins() : Collections.<PluginModel>emptyList();
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("Project ")
        .append(this.groupId)
        .append(":")
        .append(this.artifactId)
        .append(" [\n");
    return sb.toString();
    //    return TO_STRING_JOINER
    //        .appendTo(
    //            sb,
    //            CollectionToStringHelper.toString("Modules", this.modules),
    //            this.dependencyManagement,
    //            this.dependencies,
    //            this.build)
    //        .append("\n]")
    //        .toString();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (!(obj instanceof ProjectModel)) {
      return false;
    }
    ProjectModel other = (ProjectModel) obj;
    return Objects.equals(this.groupId, other.groupId)
        && Objects.equals(this.artifactId, other.artifactId)
        && Objects.equals(this.modules, other.modules)
        && Objects.equals(this.dependencyManagement, other.dependencyManagement)
        && Objects.equals(this.dependencies, other.dependencies)
        && Objects.equals(this.build, other.build);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        this.groupId, this.artifactId, this.modules, this.dependencyManagement, this.dependencies, this.build);
  }
}
