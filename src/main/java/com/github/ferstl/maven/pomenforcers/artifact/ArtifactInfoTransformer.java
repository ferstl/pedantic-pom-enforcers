package com.github.ferstl.maven.pomenforcers.artifact;

import java.util.ArrayList;

import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

public class ArtifactInfoTransformer implements Function<String, ArtifactInfo> {

  private static final Splitter COLON_SPLITTER = Splitter.on(":");

 @Override
 public ArtifactInfo apply(String input) {
   ArrayList<String> artifactElements = Lists.newArrayList(COLON_SPLITTER.split(input));

   if(artifactElements.size() != 2) {
     throw new IllegalArgumentException("Cannot read POM information: " + input);
   }

   return new ArtifactInfo(artifactElements.get(0), artifactElements.get(1));
  }
}