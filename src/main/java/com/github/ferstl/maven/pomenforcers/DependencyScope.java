package com.github.ferstl.maven.pomenforcers;

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
