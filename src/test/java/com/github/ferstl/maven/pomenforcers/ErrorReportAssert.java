/*
 * Copyright (c) 2012 - 2025 the original author or authors.
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

import org.assertj.core.api.AbstractAssert;

/**
 * Matcher that shows the {@link ErrorReport} in case of an unexpected failure.
 */
class ErrorReportAssert extends AbstractAssert<ErrorReportAssert, ErrorReport> {

  public ErrorReportAssert(ErrorReport actual) {
    super(actual, ErrorReportAssert.class);
  }

  public static ErrorReportAssert assertThat(ErrorReport actual) {
    return new ErrorReportAssert(actual);
  }

  public ErrorReportAssert hasErrors() {
    if (!this.actual.hasErrors()) {
      failWithMessage("There were no errors");
    }

    return this;
  }

  public ErrorReportAssert hasNoErrors() {
    if (this.actual.hasErrors()) {
      failWithMessage("There were errors");
    }

    return this;
  }
}
