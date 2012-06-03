/*
 * Copyright (c) 2012 by The Authors of the Pedantic POM Enforcers
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
package ch.sferstl.maven.pomenforcer.artifact;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;

import ch.sferstl.maven.pomenforcer.priority.PriorityComparatorFactory;

public class ArtifactSorter<T, F extends PriorityComparatorFactory<String, T>> {

  private final Set<F> orderBy;
  private final Multimap<F, String> priorityMap;

  public ArtifactSorter() {
    this.orderBy = Sets.newLinkedHashSet();
    this.priorityMap = LinkedHashMultimap.create();
  }

  public void orderBy(Collection<F> artifactElements) {
    this.orderBy.clear();
    this.orderBy.addAll(artifactElements);
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

  public Ordering<T> createOrdering() {
    List<Comparator<T>> comparators = Lists.newArrayListWithCapacity(this.orderBy.size());
    for (F artifactElement : this.orderBy) {
      Comparator<T> comparator =
          artifactElement.createPriorityComparator(this.priorityMap.get(artifactElement));
      comparators.add(comparator);
    }

    Ordering<T> ordering;
    if (comparators.size() > 0) {
      ordering = Ordering.from(comparators.get(0));
      for (Comparator<T> comparator : comparators.subList(1, comparators.size())) {
        ordering = ordering.compound(comparator);
      }
    } else {
      throw new IllegalStateException("Undefined artifact order.");
    }
    return ordering;
  }
}
