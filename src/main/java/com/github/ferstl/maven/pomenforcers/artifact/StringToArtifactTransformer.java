package com.github.ferstl.maven.pomenforcers.artifact;

import java.util.ArrayList;

import com.github.ferstl.maven.pomenforcers.model.ArtifactModel;
import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

public class StringToArtifactTransformer implements Function<String, ArtifactModel> {

  private static final Splitter COLON_SPLITTER = Splitter.on(":");

 @Override
 public ArtifactModel apply(String input) {
   ArrayList<String> artifactElements = Lists.newArrayList(COLON_SPLITTER.split(input));

   if(artifactElements.size() != 2) {
     throw new IllegalArgumentException("Cannot read POM information: " + input);
   }

   return new ArtifactModel(artifactElements.get(0), artifactElements.get(1), null);
  }
}