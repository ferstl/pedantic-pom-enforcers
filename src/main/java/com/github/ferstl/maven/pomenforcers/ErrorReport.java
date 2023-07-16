/*
 * Copyright (c) 2012 - 2023 the original author or authors.
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
package com.github.ferstl.maven.pomenforcers;

import java.util.Collection;
import java.util.LinkedList;
import java.util.function.Function;
import java.util.stream.Collectors;
import com.github.ferstl.maven.pomenforcers.util.SideBySideDiffUtil;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.Collections2;
import static com.google.common.base.Functions.toStringFunction;


public class ErrorReport {

  private static final String LIST_ITEM = "- ";
  private static final String LINE_SEPARATOR = System.getProperty("line.separator", "\n");
  private static final Joiner LINE_JOINER = Joiner.on(LINE_SEPARATOR);
  private static final Joiner LIST_JOINER = Joiner.on(LINE_SEPARATOR + LIST_ITEM);

  private final String title;
  private final Collection<Object> lines;

  private boolean useLargeTitle;

  public static <T> String toList(Collection<T> collection) {
    return toList(collection, Function.identity());
  }

  public static <T> String toList(Collection<T> collection, Function<T, ?> toStringFunction) {
    return LIST_ITEM + LIST_JOINER.join(Collections2.transform(collection, toStringFunction::apply));
  }

  public ErrorReport(PedanticEnforcerRule rule) {
    this.title = rule.name() + ": " + rule.getSlogan();
    this.lines = new LinkedList<>();
  }

  public ErrorReport useLargeTitle() {
    this.useLargeTitle = true;
    return this;
  }

  public ErrorReport addLine(Object line) {
    this.lines.add(line);
    return this;
  }

  public ErrorReport addDiff(Collection<String> actual, Collection<String> required, String leftTitle, String rightTitle) {
    String diff = SideBySideDiffUtil.diff(actual, required, leftTitle, rightTitle);
    this.lines.add(diff);
    return this;
  }

  public <T> ErrorReport addDiff(Collection<T> actual, Collection<T> required, String leftTitle, String rightTitle, Function<? super T, String> toStringFunction) {
    Collection<String> actualAsString = actual.stream().map(toStringFunction).collect(Collectors.toList());
    Collection<String> requiredAsString = required.stream().map(toStringFunction).collect(Collectors.toList());

    return addDiff(actualAsString, requiredAsString, leftTitle, rightTitle);
  }

  public <T> ErrorReport addDiffUsingToString(Collection<T> actual, Collection<T> required, String leftTitle, String rightTitle) {
    return addDiff(actual, required, leftTitle, rightTitle, toStringFunction());
  }

  public ErrorReport formatLine(String line, Object... params) {
    this.lines.add(String.format(line, params));
    return this;
  }

  public ErrorReport emptyLine() {
    this.lines.add("");
    return this;
  }

  public boolean hasErrors() {
    return !this.lines.isEmpty();
  }

  @Override
  public String toString() {
    return LINE_JOINER.join(
        formatTitle(),
        LINE_JOINER.join(this.lines));
  }

  private String formatTitle() {
    if (this.useLargeTitle) {
      String border = Strings.repeat("#", this.title.length() + 4);
      return LINE_JOINER.join("", border, "# " + this.title + " #", border, "");
    }
    return LINE_JOINER.join("" + this.title, Strings.repeat("=", this.title.length()), "");
  }

}
