/*
 * Copyright (c) 2012 - 2015 by Stefan Ferstl <st.ferstl@gmail.com>
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

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

/**
 * Matcher that shows the {@link ErrorReport} in case of an unexpected failure.
 */
class ErrorReportMatcher extends TypeSafeMatcher<ErrorReport> {

  private final boolean expectedErrors;

  private ErrorReportMatcher(boolean expectedErrors) {
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
