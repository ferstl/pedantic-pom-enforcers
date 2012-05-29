package ch.sferstl.maven.pomenforcer.artifact;

import java.util.Collection;

import org.apache.maven.enforcer.rule.api.EnforcerRuleHelper;
import org.apache.maven.model.Dependency;

import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.collect.Collections2;

import ch.sferstl.maven.pomenforcer.util.EnforcerRuleUtils;

public class DependencyMatcher {

  private final MatchFunction matchFunction;

  public DependencyMatcher(Collection<Dependency> superset, EnforcerRuleHelper helper) {
    this.matchFunction = new MatchFunction(superset, helper);
  }

  public Collection<Dependency> match(Collection<Dependency> subset) {
    return Collections2.transform(subset, this.matchFunction);
  }

  private static class MatchFunction implements Function<Dependency, Dependency> {

    private final Collection<Dependency> superset;
    private final EnforcerRuleHelper helper;

    public MatchFunction(Collection<Dependency> superset, EnforcerRuleHelper helper) {
      this.superset = superset;
      this.helper = helper;
    }

    @Override
    public Dependency apply(Dependency dependency) {
      for (Dependency supersetDependency : this.superset) {
        String groupId = EnforcerRuleUtils.resolveStringProperty(dependency.getGroupId(), this.helper);
        String artifactId = EnforcerRuleUtils.resolveStringProperty(dependency.getArtifactId(), this.helper);
        if (supersetDependency.getGroupId().equals(groupId)
         && supersetDependency.getArtifactId().equals(artifactId)) {
          Dependency matchedDependency = supersetDependency.clone();
          matchedDependency.setScope(Objects.firstNonNull(supersetDependency.getScope(), "compile"));
          return matchedDependency;
        }
      }
      throw new IllegalStateException(
          "Could not match dependency '" + dependency + "' with superset '." + this.superset + "'.");
    }

  }
}
