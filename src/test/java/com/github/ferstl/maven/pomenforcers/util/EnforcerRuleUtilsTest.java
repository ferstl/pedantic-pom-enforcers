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

import org.apache.maven.enforcer.rule.api.EnforcerRuleHelper;
import org.junit.Before;
import org.junit.Test;
import static com.github.ferstl.maven.pomenforcers.util.EnforcerRuleUtils.evaluateProperties;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class EnforcerRuleUtilsTest {

  private EnforcerRuleHelper mockHelper;

  @Before
  public void setup() throws Exception {
    this.mockHelper = mock(EnforcerRuleHelper.class);
    when(this.mockHelper.evaluate(anyString())).thenReturn("test");
  }

  @Test
  public void testEvaluateProperties() {
    assertThat(evaluateProperties("foo-${user.name}-bar", this.mockHelper), is("foo-test-bar"));
    assertThat(evaluateProperties("foo-${x}-bar-${y}", this.mockHelper), is("foo-test-bar-test"));
    assertThat(evaluateProperties("foo", this.mockHelper), is("foo"));
    assertThat(evaluateProperties("", this.mockHelper), is(""));
    assertThat(evaluateProperties(null, this.mockHelper), nullValue());
  }

}
