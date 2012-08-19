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
package com.github.ferstl.maven.pomenforcers.reader;

public final class XPathExpressions {

  public static final String POM_MODULES = "/project/modules";
  public static final String POM_MANAGED_DEPENDENCIES = "/project/dependencyManagement/dependencies/dependency";
  public static final String POM_DEPENENCIES = "/project/dependencies/dependency";
  public static final String POM_VERSIONED_DEPENDENCIES = "/project/dependencies/dependency[version]";
  public static final String POM_DEPENDENCIES_WITH_EXCLUSIONS = "/project/dependencies/dependency[exclusions]";

  public static final String POM_MANAGED_PLUGINS = "/project/build/pluginManagement/plugins";
  public static final String POM_PLUGINS = "/project/build/plugins";
  public static final String POM_VERSIONED_PLUGINS = "/project/build/plugins/plugin[version]";
  public static final String POM_CONFIGURED_PLUGINS = "/project/build/plugins/plugin[configuration]";
  public static final String POM_CONFIGURED_PLUGIN_DEPENDENCIES = "/project/build/plugins/plugin[dependencies]";

  public static final String STANDALONE_DEPENDENCIES = "/dependencies/dependency";
  public static final String STANDALONE_PLUGINS = "/plugins";

  private XPathExpressions() {}
}
