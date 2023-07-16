/*
 * Copyright (c) 2012 - 2023 the original author or authors.
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
import com.google.common.base.Joiner;

class BuildModel {

  private static final Joiner TO_STRING_JOINER = Joiner.on("\n");

  @XmlElement(namespace = "http://maven.apache.org/POM/4.0.0")
  private PluginManagementModel pluginManagement;

  @XmlElement(namespace = "http://maven.apache.org/POM/4.0.0")
  private PluginsModel plugins;


  public List<PluginModel> getManagedPlugins() {
    return this.pluginManagement != null ? this.pluginManagement.getPlugins() : Collections.emptyList();
  }

  public List<PluginModel> getPlugins() {
    return this.plugins != null ? this.plugins.getPlugins() : Collections.emptyList();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (!(obj instanceof BuildModel)) {
      return false;
    }

    BuildModel other = (BuildModel) obj;
    return Objects.equals(this.pluginManagement, other.pluginManagement)
        && Objects.equals(this.plugins, other.plugins);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.pluginManagement, this.plugins);
  }

  @Override
  public String toString() {
    return TO_STRING_JOINER.join(this.pluginManagement, this.plugins);
  }
}
