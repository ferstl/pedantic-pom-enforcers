/*
 * Copyright (c) 2012 - 2025 the original author or authors.
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
package com.github.ferstl.maven.pomenforcers.priority;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import com.google.common.collect.Iterables;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;

/**
 * @param <T> Type of this ordering.
 * @param <P> Type of the priorities.
 * @param <F> Type of the {@link PriorityOrderingFactory}.
 */
public class CompoundPriorityOrdering<T, P extends Comparable<P>, F extends PriorityOrderingFactory<P, T>> extends Ordering<T> {

  private final Set<F> orderBy;
  private final Multimap<F, P> priorityMap;

  public static <T, P extends Comparable<P>, F extends PriorityOrderingFactory<P, T>> CompoundPriorityOrdering<T, P, F> orderBy(Iterable<F> artifactElements) {
    if (Iterables.isEmpty(artifactElements)) {
      throw new IllegalArgumentException("No order specified.");
    }
    return new CompoundPriorityOrdering<>(artifactElements);
  }

  @SafeVarargs
  public static <T, P extends Comparable<P>, F extends PriorityOrderingFactory<P, T>>
  CompoundPriorityOrdering<T, P, F> orderBy(F... artifactElements) {
    return orderBy(Arrays.asList(artifactElements));
  }

  private CompoundPriorityOrdering(Iterable<F> artifactElements) {
    this.orderBy = Sets.newLinkedHashSet(artifactElements);
    this.priorityMap = LinkedHashMultimap.create();
  }

  public void redefineOrderBy(Iterable<F> artifactElements) {
    this.orderBy.clear();
    this.orderBy.addAll(Lists.newArrayList(artifactElements));
  }

  public void setPriorities(F artifactElement, Iterable<P> priorities) {
    this.priorityMap.removeAll(artifactElement);
    this.priorityMap.putAll(artifactElement, priorities);
  }

  @Override
  public int compare(T left, T right) {
    return createOrdering().compare(left, right);
  }

  private Ordering<T> createOrdering() {
    List<Comparator<T>> comparators = new ArrayList<>(this.orderBy.size());
    for (F artifactElement : this.orderBy) {
      Comparator<T> comparator =
          artifactElement.createPriorityOrdering(this.priorityMap.get(artifactElement));
      comparators.add(comparator);
    }

    return Ordering.compound(comparators);
  }
}
