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
package com.github.ferstl.maven.pomenforcers.priority;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.function.Function;
import com.google.common.base.Equivalence;
import com.google.common.collect.Ordering;

/**
 * Comparator that makes comparisons based on a priority collection. Objects that match an item in
 * the priority collection will be considered smaller than objects that don't match any item in the
 * priority collection. If both compared objects match different items in the priority collection,
 * the object that matches the item closer to the <quote>beginning</quote> of the collection (as
 * returned by the collection's iterator) will be considered smaller. Thus, it is recommended to use
 * {@link List}s or {@link LinkedHashSet}s to define the priority collection.
 *
 * @param <P> Type of the priority collection.
 * @param <T> Type of the values to be compared.
 */
public class PriorityOrdering<P extends Comparable<? super P>, T> extends Ordering<T> {

  /**
   * The priority collection.
   */
  private final Collection<P> priorityCollection;

  /**
   * Matches the values to be compared with the items in the priority collection.
   */
  private final Equivalence<? super P> priorityMatcher;

  /**
   * Transforms the type of the objects to be compared into the type of the priority collection. Use
   * {@link Function#identity()} if the type of the priority collection and the type of the objects to be
   * compared are the same.
   */
  private final Function<T, P> transformer;


  public PriorityOrdering(Collection<P> prioritizedItems, Function<T, P> transformer, Equivalence<? super P> priorityMatcher) {
    this.priorityCollection = prioritizedItems;
    this.priorityMatcher = priorityMatcher;
    this.transformer = transformer;
  }

  public PriorityOrdering(Collection<P> priorityCollection, Function<T, P> transformer) {
    this(priorityCollection, transformer, Equivalence.equals());
  }

  @Override
  public int compare(T object1, T object2) {
    P comparable1 = this.transformer.apply(object1);
    P comparable2 = this.transformer.apply(object2);

    int rank1 = this.rank(comparable1);
    int rank2 = this.rank(comparable2);

    if (rank1 == rank2) {
      return comparable1.compareTo(comparable2);
    }

    return rank1 - rank2;

  }

  /**
   * Determine the priority of the given item by matching it against the priority collection.
   * The lower the rank, the higher the priority.
   *
   * @param item The item to prioritize.
   * @return The priority of the given item or {@link Integer#MAX_VALUE} if the given item does not
   * match any element of the priority collection.
   */
  private int rank(P item) {
    int i = 0;
    for (P prioritizedItem : this.priorityCollection) {
      if (this.priorityMatcher.equivalent(item, prioritizedItem)) {
        return i;
      }
      i++;
    }

    return Integer.MAX_VALUE;
  }

}
