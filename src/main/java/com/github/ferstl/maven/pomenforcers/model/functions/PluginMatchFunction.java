package com.github.ferstl.maven.pomenforcers.model.functions;

import java.util.Collection;
import java.util.Map.Entry;

import org.apache.maven.enforcer.rule.api.EnforcerRuleHelper;
import org.apache.maven.model.Plugin;

import com.github.ferstl.maven.pomenforcers.model.PluginModel;
import com.google.common.base.Function;
import com.google.common.collect.Maps;

import static com.github.ferstl.maven.pomenforcers.util.EnforcerRuleUtils.evaluateProperties;
import static com.google.common.base.Objects.equal;

public class PluginMatchFunction implements Function<PluginModel, Entry<PluginModel, PluginModel>> {

  private final Collection<Plugin> superset;
  private final EnforcerRuleHelper helper;

  public PluginMatchFunction(Collection<Plugin> superset, EnforcerRuleHelper helper) {
    this.superset = superset;
    this.helper = helper;
  }

  @Override
  public Entry<PluginModel, PluginModel> apply(PluginModel plugin) {
    String groupId = evaluateProperties(plugin.getGroupId(), this.helper);
    String artifactId = evaluateProperties(plugin.getArtifactId(), this.helper);

    for (Plugin supersetPlugin : this.superset) {
      PluginModel supersetPluginModel = createPluginModel(supersetPlugin);

      if (equal(supersetPlugin.getGroupId(), groupId)
       && equal(supersetPlugin.getArtifactId(), artifactId)) {
        return Maps.immutableEntry(supersetPluginModel, plugin);
      }
    }

    throw new IllegalStateException(
        "Could not match plugin '" + plugin + "' with superset '." + this.superset + "'.");
  }

  private static PluginModel createPluginModel(Plugin plugin) {
    return new PluginModel(plugin.getGroupId(), plugin.getArtifactId(), plugin.getVersion());
  }
}