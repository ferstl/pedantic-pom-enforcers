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
package com.github.ferstl.maven.pomenforcers.model;

import java.util.Collection;
import java.util.Map;

import com.github.ferstl.maven.pomenforcers.priority.PriorityOrdering;
import com.github.ferstl.maven.pomenforcers.priority.PriorityOrderingFactory;
import com.google.common.base.Function;
import com.google.common.collect.Maps;

import static com.github.ferstl.maven.pomenforcers.model.functions.DependencyElementExtractor.ARTIFACT_ID_EXTRACTOR;
import static com.github.ferstl.maven.pomenforcers.model.functions.DependencyElementExtractor.GROUP_ID_EXTRACTOR;
import static com.github.ferstl.maven.pomenforcers.model.functions.DependencyElementExtractor.SCOPE_EXTRACTOR;
import static com.github.ferstl.maven.pomenforcers.model.functions.Equivalences.stringStartsWith;




public enum DependencyElement implements PriorityOrderingFactory<String, DependencyModel> {
  GROUP_ID("groupId") {
    @Override
    public PriorityOrdering<String, DependencyModel> createPriorityOrdering(Collection<String> priorityCollection) {
      return new PriorityOrdering<>(priorityCollection, GROUP_ID_EXTRACTOR, stringStartsWith());
    }
  },

  ARTIFACT_ID("artifactId") {
    @Override
    public PriorityOrdering<String, DependencyModel> createPriorityOrdering(Collection<String> priorityCollection) {
      return new PriorityOrdering<>(priorityCollection, ARTIFACT_ID_EXTRACTOR, stringStartsWith());
    }
  },

  SCOPE("scope") {
    @Override
    public PriorityOrdering<String, DependencyModel> createPriorityOrdering(Collection<String> priorityCollection) {
      return new PriorityOrdering<>(priorityCollection, SCOPE_EXTRACTOR);
    }
  };

  private static final Function<String, DependencyElement> STRING_TO_DEPENDENCY_ELEMENT =
      new StringToDependencyElementTransformer();

  private static Map<String, DependencyElement> elementMap;

  static {
    elementMap = Maps.newLinkedHashMap();
    for (DependencyElement element : values()) {
      elementMap.put(element.getElementName(), element);
    }
  }

  public static DependencyElement getByElementName(String elementName) {
    if (elementName == null) {
      throw new NullPointerException("Element name is null");
    }

    DependencyElement result = elementMap.get(elementName);
    if (result == null) {
      throw new IllegalArgumentException("No dependency element with name " + elementName);
    }

    return result;
  }

  public static Function<String, DependencyElement> stringToDependencyElement() {
    return STRING_TO_DEPENDENCY_ELEMENT;
  }

  private final String elementName;

  private DependencyElement(String elementName) {
    this.elementName = elementName;
  }

  public String getElementName() {
    return this.elementName;
  }

  private static class StringToDependencyElementTransformer implements Function<String, DependencyElement> {
    @Override
    public DependencyElement apply(String input) {
      return getByElementName(input);
    }
  }
}