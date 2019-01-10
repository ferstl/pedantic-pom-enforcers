/*
 * Copyright (c) 2012 - 2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.ferstl.maven.pomenforcers.model.functions;

import static com.github.ferstl.maven.pomenforcers.util.EnforcerRuleUtils.evaluateProperties;

import java.util.Objects;

import org.apache.maven.enforcer.rule.api.EnforcerRuleHelper;
import org.apache.maven.model.Dependency;

import com.github.ferstl.maven.pomenforcers.model.DependencyModel;
import com.github.ferstl.maven.pomenforcers.model.DependencyScope;
import com.google.common.collect.ImmutableBiMap.Builder;

/**
 * Matches Maven {@link Dependency} objects with {@link DependencyModel} objects.
 */
public class DependencyMatcher extends AbstractOneToOneMatcher<Dependency, DependencyModel> {

  public DependencyMatcher(EnforcerRuleHelper helper) {
    super(helper);
  }

  @Override
  protected DependencyModel transform(Dependency mavenDependency) {
    return new DependencyModel(
        mavenDependency.getGroupId(),
        mavenDependency.getArtifactId(),
        mavenDependency.getVersion(),
        mavenDependency.getScope(),
        mavenDependency.getClassifier(),
        mavenDependency.getType());
  }

  @Override
  protected boolean matches(DependencyModel supersetItem, DependencyModel subsetItem) {
    String groupId = evaluateProperties(subsetItem.getGroupId(), getHelper());
    String artifactId = evaluateProperties(subsetItem.getArtifactId(), getHelper());
    String classifier = evaluateProperties(subsetItem.getClassifier(), getHelper());
    String type = evaluateProperties(subsetItem.getType(), getHelper());

    return Objects.equals(supersetItem.getGroupId(), groupId)
        && Objects.equals(supersetItem.getArtifactId(), artifactId)
        && Objects.equals(supersetItem.getClassifier(), classifier)
        && Objects.equals(supersetItem.getType(), type);
  }

  @Override
  protected void handleUnmatchedItem(
      Builder<DependencyModel, DependencyModel> mapBuilder,
      DependencyModel subsetItem) {
    String type = evaluateProperties(subsetItem.getType(), getHelper());
    if ("pom".equals(type) && DependencyScope.IMPORT.equals(subsetItem.getScope())) {
      mapBuilder.put(subsetItem, subsetItem);
    } else {
      super.handleUnmatchedItem(mapBuilder, subsetItem);
    }
  }
}