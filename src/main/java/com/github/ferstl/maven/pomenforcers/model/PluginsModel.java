/*
 * Copyright (c) 2012 - 2019 the original author or authors.
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

class PluginsModel {

  @XmlElement(name = "plugin", namespace = "http://maven.apache.org/POM/4.0.0")
  private List<PluginModel> plugins;

  public PluginsModel() {
  }

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
