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
