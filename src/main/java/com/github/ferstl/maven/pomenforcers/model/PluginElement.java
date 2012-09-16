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

import static com.github.ferstl.maven.pomenforcers.model.functions.Equivalences.stringStartsWith;




public enum PluginElement implements PriorityOrderingFactory<String, PluginModel>, Function<PluginModel, String> {

  GROUP_ID("groupId") {
    @Override
    public PriorityOrdering<String, PluginModel> createPriorityOrdering(Collection<String> priorityCollection) {
      return new PriorityOrdering<>(priorityCollection, this, stringStartsWith());
    }

    @Override
    public String apply(PluginModel input) {
      return input.getGroupId();
    }
  },

  ARTIFACT_ID("artifactId") {
    @Override
    public PriorityOrdering<String, PluginModel> createPriorityOrdering(Collection<String> priorityCollection) {
      return new PriorityOrdering<>(priorityCollection, this, stringStartsWith());
    }

    @Override
    public String apply(PluginModel input) {
      return input.getArtifactId();
    }
  };

  private static final Function<String, PluginElement> STRING_TO_PLUGIN_ELEMENT =
      new StringToPluginElementTransformer();

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

  public static Function<String, PluginElement> stringToPluginElement() {
    return STRING_TO_PLUGIN_ELEMENT;
  }

  private final String elementName;

  private PluginElement(String elementName) {
    this.elementName = elementName;
  }

  public String getElementName() {
    return this.elementName;
  }

  private static class StringToPluginElementTransformer implements Function<String, PluginElement> {
    @Override
    public PluginElement apply(String input) {
      return getByElementName(input);
    }
  }
}
