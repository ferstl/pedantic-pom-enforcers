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
package com.github.ferstl.maven.pomenforcers.artifact;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import com.github.ferstl.maven.pomenforcers.priority.PriorityComparatorFactory;
import com.google.common.collect.Iterables;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;


public class ArtifactOrdering<T, F extends PriorityComparatorFactory<String, T>> extends Ordering<T> {

  private final Set<F> orderBy;
  private final Multimap<F, String> priorityMap;

  public static <T, F extends PriorityComparatorFactory<String, T>>
  ArtifactOrdering<T, F> orderBy(Iterable<F> artifactElements) {
    if (Iterables.isEmpty(artifactElements)) {
      throw new IllegalArgumentException("No order specified.");
    }
    return new ArtifactOrdering<>(artifactElements);
  }

  @SafeVarargs
  public static <T, F extends PriorityComparatorFactory<String, T>>
  ArtifactOrdering<T, F> orderBy(F... artifactElements) {
    return orderBy(Arrays.asList(artifactElements));
  }

  private ArtifactOrdering(Iterable<F> artifactElements) {
    this.orderBy = Sets.newLinkedHashSet(artifactElements);
    this.priorityMap = LinkedHashMultimap.create();
  }

  public void redefineOrderBy(Iterable<F> artifactElements) {
    this.orderBy.clear();
    this.orderBy.addAll(Lists.newArrayList(artifactElements));
  }

  public void setPriorities(F artifactElement, Iterable<String> priorities) {
    this.priorityMap.putAll(artifactElement, priorities);
  }

  public Collection<F> getOrderBy() {
    return Collections.unmodifiableCollection(this.orderBy);
  }

  public Collection<String> getPriorities(F artifactElement) {
    return this.priorityMap.get(artifactElement);
  }

  @Override
  public int compare(T left, T right) {
    return createOrdering().compare(left, right);
  }

  private Ordering<T> createOrdering() {
    List<Comparator<T>> comparators = new ArrayList<>(this.orderBy.size());
    for (F artifactElement : this.orderBy) {
      Comparator<T> comparator =
          artifactElement.createPriorityComparator(this.priorityMap.get(artifactElement));
      comparators.add(comparator);
    }

    return Ordering.compound(comparators);
  }
}
