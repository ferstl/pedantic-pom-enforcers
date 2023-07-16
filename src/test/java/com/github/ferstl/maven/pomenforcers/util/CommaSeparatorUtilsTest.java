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
package com.github.ferstl.maven.pomenforcers.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class CommaSeparatorUtilsTest {

  @Test
  public void testSplitAndAddToCollection() {
    Map<String, List<String>> tests = ImmutableMap.<String, List<String>>builder()
        .put("a,b,c", ImmutableList.of("a", "b", "c"))
        .put("a,b,,,c,", ImmutableList.of("a", "b", "c"))
        .put(" a \n,\n   \t b   ,\r\n  c \t\n", ImmutableList.of("a", "b", "c"))
        .build();
    for (Map.Entry<String, List<String>> test : tests.entrySet()) {
      List<String> l = new ArrayList<>();
      CommaSeparatorUtils.splitAndAddToCollection(test.getKey(), l);
      assertThat(l, equalTo(test.getValue()));
    }
  }
}
