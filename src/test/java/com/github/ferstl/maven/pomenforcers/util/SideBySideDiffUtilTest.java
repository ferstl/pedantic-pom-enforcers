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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.assertj.core.api.AbstractAssert;
import org.junit.Test;
import com.github.ferstl.maven.pomenforcers.model.PomSection;
import com.google.common.collect.Ordering;
import static com.github.ferstl.maven.pomenforcers.util.SideBySideDiffUtil.diff;
import static com.github.ferstl.maven.pomenforcers.util.SideBySideDiffUtilTest.SideBySideDiffAssert.assertThat;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;

/**
 * JUnit tests for {@link SideBySideDiffUtil}.
 */
public class SideBySideDiffUtilTest {

  @Test
  public void insertion() {
    String diff = diff(
        asList("abc", "def", "ghi", "jkl"),
        asList("abc", "zyx", "def", "wvu", "ghi", "tsr", "jkl", "qpo"),
        "", "");

    assertThat(diff).hasContent(
        "  abc |   abc",
        "      | + zyx",
        "  def |   def",
        "      | + wvu",
        "  ghi |   ghi",
        "      | + tsr",
        "  jkl |   jkl",
        "      | + qpo"
    );
  }


  @Test
  public void deletion() {
    String diff = diff(
        asList("abc", "zyx", "def", "wvu", "ghi", "tsr", "jkl", "qpo"),
        asList("abc", "def", "ghi", "jkl"),
        "", "");

    assertThat(diff).hasContent(
        "  abc |   abc",
        "- zyx |",
        "  def |   def",
        "- wvu |",
        "  ghi |   ghi",
        "- tsr |",
        "  jkl |   jkl",
        "- qpo |"
    );
  }

  @Test
  public void changeWithBiggerLeftSide() {
    String diff = diff(
        asList("abc", "def", "ghi", "jkl", "mno", "pqr"),
        asList("abc", "zyx", "jkl", "wvu"),
        "", "");

    assertThat(diff).hasContent(
        "  abc |   abc",
        "- def | + zyx",
        "- ghi |",
        "  jkl |   jkl",
        "- mno | + wvu",
        "- pqr |"
    );
  }

  @Test
  public void changeWithBiggerRightSide() {
    String diff = diff(
        asList("abc", "def", "ghi", "jkl", "mno", "pqr"),
        asList("zyx", "wvu", "def", "ghi", "wvu", "tsr", "mno", "pqr"),
        "", "");

    assertThat(diff).hasContent(
        "- abc | + zyx",
        "      | + wvu",
        "  def |   def",
        "  ghi |   ghi",
        "- jkl | + wvu",
        "      | + tsr",
        "  mno |   mno",
        "  pqr |   pqr"
    );
  }

  @Test
  public void titleLongerThanContent() {
    String diff = diff(asList("a", "b", "c"), asList("a", "b"), "leftTitle", "rightTitle");

    assertThat(diff).hasContent(
        "leftTitle | rightTitle",
        "----------------------",
        "  a       |   a",
        "  b       |   b",
        "- c       |"
    );
  }

  @Test
  public void titleShorterThanContent() {
    String diff = diff(asList("abcdef", "ghijkl", "mnopqr"), asList("stuvwx", "yz"), "L", "R");

    assertThat(diff).hasContent(
        "L        | R",
        "-------------------",
        "- abcdef | + stuvwx",
        "- ghijkl | + yz",
        "- mnopqr |"
    );
  }

  @Test
  public void emptyTexts() {
    String diff = diff(Collections.emptyList(), Collections.emptyList(), "", "");

    assertEquals("", diff);
  }

  @Test
  public void emptyTextsWithTitle() {
    String diff = diff(Collections.emptyList(), Collections.emptyList(), "left", "right");

    assertEquals("", diff);
  }

  @Test
  public void originalTextEmpty() {
    String diff = diff(Collections.emptyList(), asList("abc", "def"), "", "");

    assertThat(diff).hasContent(
        "   | + abc",
        "   | + def"
    );
  }

  @Test
  public void revisedTextEmpty() {
    String diff = diff(asList("abc", "def"), Collections.emptyList(), "", "");

    assertThat(diff).hasContent(
        "- abc |",
        "- def |"
    );
  }

