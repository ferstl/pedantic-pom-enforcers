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
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import com.google.common.base.Joiner;
import static com.google.common.base.Objects.equal;

@XmlRootElement(name = "dependency")
public class DependencyModel extends ArtifactModel {

  private static final Joiner TO_STRING_JOINER = Joiner.on(":").skipNulls();

  @XmlElement(namespace = "http://maven.apache.org/POM/4.0.0")
  @XmlJavaTypeAdapter(value = DependencyScopeAdapter.class)
  private DependencyScope scope;

  @XmlElement(namespace = "http://maven.apache.org/POM/4.0.0")
  private String classifier;

  @XmlElement(namespace = "http://maven.apache.org/POM/4.0.0")
  private String type;

  @XmlElementWrapper(namespace = "http://maven.apache.org/POM/4.0.0")
  @XmlElement(name = "exclusion", namespace = "http://maven.apache.org/POM/4.0.0")
  private List<ArtifactModel> exclusions;

  // Constructor used by JAXB
  DependencyModel() {
  }

  public DependencyModel(
      String groupId, String artifactId, String version, String scope, String classifier, String type) {

    super(groupId, artifactId, version);
    this.scope = scope != null ? DependencyScope.getByScopeName(scope) : null;
    this.classifier = classifier;
    this.type = type;
  }

  public DependencyScope getScope() {
    return this.scope != null ? this.scope : DependencyScope.COMPILE;
  }

  public String getClassifier() {
    return this.classifier;
  }

  public String getType() {
    return this.type != null ? this.type : "jar";
  }

  public List<ArtifactModel> getExclusions() {
    return this.exclusions != null ? this.exclusions : Collections.emptyList();
  }

  @Override
  public String toString() {
    return TO_STRING_JOINER.join(
        super.toString(),
        getType(),
        getScope().getScopeName(),
        this.classifier);
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
        && equal(this.type, other.type)
        && equal(this.scope, other.scope)
        && equal(this.exclusions, other.exclusions);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), this.classifier, this.type, this.scope);
  }

}
