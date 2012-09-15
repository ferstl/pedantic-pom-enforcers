package com.github.ferstl.maven.pomenforcers.functions;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Plugin;

import com.github.ferstl.maven.pomenforcers.model.ArtifactModel;
import com.github.ferstl.maven.pomenforcers.model.DependencyModel;
import com.github.ferstl.maven.pomenforcers.model.PluginModel;
import com.google.common.base.Function;

public final class Transformers {

  private static final StringToArtifactTransformer STRING_TO_ARTIFACT = new StringToArtifactTransformer();
  private static final Function<Plugin, PluginModel> PLUGIN_TO_PLUGIN_MODEL =
      new PluginToPluginModelTransformer();
  private static final Function<Dependency, DependencyModel> DEPENDENCY_TO_DEPENDENCY_MODEL =
      new DependencyToDependencyModelTransformer();


  public static Function<String, ArtifactModel> stringToArtifactModel() {
    return STRING_TO_ARTIFACT;
  }

  public static Function<Plugin, PluginModel> pluginToPluginModel() {
    return PLUGIN_TO_PLUGIN_MODEL;
  }

  public static Function<Dependency, DependencyModel> dependencyToDependencyModel() {
    return DEPENDENCY_TO_DEPENDENCY_MODEL;
  }

  private Transformers() {}

  private static class PluginToPluginModelTransformer implements Function<Plugin, PluginModel> {
    @Override
    public PluginModel apply(Plugin input) {
      return new PluginModel(input.getGroupId(), input.getArtifactId(), input.getVersion());
    }
  }

  private static class DependencyToDependencyModelTransformer implements Function<Dependency, DependencyModel> {
    @Override
    public DependencyModel apply(Dependency input) {
      return new DependencyModel(
          input.getGroupId(), input.getArtifactId(), input.getVersion(), input.getScope(), input.getClassifier());
    }
  }
}
