package com.github.ferstl.maven.pomenforcers.model.functions;

import java.util.Collection;

import org.apache.maven.enforcer.rule.api.EnforcerRuleHelper;

import com.github.ferstl.maven.pomenforcers.model.PluginModel;
import com.google.common.base.Function;

import static com.github.ferstl.maven.pomenforcers.util.EnforcerRuleUtils.evaluateProperties;
import static com.google.common.base.Objects.equal;

public class PluginMatchFunction implements Function<PluginModel, PluginModel> {

  private final Collection<PluginModel> superset;
  private final EnforcerRuleHelper helper;

  public PluginMatchFunction(Collection<PluginModel> superset, EnforcerRuleHelper helper) {
    this.superset = superset;
    this.helper = helper;
  }

  @Override
  public PluginModel apply(PluginModel plugin) {
    String groupId = evaluateProperties(plugin.getGroupId(), this.helper);
    String artifactId = evaluateProperties(plugin.getArtifactId(), this.helper);
    for (PluginModel supersetPlugin : this.superset) {
      if (equal(supersetPlugin.getGroupId(), groupId)
       && equal(supersetPlugin.getArtifactId(), artifactId)) {
        return new PluginModel(
            supersetPlugin.getGroupId(),
            supersetPlugin.getArtifactId(),
            supersetPlugin.getVersion());
      }
    }
    throw new IllegalStateException(
        "Could not match plugin '" + plugin + "' with superset '." + this.superset + "'.");
  }
}