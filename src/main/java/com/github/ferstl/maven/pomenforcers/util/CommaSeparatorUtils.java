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
package com.github.ferstl.maven.pomenforcers.util;

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
    splitAndAddToCollection(commaSeparatedItems, collection, Functions.identity());
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

  private CommaSeparatorUtils() {
  }
}
