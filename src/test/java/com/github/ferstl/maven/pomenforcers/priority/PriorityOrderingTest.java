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
package com.github.ferstl.maven.pomenforcers.priority;

import java.util.ArrayList;
import java.util.function.Function;
import org.junit.Test;
import com.google.common.collect.Lists;
import static org.assertj.core.api.Assertions.assertThat;


public class PriorityOrderingTest {


  @Test
  public void testCompare() {
    ArrayList<String> prioritizedItems = Lists.newArrayList("z", "y", "x");
    Function<String, String> transformer = Function.identity();
    PriorityOrdering<String, String> testComparator = new PriorityOrdering<>(prioritizedItems, transformer);

    // x is in the priority list, a isn't -> a > x
    assertThat(testComparator.compare("a", "x")).isGreaterThan(0);
    // x is located after y in the priority list -> x > y
    assertThat(testComparator.compare("x", "y")).isGreaterThan(0);
    // x is located after y in the priority list -> y < x
    assertThat(testComparator.compare("y", "x")).isLessThan(0);
    // equality applies to values in the priority list
    assertThat(testComparator.compare("x", "x")).isEqualTo(0);
    // regular comparison for values that are not in the priority list
    assertThat(testComparator.compare("b", "c")).isLessThan(0);
    assertThat(testComparator.compare("b", "b")).isEqualTo(0);
    assertThat(testComparator.compare("c", "b")).isGreaterThan(0);
  }

  @Test
  public void testCompareWithoutPriorities() {
    ArrayList<String> prioritizedItems = Lists.newArrayList();
    Function<String, String> identity = Function.identity();
    PriorityOrdering<String, String> testComparator = new PriorityOrdering<>(prioritizedItems, identity);

    assertThat(testComparator.compare("a", "b")).isLessThan(0);
    assertThat(testComparator.compare("a", "a")).isEqualTo(0);
    assertThat(testComparator.compare("b", "a")).isGreaterThan(0);
  }


}
