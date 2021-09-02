/*
 * Copyright (c) 2012 - 2020 the original author or authors.
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
  private static final String WILDCARD = "*";
  private static final char WILDCARD_CHAR = WILDCARD.charAt(0);

  @XmlElement(namespace = "http://maven.apache.org/POM/4.0.0")
  private String groupId;
  @XmlElement(namespace = "http://maven.apache.org/POM/4.0.0")
  private String artifactId;
  @XmlElement(namespace = "http://maven.apache.org/POM/4.0.0")
  private String version;

  ArtifactModel() {
  }

  public ArtifactModel(String groupId, String artifactId, String version) {
    // Make sure that wildcards are valid
    determineWildcardMode(groupId);
    determineWildcardMode(artifactId);

    this.groupId = groupId;
    this.artifactId = artifactId;
    this.version = version;

  }

  public ArtifactModel(String groupId, String artifactId) {
    this(groupId, artifactId, null);
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

  public boolean matches(ArtifactModel pattern) {
    if (pattern == this) {
      return true;
    }

    if (pattern == null) {
      return false;
    }

    return match(this.groupId, pattern.groupId)
        && match(this.artifactId, pattern.artifactId);
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

  private static WildcardMode determineWildcardMode(String string) {
    if (string == null) {
      return WildcardMode.NONE;
    }

    int wildcardCount = 0;
    for (int i = 0; i < string.length(); i++) {
      if (string.charAt(i) == WILDCARD_CHAR) {
        wildcardCount++;
      }
    }

    if (wildcardCount == 0) {
      return WildcardMode.NONE;
    } else if (wildcardCount == 1 && string.length() == 1) {
      return WildcardMode.FULL;
    } else if (wildcardCount == 1 && string.startsWith(WILDCARD)) {
      return WildcardMode.LEADING;
    } else if (wildcardCount == 1 && string.endsWith(WILDCARD)) {
      return WildcardMode.TRAILING;
    } else if (wildcardCount == 2 && string.startsWith(WILDCARD) && string.endsWith(WILDCARD)) {
      return WildcardMode.CONTAINS;
    } else {
      throw new IllegalArgumentException("Invalid wildcard pattern '" + string + "'");
    }
  }

  private static boolean match(String string, String pattern) {
    switch (determineWildcardMode(pattern)) {
      case NONE:
        return string.equals(pattern);
      case LEADING:
        return string.endsWith(pattern.substring(1));
      case TRAILING:
        return string.startsWith(pattern.substring(0, pattern.length() - 1));
      case CONTAINS:
        return string.contains(pattern.substring(1, pattern.length() - 1));
      case FULL:
        return true;
      default:
        return false;
    }
  }

  private enum WildcardMode {
    NONE, LEADING, TRAILING, CONTAINS, FULL
  }
}
