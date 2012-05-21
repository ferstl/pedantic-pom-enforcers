package ch.sferstl.maven.pomenforcer.priority;

import java.util.Collection;
import java.util.Comparator;


public interface PriorityComparatorFactory<P extends Comparable<P>, T> {
  Comparator<T> createPriorityComparator(Collection<P> priorityCollection);
}
