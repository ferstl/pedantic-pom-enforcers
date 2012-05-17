package ch.sferstl.maven.pomenforcer;

import java.util.Collection;
import java.util.Comparator;


public interface PriorityComparatorFactory<P extends Comparable<P>, T> {
  Comparator<T> createPriorityComparator(Collection<P> priorityList);
}
