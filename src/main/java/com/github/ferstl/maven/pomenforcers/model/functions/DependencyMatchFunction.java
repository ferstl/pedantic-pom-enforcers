/*
 * Copyright (c) 2013 by Stefan Ferstl <st.ferstl@gmail.com>
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

import java.util.Collection;
import java.util.Map.Entry;

import org.apache.maven.enforcer.rule.api.EnforcerRuleHelper;
import org.apache.maven.model.Dependency;

import com.github.ferstl.maven.pomenforcers.model.DependencyModel;
import com.google.common.base.Function;
import com.google.common.collect.Maps;

import static com.github.ferstl.maven.pomenforcers.util.EnforcerRuleUtils.evaluateProperties;
import static com.google.common.base.Objects.equal;

public class DependencyMatchFunction implements Function<DependencyModel, Entry<DependencyModel, DependencyModel>> {

  private final Collection<Dependency> superset;
  private final EnforcerRuleHelper helper;

  public DependencyMatchFunction(Collection<Dependency> superset, EnforcerRuleHelper helper) {
    this.superset = superset;
    this.helper = helper;
  }

  @Override
  public Entry<DependencyModel, DependencyModel> apply(DependencyModel dependency) {
    String groupId = evaluateProperties(dependency.getGroupId(), this.helper);
    String artifactId = evaluateProperties(dependency.getArtifactId(), this.helper);
    String classifier = evaluateProperties(dependency.getClassifier(), this.helper);
    String type = evaluateProperties(dependency.getType(), this.helper);

    for (Dependency supersetDependency : this.superset) {
      DependencyModel supersetDependencyModel = createDependencyModel(supersetDependency);

      if (equal(supersetDependency.getGroupId(), groupId)
       && equal(supersetDependency.getArtifactId(), artifactId)
       && equal(supersetDependency.getClassifier(), classifier)
       && equal(supersetDependency.getType(), type)) {

        DependencyModel dependencyModel = new DependencyModel(
            supersetDependency.getGroupId(),
            supersetDependency.getArtifactId(),
            supersetDependency.getVersion(),
            supersetDependency.getScope(),
            supersetDependency.getClassifier(),
            supersetDependency.getType());
        return Maps.immutableEntry(supersetDependencyModel, dependencyModel);
      }
    }

    throw new IllegalStateException(
        "Could not match dependency '" + dependency + "' with superset '." + this.superset + "'.");
  }

  private static DependencyModel createDependencyModel(Dependency dependency) {
    return new DependencyModel(
        dependency.getGroupId(),
        dependency.getArtifactId(),
        dependency.getVersion(),
        dependency.getScope(),
        dependency.getClassifier(),
        dependency.getType());
  }

}