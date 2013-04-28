package com.github.ferstl.maven.pomenforcers.model.functions;

import java.util.Objects;

import org.apache.maven.enforcer.rule.api.EnforcerRuleHelper;
import org.apache.maven.model.Plugin;

import com.github.ferstl.maven.pomenforcers.model.PluginModel;

import static com.github.ferstl.maven.pomenforcers.util.EnforcerRuleUtils.evaluateProperties;


public class PluginMatcher extends AbstractOneToOneMatcher<Plugin, PluginModel> {

  public PluginMatcher(EnforcerRuleHelper helper) {
    super(helper);
  }

  @Override
  protected PluginModel transform(Plugin mavenPlugin) {
    return new PluginModel(mavenPlugin.getGroupId(), mavenPlugin.getArtifactId(), mavenPlugin.getVersion());
  }

  @Override
  protected boolean matches(PluginModel supersetItem, PluginModel subsetItem) {
    String groupId = evaluateProperties(subsetItem.getGroupId(), getHelper());
    String artifactId = evaluateProperties(subsetItem.getArtifactId(), getHelper());

    return Objects.equals(supersetItem.getGroupId(), groupId)
        && Objects.equals(supersetItem.getArtifactId(), artifactId);
  }

}
