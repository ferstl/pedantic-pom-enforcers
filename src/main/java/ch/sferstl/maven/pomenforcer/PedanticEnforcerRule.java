/*
 * Copyright (c) 2012 by The Authors of the Pedantic POM Enforcers
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
package ch.sferstl.maven.pomenforcer;


public enum PedanticEnforcerRule {

  POM_SECTION_ORDER {
    @Override
    public AbstractPedanticEnforcer createEnforcerRule() {
      return new PedanticPomSectionOrderEnforcer();
    }
  },
  MODULE_ORDER {
    @Override
    public AbstractPedanticEnforcer createEnforcerRule() {
      return new PedanticModuleOrderEnforcer();
    }
  },
  DEPENDENCY_MANAGEMENT_ORDER {
    @Override
    public AbstractPedanticEnforcer createEnforcerRule() {
      return new PedanticDependencyManagementOrderEnforcer();
    }
  },
  DEPENDENCY_ORDER {
    @Override
    public AbstractPedanticEnforcer createEnforcerRule() {
      return new PedanticDependencyOrderEnforcer();
    }
  },
  PLUGIN_MANAGEMENT_ORDER {
    @Override
    public AbstractPedanticEnforcer createEnforcerRule() {
      return new PedanticPluginManagementOrderEnforcer();
    }
  };

  public abstract AbstractPedanticEnforcer createEnforcerRule();
}
