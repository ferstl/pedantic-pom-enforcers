/*
 * Copyright (c) 2012 - 2020 the original author or authors.
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.maven.enforcer.rule.api.EnforcerRuleHelper;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableBiMap.Builder;


public abstract class AbstractOneToOneMatcher<U, V> {

  private final EnforcerRuleHelper helper;

  AbstractOneToOneMatcher(EnforcerRuleHelper helper) {
    this.helper = helper;
  }

  public final BiMap<V, V> match(Collection<U> superset, Collection<V> subset) {
    Builder<V, V> mapBuilder = ImmutableBiMap.builder();

    // Transform the superset here in order not to do it in each nested loop
    Collection<V> transformedSuperset = transformSuperset(superset);

    for (V subsetItem : subset) {
      boolean itemMatched = false;

      for (V supersetItem : transformedSuperset) {
        if (matches(supersetItem, subsetItem)) {
          itemMatched = true;
          mapBuilder.put(supersetItem, subsetItem);
          break;
        }
      }

      if (!itemMatched) {
        handleUnmatchedItem(mapBuilder, subsetItem);
      }
    }

    return mapBuilder.build();
  }

  protected void handleUnmatchedItem(Builder<V,V> mapBuilder, V subsetItem) {
    throw new IllegalArgumentException("Could not match item " + subsetItem + " with superset");
  }

  protected abstract V transform(U supersetItem);

  protected abstract boolean matches(V supersetItem, V subsetItem);

  EnforcerRuleHelper getHelper() {
    return this.helper;
  }

  private Collection<V> transformSuperset(Collection<U> superset) {
    List<V> transformed = new ArrayList<>(superset.size());
    for (U supersetItem : superset) {
      transformed.add(transform(supersetItem));
    }
    return transformed;
  }
}
