package com.github.ferstl.maven.pomenforcers.model.functions;

import java.util.Collection;
import java.util.Map.Entry;

import com.google.common.base.Function;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableBiMap.Builder;

import static com.google.common.collect.Collections2.transform;


public class OneToOneMatcher<T> {

  private final Function<T, Entry<T, T>> matchFunction;

  public OneToOneMatcher(Function<T, Entry<T, T>> matchFunction) {
    this.matchFunction = matchFunction;
  }

  public BiMap<T, T> match(Collection<T> subset) {

    Collection<Entry<T, T>> matchedEntries =  transform(subset, this.matchFunction);
    Builder<T, T> bimapBuilder = ImmutableBiMap.<T, T>builder();
    for (Entry<T, T> entry : matchedEntries) {
      bimapBuilder.put(entry.getKey(), entry.getValue());
    }

    return bimapBuilder.build();
  }
}
