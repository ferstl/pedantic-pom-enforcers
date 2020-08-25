/*
 * Copyright (c) 2012 - 2020 the original author or authors.
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
import java.util.function.Function;
import java.util.stream.StreamSupport;
import com.google.common.base.Splitter;
import static java.util.stream.Collectors.toCollection;

public final class CommaSeparatorUtils {

  private static final Splitter COMMA_SPLITTER = Splitter.on(",");

  public static void splitAndAddToCollection(String commaSeparatedItems, Collection<String> collection) {
    splitAndAddToCollection(commaSeparatedItems, collection, Function.identity());
  }

  public static <T> void splitAndAddToCollection(String commaSeparatedItems, Collection<T> collection, Function<String, T> transformer) {
    Iterable<String> items = COMMA_SPLITTER.split(commaSeparatedItems);
    // Don't touch the collection if there is nothing to add.
    if (items.iterator().hasNext()) {
      collection.clear();
    }
    StreamSupport.stream(items.spliterator(), false)
        .map(transformer)
        .collect(toCollection(() -> collection));
  }

}
