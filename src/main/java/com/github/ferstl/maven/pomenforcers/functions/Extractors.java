package com.github.ferstl.maven.pomenforcers.functions;

import com.github.ferstl.maven.pomenforcers.model.DependencyModel;
import com.github.ferstl.maven.pomenforcers.model.PluginModel;
import com.google.common.base.Function;

public final class Extractors {
  public static Function<DependencyModel, String> dependencyGroupId() {
    return DependencyElementExtractor.GROUP_ID_EXTRACTOR;
  }

  public static Function<DependencyModel, String> dependencyArtifactId() {
    return DependencyElementExtractor.ARTIFACT_ID_EXTRACTOR;
  }

  public static Function<DependencyModel, String> dependencyScope() {
    return DependencyElementExtractor.SCOPE_EXTRACTOR;
  }

  public static Function<PluginModel, String> pluginGroupId() {
    return PluginElementExtractor.GROUP_ID_EXTRACTOR;
  }

  public static Function<PluginModel, String> pluginArtifactId() {
    return PluginElementExtractor.ARTIFACT_ID_EXTRACTOR;
  }

}
