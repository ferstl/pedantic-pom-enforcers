package com.github.ferstl.maven.pomenforcers.util;

import org.apache.maven.enforcer.rule.api.EnforcerRuleHelper;
import org.junit.Before;
import org.junit.Test;

import static com.github.ferstl.maven.pomenforcers.util.EnforcerRuleUtils.evaluateProperties;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
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
