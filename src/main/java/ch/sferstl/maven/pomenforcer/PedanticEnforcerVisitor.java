package ch.sferstl.maven.pomenforcer;

public interface PedanticEnforcerVisitor {

  void visit(PedanticPomSectionOrderEnforcer sectionOrderEnforcer);
  void visit(PedanticModuleOrderEnforcer moduleOrderEnforcer);
  void visit(PedanticDependencyManagementOrderEnforcer dependencyManagementOrderEnforcer);
  void visit(PedanticDependencyOrderEnforcer dependencyOrderEnforcer);
  void visit(PedanticPluginManagementOrderEnforcer pluginManagementOrderEnforcer);
  void visit(CompoundPedanticEnforcer compoundEnforcer);
}
