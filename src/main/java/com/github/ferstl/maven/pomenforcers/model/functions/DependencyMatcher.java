package com.github.ferstl.maven.pomenforcers.model.functions;

import java.util.Objects;

import org.apache.maven.enforcer.rule.api.EnforcerRuleHelper;
import org.apache.maven.model.Dependency;

import com.github.ferstl.maven.pomenforcers.model.DependencyModel;

import static com.github.ferstl.maven.pomenforcers.util.EnforcerRuleUtils.evaluateProperties;


public class DependencyMatcher extends AbstractOneToOneMatcher<Dependency, DependencyModel> {

  public DependencyMatcher(EnforcerRuleHelper helper) {
    super(helper);
  }

  @Override
  protected DependencyModel transform(Dependency mavenDependency) {
    return new DependencyModel(
        mavenDependency.getGroupId(),
        mavenDependency.getArtifactId(),
        mavenDependency.getVersion(),
        mavenDependency.getScope(),
        mavenDependency.getClassifier(),
        mavenDependency.getType());
  }

  @Override
  protected boolean matches(DependencyModel supersetItem, DependencyModel subsetItem) {
    String groupId = evaluateProperties(subsetItem.getGroupId(), getHelper());
    String artifactId = evaluateProperties(subsetItem.getArtifactId(), getHelper());
    String classifier = evaluateProperties(subsetItem.getClassifier(), getHelper());
    String type = evaluateProperties(subsetItem.getType(), getHelper());

    return Objects.equals(supersetItem.getGroupId(), groupId)
        && Objects.equals(supersetItem.getArtifactId(), artifactId)
        && Objects.equals(supersetItem.getClassifier(), classifier)
        && Objects.equals(supersetItem.getType(), type);
  }

}
