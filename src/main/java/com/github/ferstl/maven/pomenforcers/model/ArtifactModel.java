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

import java.util.Objects;
import javax.xml.bind.annotation.XmlElement;
import com.google.common.base.Joiner;

import static com.google.common.base.Objects.equal;

public class ArtifactModel {

  private static final Joiner TO_STRING_JOINER = Joiner.on(":").useForNull("");

  @XmlElement(namespace = "http://maven.apache.org/POM/4.0.0")
  private String groupId;
  @XmlElement(namespace = "http://maven.apache.org/POM/4.0.0")
  private String artifactId;
  @XmlElement(namespace = "http://maven.apache.org/POM/4.0.0")
  private String version;

  ArtifactModel() {
  }

  public ArtifactModel(String groupId, String artifactId, String version) {
    this.groupId = groupId;
    this.artifactId = artifactId;
    this.version = version;
  }

  public ArtifactModel(String groupId, String artifactId) {
    this.groupId = groupId;
    this.artifactId = artifactId;
    this.version = null;
  }

  public String getGroupId() {
    return this.groupId;
  }

  public String getArtifactId() {
    return this.artifactId;
  }

  public String getVersion() {
    return this.version;
  }

  @Override
  public String toString() {
    return TO_STRING_JOINER.join(
        this.groupId,
        this.artifactId,
        this.version);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof ArtifactModel)) {
      return false;
    }

    ArtifactModel other = (ArtifactModel) obj;
    return equal(this.groupId, other.groupId)
        && equal(this.artifactId, other.artifactId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.groupId, this.artifactId);
  }
}
