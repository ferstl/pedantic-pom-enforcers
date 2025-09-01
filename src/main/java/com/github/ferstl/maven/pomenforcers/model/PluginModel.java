/*
 * Copyright (c) 2012 - 2025 the original author or authors.
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
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import org.w3c.dom.Element;

public class PluginModel extends ArtifactModel {

  @XmlElementWrapper(name = "configuration", namespace = "http://maven.apache.org/POM/4.0.0")
  @XmlAnyElement
  private List<Element> configItems;

  @XmlElement(namespace = "http://maven.apache.org/POM/4.0.0")
  private DependenciesModel dependencies;

  PluginModel() {
  }

  public PluginModel(String groupId, String artifactId, String version) {
    super(groupId, artifactId, version);
  }

  public boolean isConfigured() {
    return this.configItems != null && !this.configItems.isEmpty();
  }

  public List<DependencyModel> getDependencies() {
    return this.dependencies != null ? this.dependencies.getDependencies() : Collections.emptyList();
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
        // TODO: Element implementations may not implement equals()!!
        && Objects.equals(this.configItems, other.configItems)
        && Objects.equals(this.dependencies, other.dependencies);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), this.configItems, this.dependencies);
  }
}
