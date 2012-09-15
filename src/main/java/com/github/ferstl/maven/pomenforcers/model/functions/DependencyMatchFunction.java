package com.github.ferstl.maven.pomenforcers.model.functions;

import java.util.Collection;

import org.apache.maven.enforcer.rule.api.EnforcerRuleHelper;

import com.github.ferstl.maven.pomenforcers.model.DependencyModel;
import com.google.common.base.Function;

import static com.github.ferstl.maven.pomenforcers.util.EnforcerRuleUtils.evaluateProperties;
import static com.google.common.base.Objects.equal;

public class DependencyMatchFunction implements Function<DependencyModel, DependencyModel> {

  private final Collection<DependencyModel> superset;
  private final EnforcerRuleHelper helper;

  public DependencyMatchFunction(Collection<DependencyModel> superset, EnforcerRuleHelper helper) {
    this.superset = superset;
    this.helper = helper;
  }

  @Override
  public DependencyModel apply(DependencyModel dependency) {
    String groupId = evaluateProperties(dependency.getGroupId(), this.helper);
    String artifactId = evaluateProperties(dependency.getArtifactId(), this.helper);
    String classifier = evaluateProperties(dependency.getClassifier(), this.helper);
    for (DependencyModel supersetDependency : this.superset) {
      if (equal(supersetDependency.getGroupId(), groupId)
       && equal(supersetDependency.getArtifactId(), artifactId)
       && equal(supersetDependency.getClassifier(), classifier)) {
        return new DependencyModel(
            supersetDependency.getGroupId(),
            supersetDependency.getArtifactId(),
            supersetDependency.getVersion(),
            supersetDependency.getScope().getScopeName(),
            supersetDependency.getClassifier());
      }
    }
    throw new IllegalStateException(
        "Could not match dependency '" + dependency + "' with superset '." + this.superset + "'.");
  }

}