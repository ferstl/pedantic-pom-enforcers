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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import javax.xml.bind.annotation.XmlElement;

class DependenciesModel {

  @XmlElement(name = "dependency", namespace = "http://maven.apache.org/POM/4.0.0")
  private List<DependencyModel> dependencies;

  // Constructor used by JAXB
  DependenciesModel() {
  }

  public DependenciesModel(Collection<DependencyModel> dependencies) {
    this.dependencies = new ArrayList<>();
    this.dependencies.addAll(dependencies);
  }


  public List<DependencyModel> getDependencies() {
    return this.dependencies != null ? this.dependencies : Collections.emptyList();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (!(obj instanceof DependenciesModel)) {
      return false;
    }

    DependenciesModel other = (DependenciesModel) obj;
    return getDependencies().equals(other.getDependencies());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getDependencies());
  }

  @Override
  public String toString() {
    return CollectionToStringHelper.toString("Dependencies", this.dependencies);
  }
}
