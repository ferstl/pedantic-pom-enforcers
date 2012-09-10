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

import java.util.LinkedHashMap;
import java.util.Map;

public enum DependencyScope {

  COMPILE("compile"),
  PROVIDED("provided"),
  RUNTIME("runtime"),
  TEST("test"),
  SYSTEM("system"),
  IMPORT("import");

  private static final Map<String, DependencyScope> dependencyScopeMap;

  static {
    dependencyScopeMap = new LinkedHashMap<>();
    for (DependencyScope scope : values()) {
      dependencyScopeMap.put(scope.getScopeName(), scope);
    }
  }

  public static DependencyScope getByScopeName(String scopeName) {
    if (scopeName == null) {
      throw new NullPointerException("Scope name is null.");
    }

    DependencyScope scope = dependencyScopeMap.get(scopeName);
    if (scope == null) {
      throw new IllegalArgumentException("Dependency scope'" + scopeName + "' does not exist.");
    }
    return scope;
  }

  private final String scopeName;

  private DependencyScope(String name) {
    this.scopeName = name;
  }

  public String getScopeName() {
    return this.scopeName;
  }
}
