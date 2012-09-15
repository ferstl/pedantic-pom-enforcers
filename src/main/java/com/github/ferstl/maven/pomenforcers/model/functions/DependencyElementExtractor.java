package com.github.ferstl.maven.pomenforcers.model.functions;

import com.github.ferstl.maven.pomenforcers.model.DependencyModel;
import com.google.common.base.Function;

enum DependencyElementExtractor implements Function<DependencyModel, String> {

  GROUP_ID_EXTRACTOR {
    @Override
    public String apply(DependencyModel input) {
      return input.getGroupId();
    }
  },
  ARTIFACT_ID_EXTRACTOR {
    @Override
    public String apply(DependencyModel input) {
      return input.getArtifactId();
    }
  },
  SCOPE_EXTRACTOR {
    @Override
    public String apply(DependencyModel input) {
      return input.getScope().getScopeName();
    }
  };
}
