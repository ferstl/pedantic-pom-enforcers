package ch.sferstl.maven.pomenforcer.util;

import java.util.Collection;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;

public final class CommaSeparatorUtils {

  private static final Splitter COMMA_SPLITTER = Splitter.on(",");
  private static final Joiner COMMA_JOINER = Joiner.on(",");

  public static void splitAndAddToCollection(
      String commaSeparatedItems, Collection<String> collection) {
    splitAndAddToCollection(commaSeparatedItems, collection, Functions.<String>identity());
  }

  public static <T> void splitAndAddToCollection(
      String commaSeparatedItems, Collection<T> collection, Function<String, T> transformer) {
    Iterable<String> items = COMMA_SPLITTER.split(commaSeparatedItems);
    // Don't touch the collection if there is nothing to add.
    if (items.iterator().hasNext()) {
      collection.clear();
    }
    Iterables.addAll(collection, Iterables.transform(items, transformer));
  }

  public static String join(Iterable<?> parts) {
    return COMMA_JOINER.join(parts);
  }

  public static <T> String join(Iterable<T> parts, Function<T, String> transformer) {
    Iterable<String> convertedParts = Iterables.transform(parts, transformer);
    return COMMA_JOINER.join(convertedParts);
  }

  private CommaSeparatorUtils() {}
}
