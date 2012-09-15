package com.github.ferstl.maven.pomenforcers.functions;

import com.github.ferstl.maven.pomenforcers.model.PluginModel;
import com.google.common.base.Function;

enum PluginElementExtractor implements Function<PluginModel, String> {
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