package ch.sferstl.maven.pomenforcer.artifact;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.apache.maven.artifact.Artifact;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;

public class ArtifactOrdering {

  private final Set<ArtifactElement> orderBy;
  private final Multimap<ArtifactElement, String> priorityMap;

  public ArtifactOrdering() {
    this.orderBy = Sets.newLinkedHashSet();
    this.priorityMap = LinkedHashMultimap.create();
  }

  public void orderBy(Collection<ArtifactElement> artifactElements) {
    this.orderBy.clear();
    this.orderBy.addAll(artifactElements);
  }

  public void setPriorities(ArtifactElement artifactElement, Iterable<String> priorities) {
    this.priorityMap.putAll(artifactElement, priorities);
  }

  public Collection<ArtifactElement> getOrderBy() {
    return Collections.unmodifiableCollection(this.orderBy);
  }

  public Collection<String> getPriorities(ArtifactElement artifactElement) {
    return this.priorityMap.get(artifactElement);
  }

  public Ordering<Artifact> createArtifactOrdering() {
    List<Comparator<Artifact>> comparators = Lists.newArrayListWithCapacity(this.orderBy.size());
    for (ArtifactElement artifactElement : this.orderBy) {
      Comparator<Artifact> comparator =
          artifactElement.createPriorityComparator(this.priorityMap.get(artifactElement));
      comparators.add(comparator);
    }

    Ordering<Artifact> ordering;
    if (comparators.size() > 0) {
      ordering = Ordering.from(comparators.get(0));
      for (Comparator<Artifact> comparator : comparators.subList(1, comparators.size())) {
        ordering = ordering.compound(comparator);
      }
    } else {
      throw new IllegalStateException("Undefined artifact order.");
    }
    return ordering;
  }
}
