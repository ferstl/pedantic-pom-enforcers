package com.github.ferstl.maven.pomenforcers;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

/**
 * Matcher that shows the {@link ErrorReport} in case of an unexpected failure.
 */
public class ErrorReportMatcher extends TypeSafeMatcher<ErrorReport> {

  private final boolean expectedErrors;

  ErrorReportMatcher(boolean expectedErrors) {
    this.expectedErrors = expectedErrors;
  }

  @Override
  protected boolean matchesSafely(ErrorReport item) {
    return item.hasErrors() == this.expectedErrors;
  }

  @Override
  public void describeTo(Description description) {
    description.appendText("There should be " + (!this.expectedErrors ? "no " : "") + "errors in the error report");
  }

  @Override
  protected void describeMismatchSafely(ErrorReport item, Description mismatchDescription) {
    mismatchDescription.appendText("There were " + (this.expectedErrors ? "no " : "") + "errors\n");
    if (!this.expectedErrors) {
      mismatchDescription.appendValue(item);
    }
  }

  public static ErrorReportMatcher hasErrors() {
    return new ErrorReportMatcher(true);
  }

  public static ErrorReportMatcher hasNoErrors() {
    return new ErrorReportMatcher(false);
  }
}
