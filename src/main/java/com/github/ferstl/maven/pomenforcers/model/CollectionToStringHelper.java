package com.github.ferstl.maven.pomenforcers.model;

import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;

final class CollectionToStringHelper {

  private static final Joiner JOINER = Joiner.on(",\n");

  public static String toString(String prefix, Iterable<?> iterable) {
    StringBuilder sb = new StringBuilder(prefix).append(" [\n");
    JOINER.appendTo(sb, iterable).append((iterable != null && !Iterables.isEmpty(iterable)) ? "\n]" : "]");
    return sb.toString();
  }

  private CollectionToStringHelper() {}
}
