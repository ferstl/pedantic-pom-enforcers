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

class PluginManagementModel {

  @XmlElement(namespace = "http://maven.apache.org/POM/4.0.0")
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
    if (!(obj instanceof PluginManagementModel)) {
      return false;
    }
    PluginManagementModel other = (PluginManagementModel) obj;
    return Objects.equals(this.plugins, other.plugins);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.plugins);
  }
}
