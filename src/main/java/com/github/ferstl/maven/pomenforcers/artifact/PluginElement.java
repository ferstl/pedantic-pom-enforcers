/*
 * Copyright (c) 2012 by The Author(s)
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
package com.github.ferstl.maven.pomenforcers.artifact;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;

import com.github.ferstl.maven.pomenforcers.priority.PriorityComparator;
import com.github.ferstl.maven.pomenforcers.priority.PriorityComparatorFactory;
import com.github.ferstl.maven.pomenforcers.priority.StringStartsWithEquivalence;
import com.google.common.base.Function;
import com.google.common.collect.Maps;


public enum PluginElement implements PriorityComparatorFactory<String, Artifact> {

  GROUP_ID("groupId") {
    @Override
    public Comparator<Artifact> createPriorityComparator(Collection<String> priorityCollection) {
      StringStartsWithEquivalence priorityMatcher = new StringStartsWithEquivalence();
      Function<Artifact, String> transformer = new Function<Artifact, String>() {
        @Override
        public String apply(Artifact input) {
          return input.getGroupId();
        }
      };
      return new PriorityComparator<>(priorityCollection, transformer, priorityMatcher);
    }
  },

  ARTIFACT_ID("artifactId") {
    @Override
    public Comparator<Artifact> createPriorityComparator(Collection<String> priorityCollection) {
      StringStartsWithEquivalence priorityMatcher = new StringStartsWithEquivalence();
      Function<Artifact, String> transformer = new Function<Artifact, String>() {
        @Override
        public String apply(Artifact input) {
          return input.getArtifactId();
        }
      };
      return new PriorityComparator<>(priorityCollection, transformer, priorityMatcher);
    }
  };

  private static Map<String, PluginElement> elementMap;

  static {
    elementMap = Maps.newLinkedHashMap();
    for (PluginElement element : values()) {
      elementMap.put(element.getElementName(), element);
    }
  }

  public static PluginElement getByElementName(String elementName) {
    if (elementName == null) {
      throw new NullPointerException("Element name is null");
    }

    PluginElement result = elementMap.get(elementName);
    if (result == null) {
      throw new IllegalArgumentException("No plugin element with name " + elementName);
    }

    return result;
  }

  private final String elementName;

  private PluginElement(String elementName) {
    this.elementName = elementName;
  }

  public String getElementName() {
    return this.elementName;
  }
}
