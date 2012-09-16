package com.github.ferstl.maven.pomenforcers.model.functions;

import com.github.ferstl.maven.pomenforcers.model.PluginModel;
import com.google.common.base.Function;

public enum PluginElementExtractor implements Function<PluginModel, String> {
  GROUP_ID_EXTRACTOR {
    @Override
    public String apply(PluginModel input) {
      return input.getGroupId();
    }
  },
  ARTIFACT_ID_EXTRACTOR {
    @Override
    public String apply(PluginModel input) {
      return input.getArtifactId();
    }
  };
}