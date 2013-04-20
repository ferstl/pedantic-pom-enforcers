/*
 * Copyright (c) 2013 by Stefan Ferstl <st.ferstl@gmail.com>
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

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.Collections2;


public class ErrorReport {
  private static final String LIST_ITEM = "- ";
  private static final String LINE_SEPARATOR = System.getProperty("line.separator", "\n");
  private static final Joiner LINE_JOINER = Joiner.on(LINE_SEPARATOR);
  private static final Joiner LIST_JOINER = Joiner.on(LINE_SEPARATOR + LIST_ITEM);

  private final String title;
  private final Collection<Object> lines;

  private boolean useLargeTitle;

  public static <T> String toList(Collection<T> collection) {
    return toList(collection, Functions.<T>identity());
  }

  public static <T> String toList(Collection<T> collection, Function<T, ?> toStringFunction) {
    return LIST_ITEM + LIST_JOINER.join(Collections2.transform(collection, toStringFunction));
  }

  public ErrorReport(String title) {
    this.title = title;
    this.lines = new LinkedList<>();
  }

  public ErrorReport(PedanticEnforcerRule rule) {
    this(rule.name() + ": " + rule.getSlogan());
  }

  public ErrorReport useLargeTitle() {
    this.useLargeTitle = true;
    return this;
  }

  public ErrorReport addLine(Object line) {
    this.lines.add(line);
    return this;
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
