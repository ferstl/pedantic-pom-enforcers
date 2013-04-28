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

  public AbstractOneToOneMatcher(EnforcerRuleHelper helper) {
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
        throw new IllegalArgumentException("Could not match item " + subsetItem + " with superset");
      }
    }

    return mapBuilder.build();
  }

  protected abstract V transform(U supersetItem);

  protected abstract boolean matches(V supersetItem, V subsetItem);

  protected EnforcerRuleHelper getHelper() {
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
