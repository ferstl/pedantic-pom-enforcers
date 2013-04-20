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

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.google.common.base.Joiner;

import static com.google.common.base.Objects.equal;

@XmlRootElement(name = "dependency")
public class DependencyModel extends ArtifactModel {

  private static final Joiner TO_STRING_JOINER = Joiner.on(":");

  @XmlJavaTypeAdapter(value = DependencyScopeAdapter.class)
  private DependencyScope scope;
  private String classifier;

  @XmlElementWrapper
  @XmlElement(name = "exclusion")
  private List<ArtifactModel> exclusions;

  // Constructor used by JAXB
  DependencyModel() {}

  public DependencyModel(String groupId, String artifactId, String version, String scope, String classifier) {
    super(groupId, artifactId, version);
    this.scope = scope != null ? DependencyScope.getByScopeName(scope) : null;
    this.classifier = classifier;
  }

  public DependencyScope getScope() {
    if (this.scope == null) {
      return DependencyScope.COMPILE;
    }
    return this.scope;
  }

  public String getClassifier() {
    return this.classifier;
  }

  public List<ArtifactModel> getExclusions() {
    return this.exclusions != null ? this.exclusions : Collections.<ArtifactModel>emptyList();
  }

  @Override
  public String toString() {
    return TO_STRING_JOINER.join(
        super.toString(),
        getScope().getScopeName(),
        this.classifier != null ? this.classifier : "<no classifier>",
        this.exclusions != null ? CollectionToStringHelper.toString("Exclusions", this.exclusions) : "<no exclusions>");
  }

  // Note that this equals() implementation breaks the symmetry contract!
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof DependencyModel)) {
      return false;
    }

    DependencyModel other = (DependencyModel) obj;
    return super.equals(other)
        && equal(this.classifier, other.classifier)
        && equal(this.scope, other.scope)
        && equal(this.exclusions, other.exclusions);
  }

  @Override
  public int hashCode() {
   return Objects.hash(super.hashCode(), this.classifier, this.scope);
  }

}
