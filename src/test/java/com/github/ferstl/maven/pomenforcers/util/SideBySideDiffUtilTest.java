package com.github.ferstl.maven.pomenforcers.util;

import java.util.Arrays;
import java.util.List;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.junit.Test;

import com.github.ferstl.maven.pomenforcers.model.PomSection;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Ordering;

import static com.github.ferstl.maven.pomenforcers.util.SideBySideDiffUtilTest.SideBySideDiffMatcher.hasContent;
import static org.junit.Assert.assertThat;

/**
 * JUnit tests for {@link SideBySideDiffUtil}.
 */
public class SideBySideDiffUtilTest {

  // empty texts
  // left/right empty
  // completely different texts
  // text with different lengths (left/right shorter)
  // insertion
  // change with bigger left side
  // change with bigger right side


  /**
   * Tests the combination of all scenarios by comparing the required order of {@link PomSection}s
   * to alphabetically sorted {@link PomSection}s.
   */
  @Test
  public void combinations() {
    List<String> required = FluentIterable.from(Arrays.asList(PomSection.values()))
    .transform(new Function<PomSection, String>() {

      @Override
      public String apply(PomSection input) {
        return input.getSectionName();
      }})
    .toList();

    List<String> actual = Ordering.usingToString().sortedCopy(required);

    String diff = SideBySideDiffUtil.diff(actual, required);
    assertThat(diff, hasContent(
        "                         | + modelVersion          ",
        "                         | + prerequisites         ",
        "                         | + parent                ",
        "                         | + groupId               ",
        "  artifactId             |   artifactId            ",
        "- build                  | + version               ",
        "- ciManagement           | + packaging             ",
        "- contributors           | + name                  ",
        "- dependencies           |                         ",
        "- dependencyManagement   |                         ",
        "  description            |   description           ",
        "- developers             | + url                   ",
        "- distributionManagement |                         ",
        "- groupId                |                         ",
        "- inceptionYear          |                         ",
        "- issueManagement        |                         ",
        "  licenses               |   licenses              ",
        "- mailingLists           |                         ",
        "- modelVersion           |                         ",
        "- modules                |                         ",
        "- name                   |                         ",
        "  organization           |   organization          ",
        "- packaging              | + inceptionYear         ",
        "- parent                 | + ciManagement          ",
        "                         | + mailingLists          ",
        "                         | + issueManagement       ",
        "                         | + developers            ",
        "                         | + contributors          ",
        "                         | + scm                   ",
        "                         | + repositories          ",
        "  pluginRepositories     |   pluginRepositories    ",
        "- prerequisites          | + distributionManagement",
        "- profiles               | + modules               ",
        "  properties             |   properties            ",
        "                         | + dependencyManagement  ",
        "                         | + dependencies          ",
        "                         | + build                 ",
        "                         | + profiles              ",
        "  reporting              |   reporting             ",
        "  reports                |   reports               ",
        "- repositories           |                         ",
        "- scm                    |                         ",
        "- url                    |                         ",
        "- version                |                         "
        ));
  }

  static class SideBySideDiffMatcher extends TypeSafeDiagnosingMatcher<String> {

    private final String[] expectedContent;

    @Factory
    public static Matcher<String> hasContent(String... expectedContent) {
      return new SideBySideDiffMatcher(expectedContent);
    }

    public SideBySideDiffMatcher(String[] expectedContent) {
      this.expectedContent = expectedContent;
    }

    @Override
    public void describeTo(Description description) {
      for (String string : this.expectedContent) {
        description.appendText(string).appendText("\n");
      }
    }

    @Override
    protected boolean matchesSafely(String actualDiff, Description mismatchDescription) {
      String[] lines = actualDiff.split("\\r|\\n|\\r\\n");
      boolean result = true;

      if (lines.length != this.expectedContent.length) {
        mismatchDescription.appendText("Expected and actual diffs do not have the same number of lines.");
        result = false;
      }

      int minLength = Math.min(this.expectedContent.length, lines.length);
      for(int i = 0; i < minLength; i++) {
        if (!this.expectedContent[i].equals(lines[i])) {
          mismatchDescription.appendText("Mismatch in line").appendValue(i + 1).appendText(":\n")
          .appendText("Expected: '").appendText(this.expectedContent[i]).appendText("'\n")
          .appendText("but was : '").appendText(lines[i]).appendText("'\n");

          result = false;
        }
      }

      return result;
    }

  }
}