  @Test
  public void differentTexts() {
    String diff = diff(asList("abc", "def", "ghi"), asList("jklm", "nopq"), "", "");

    assertThat(diff).hasContent(
        "- abc | + jklm",
        "- def | + nopq",
        "- ghi |"
    );
  }

  @Test
  public void differentLengthsShortOriginal() {
    String diff = diff(singletonList("abc"), asList("def", "ghi", "jkl", "abc"), "", "");

    assertThat(diff).hasContent(
        "      | + def",
        "      | + ghi",
        "      | + jkl",
        "  abc |   abc"
    );
  }

  @Test
  public void differentLengthsShortRevised() {
    String diff = diff(asList("def", "ghi", "jkl", "abc"), singletonList("abc"), "", "");

    assertThat(diff).hasContent(
        "- def |",
        "- ghi |",
        "- jkl |",
        "  abc |   abc"
    );
  }

  /**
   * Tests the combination of all scenarios by comparing the required order of {@link PomSection}s
   * to alphabetically sorted {@link PomSection}s.
   */
  @Test
  public void combinations() {
    List<String> required = Arrays.stream(PomSection.values()).map(PomSection::getSectionName).collect(Collectors.toList());

    List<String> actual = Ordering.usingToString().sortedCopy(required);

    String diff = diff(actual, required, "", "");

    assertThat(diff).hasContent(
        "                         | + modelVersion",
        "                         | + prerequisites",
        "                         | + parent",
        "                         | + groupId",
        "  artifactId             |   artifactId",
        "- build                  | + version",
        "- ciManagement           | + packaging",
        "- contributors           | + name",
        "- dependencies           |",
        "- dependencyManagement   |",
        "  description            |   description",
        "- developers             | + url",
        "- distributionManagement |",
        "- groupId                |",
        "- inceptionYear          |",
        "- issueManagement        |",
        "  licenses               |   licenses",
        "- mailingLists           |",
        "- modelVersion           |",
        "- modules                |",
        "- name                   |",
        "  organization           |   organization",
        "- packaging              | + inceptionYear",
        "- parent                 | + ciManagement",
        "                         | + mailingLists",
        "                         | + issueManagement",
        "                         | + developers",
        "                         | + contributors",
        "                         | + scm",
        "                         | + repositories",
        "  pluginRepositories     |   pluginRepositories",
        "- prerequisites          | + distributionManagement",
        "- profiles               | + modules",
        "  properties             |   properties",
        "                         | + dependencyManagement",
        "                         | + dependencies",
        "                         | + build",
        "                         | + profiles",
        "  reporting              |   reporting",
        "  reports                |   reports",
        "- repositories           |",
        "- scm                    |",
        "- url                    |",
        "- version                |"
    );
  }

  static class SideBySideDiffAssert extends AbstractAssert<SideBySideDiffAssert, String> {

    SideBySideDiffAssert(String expectedContent) {
      super(expectedContent, SideBySideDiffAssert.class);
    }

    static SideBySideDiffAssert assertThat(String actual) {
      return new SideBySideDiffAssert(actual);
    }

    SideBySideDiffAssert hasContent(String... expectedContent) {
      String[] actualContentLines = this.actual.split("\\r|\\n|\\r\\n");
      StringBuilder mismatchDescription = new StringBuilder();
      boolean result = true;

      if (expectedContent.length != actualContentLines.length) {
        mismatchDescription.append("Expected and actual diffs do not have the same number of lines.\n");
        result = false;
      }

      int minLength = Math.min(actualContentLines.length, expectedContent.length);
      for (int i = 0; i < minLength; i++) {
        if (!actualContentLines[i].equals(expectedContent[i])) {
          mismatchDescription.append("Mismatch in line").append(i + 1).append(":\n")
              .append("Expected: '").append(expectedContent[i]).append("'\n")
              .append("but was : '").append(actualContentLines[i]).append("'\n");

          result = false;
        }
      }

      if (!result) {
        mismatchDescription.append("\nWhole diff:\n");
        for (String actualContentLine : actualContentLines) {
          mismatchDescription.append(actualContentLine).append("\n");
        }
        failWithMessage(mismatchDescription.toString());
      }

      return this;
    }
  }
}
