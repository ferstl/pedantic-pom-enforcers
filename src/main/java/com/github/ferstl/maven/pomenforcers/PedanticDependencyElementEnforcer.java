package com.github.ferstl.maven.pomenforcers;


import static com.github.ferstl.maven.pomenforcers.PedanticEnforcerRule.DEPENDENCY_ELEMENT;

public class PedanticDependencyElementEnforcer extends AbstractPedanticEnforcer {

  @Override
  protected PedanticEnforcerRule getDescription() {
    return DEPENDENCY_ELEMENT;
  }

  @Override
  protected void doEnforce(ErrorReport report) {

  }

  @Override
  protected void accept(PedanticEnforcerVisitor visitor) {

  }
}
