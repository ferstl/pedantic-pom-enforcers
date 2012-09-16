/*
 * Copyright (c) 2012 by The Author(s)
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
