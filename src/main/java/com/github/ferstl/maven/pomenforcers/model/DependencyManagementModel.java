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


class DependencyManagementModel {

  private DependenciesModel dependencies;

  public List<DependencyModel> getDependencies() {
    return this.dependencies != null ? this.dependencies.getDependencies() : Collections.<DependencyModel>emptyList();
  }

  @Override
  public String toString() {
    return "DependencyManagement->" + Objects.toString(this.dependencies, "none");
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (!(obj instanceof DependencyManagementModel)) {
      return false;
    }
    DependencyManagementModel other = (DependencyManagementModel) obj;
    return Objects.equals(this.dependencies, other.dependencies);
  }

  @Override
  public int hashCode() {
    return this.dependencies != null ? this.dependencies.hashCode() : 0;
  }
}
