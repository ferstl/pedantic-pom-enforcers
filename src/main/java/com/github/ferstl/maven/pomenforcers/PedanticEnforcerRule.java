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
package com.github.ferstl.maven.pomenforcers;

import com.google.common.base.Function;

/**
 * Each pedantic enforcer rule is identified by an ID. These IDs can be used within the
 * {@link CompoundPedanticEnforcer} to enable specific rules. The compound enforcer is more efficient
 * because it parses the POM file of each Maven module only once and delegates it to the configured
 * enforcer rules.
 */
public enum PedanticEnforcerRule {

  COMPOUND("One does not simply write a POM file!") {
    @Override
    public AbstractPedanticEnforcer createEnforcerRule() {
      throw new UnsupportedOperationException(
          "The " + CompoundPedanticEnforcer.class.getSimpleName()
              + " is not supposed to be instantiated outside the maven-enforcer-plugin.");
    }
  },

  /**
   * @see PedanticPomSectionOrderEnforcer
   */
  POM_SECTION_ORDER("One does not simply write a POM file!") {
    @Override
    public AbstractPedanticEnforcer createEnforcerRule() {
      return new PedanticPomSectionOrderEnforcer();
    }
  },
  /**
   * @see PedanticModuleOrderEnforcer
   */
  MODULE_ORDER("One does not simply declare modules!") {
    @Override
    public AbstractPedanticEnforcer createEnforcerRule() {
      return new PedanticModuleOrderEnforcer();
    }
  },
  /**
   * @see PedanticDependencyManagementOrderEnforcer
   */
  DEPENDENCY_MANAGEMENT_ORDER("One does not simply declare dependency management!") {
    @Override
    public AbstractPedanticEnforcer createEnforcerRule() {
      return new PedanticDependencyManagementOrderEnforcer();
    }
  },
  /**
   * @see PedanticDependencyManagementLocationEnforcer
   */
  DEPENDENCY_MANAGEMENT_LOCATION("One does not simply declare dependency management!") {
    @Override
    public AbstractPedanticEnforcer createEnforcerRule() {
      return new PedanticDependencyManagementLocationEnforcer();
    }
  },
  /**
   * @see PedanticDependencyOrderEnforcer
   */
  DEPENDENCY_ORDER("One does not simply declare dependencies!") {
    @Override
    public AbstractPedanticEnforcer createEnforcerRule() {
      return new PedanticDependencyOrderEnforcer();
    }
  },
  /**
   * @see PedanticDependencyConfigurationEnforcer
   */
  DEPENDENCY_CONFIGURATION("One does not simply configure dependencies!") {
    @Override
    public AbstractPedanticEnforcer createEnforcerRule() {
      return new PedanticDependencyConfigurationEnforcer();
    }
  },

  /**
   * @see PedanticDependencyElementEnforcer
   */
  DEPENDENCY_ELEMENT("One does not simply declare a dependency!") {
    @Override
    public AbstractPedanticEnforcer createEnforcerRule() {
      return new PedanticDependencyElementEnforcer();
    }
  },

  /**
   * @see PedanticDependencyScopeEnforcer
   */
  DEPENDENCY_SCOPE("One does not simply declare dependency scopes!") {
    @Override
    public AbstractPedanticEnforcer createEnforcerRule() {
      return new PedanticDependencyScopeEnforcer();
    }
  },
  /**
   * @see PedanticPluginManagementOrderEnforcer
   */
  PLUGIN_MANAGEMENT_ORDER("One does not simply declare plugin management!") {
    @Override
    public AbstractPedanticEnforcer createEnforcerRule() {
      return new PedanticPluginManagementOrderEnforcer();
    }
  },
  /**
   * @see PedanticPluginConfigurationEnforcer
   */
  PLUGIN_CONFIGURATION("One does not simply configure plugins!") {
    @Override
    public AbstractPedanticEnforcer createEnforcerRule() {
      return new PedanticPluginConfigurationEnforcer();
    }
  },
  /**
   * @see PedanticPluginManagementLocationEnforcer
   */
  PLUGIN_MANAGEMENT_LOCATION("One does not simply declare plugin management!") {
    @Override
    public AbstractPedanticEnforcer createEnforcerRule() {
      return new PedanticPluginManagementLocationEnforcer();
    }
  };

  private final String slogan;

  PedanticEnforcerRule(String slogan) {
    this.slogan = slogan;
  }

  public String getSlogan() {
    return this.slogan;
  }

  public static Function<String, PedanticEnforcerRule> stringToEnforcerRule() {
    return StringToEnforcerRuleTransformer.INSTANCE;
  }

  public abstract AbstractPedanticEnforcer createEnforcerRule();

  private enum StringToEnforcerRuleTransformer implements Function<String, PedanticEnforcerRule> {
    INSTANCE;

    @Override
    public PedanticEnforcerRule apply(String input) {
      return PedanticEnforcerRule.valueOf(input);
    }
  }
}
