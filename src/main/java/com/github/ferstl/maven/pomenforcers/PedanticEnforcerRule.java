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
package com.github.ferstl.maven.pomenforcers;

import com.google.common.base.Function;

/**
 * Each pedantic enforcer rule is identified by an ID. These IDs can be used within the
 * {@link CompoundPedanticEnforcer} to enable specific rules. The compound enforcer is more efficient
 * because it parses the POM file of each Maven module only once and delegates it to the configured
 * enforcer rules.
 */
public enum PedanticEnforcerRule {

  /**
   * @see PedanticPomSectionOrderEnforcer
   */
  POM_SECTION_ORDER {
    @Override
    public AbstractPedanticEnforcer createEnforcerRule() {
      return new PedanticPomSectionOrderEnforcer();
    }
  },
  /**
   * @see PedanticModuleOrderEnforcer
   */
  MODULE_ORDER {
    @Override
    public AbstractPedanticEnforcer createEnforcerRule() {
      return new PedanticModuleOrderEnforcer();
    }
  },
  /**
   * @see PedanticDependencyManagementOrderEnforcer
   */
  DEPENDENCY_MANAGEMENT_ORDER {
    @Override
    public AbstractPedanticEnforcer createEnforcerRule() {
      return new PedanticDependencyManagementOrderEnforcer();
    }
  },
  /**
   * @see PedanticDependencyManagementLocationEnforcer
   */
  DEPENDENCY_MANAGEMENT_LOCATION {
    @Override
    public AbstractPedanticEnforcer createEnforcerRule() {
      return new PedanticDependencyManagementLocationEnforcer();
    }
  },
  /**
   * @see PedanticDependencyOrderEnforcer
   */
  DEPENDENCY_ORDER {
    @Override
    public AbstractPedanticEnforcer createEnforcerRule() {
      return new PedanticDependencyOrderEnforcer();
    }
  },
  /**
   * @see PedanticDependencyConfigurationEnforcer
   */
  DEPENDENCY_CONFIGURATION {
    @Override
    public AbstractPedanticEnforcer createEnforcerRule() {
      return new PedanticDependencyConfigurationEnforcer();
    }
  },
  /**
   * @see PedanticDependencyScopeEnforcer
   */
  DEPENDENCY_SCOPE {
    @Override
    public AbstractPedanticEnforcer createEnforcerRule() {
      return new PedanticDependencyScopeEnforcer();
    }
  },
  /**
   * @see PedanticPluginManagementOrderEnforcer
   */
  PLUGIN_MANAGEMENT_ORDER {
    @Override
    public AbstractPedanticEnforcer createEnforcerRule() {
      return new PedanticPluginManagementOrderEnforcer();
    }
  },
  /**
   * @see PedanticPluginConfigurationEnforcer
   */
  PLUGIN_CONFIGURATION {
    @Override
    public AbstractPedanticEnforcer createEnforcerRule() {
      return new PedanticPluginConfigurationEnforcer();
    }
  },
  /**
   * @see PedanticPluginManagementLocationEnforcer
   */
  PLUGIN_MANAGEMENT_LOCATION {
    @Override
    public AbstractPedanticEnforcer createEnforcerRule() {
      return new PedanticPluginManagementLocationEnforcer();
    }
  };

  public static Function<String, PedanticEnforcerRule> stringToEnforcerRule() {
    return StringToEnforcerRuleTransformer.INSTANCE;
  }

  public abstract AbstractPedanticEnforcer createEnforcerRule();

  private static enum StringToEnforcerRuleTransformer implements Function<String, PedanticEnforcerRule> {
    INSTANCE;

    @Override
    public PedanticEnforcerRule apply(String input) {
      return PedanticEnforcerRule.valueOf(input);
    }
  }
}
