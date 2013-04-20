/*
 * Copyright (c) 2013 by Stefan Ferstl <st.ferstl@gmail.com>
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
import javax.xml.bind.annotation.XmlElementWrapper;

import org.w3c.dom.Element;

import com.google.common.base.Joiner;

public class PluginModel extends ArtifactModel {

  private static final Joiner TO_STRING_JOINER = Joiner.on(":").skipNulls();

  @XmlElementWrapper(name = "configuration")
  @XmlAnyElement
  private List<Element> configItems;

  private DependenciesModel dependencies;

  PluginModel() {}

  public PluginModel(String groupId, String artifactId, String version) {
    super(groupId, artifactId, version);
  }

  public boolean isConfigured() {
    return this.configItems != null && !this.configItems.isEmpty();
  }

  public List<DependencyModel> getDependencies() {
    return this.dependencies != null ? this.dependencies.getDependencies() : Collections.<DependencyModel>emptyList();
  }

  @Override
  public String toString() {
    return TO_STRING_JOINER.join(
        super.toString(),
        isConfigured() ? "<no configuration>" : "<contains configuration>",
        this.dependencies != null ? "<contains dependencies>" : "<no dependenices>");
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
