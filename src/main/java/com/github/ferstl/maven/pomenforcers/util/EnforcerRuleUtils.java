/*
 * Copyright (c) 2012 - 2019 the original author or authors.
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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.maven.enforcer.rule.api.EnforcerRuleHelper;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluationException;

import com.google.common.base.Strings;

public final class EnforcerRuleUtils {

  private static final Pattern PROPERTY_PATTERN = Pattern.compile("\\$\\{.*?\\}");

  public static MavenProject getMavenProject(EnforcerRuleHelper helper) {
    try {
      return (MavenProject) helper.evaluate("${project}");
    } catch (ExpressionEvaluationException e) {
      throw new IllegalStateException("Unable to get maven project", e);
    }
  }

  public static String evaluateProperties(String input, EnforcerRuleHelper helper) {
    if (!Strings.isNullOrEmpty(input)) {
      Matcher matcher = PROPERTY_PATTERN.matcher(input);
      StringBuffer substituted = new StringBuffer();
      while(matcher.find()) {
        String property = matcher.group();
        matcher.appendReplacement(substituted, evaluateStringProperty(property, helper));
      }
      matcher.appendTail(substituted);
      return substituted.toString();
    }
    return input;
  }

  private static String evaluateStringProperty(String property, EnforcerRuleHelper helper) {
    try {
      return (String) helper.evaluate(property);
    } catch (ExpressionEvaluationException e) {
      throw new IllegalArgumentException("Unable to resolve property " + property);
    } catch (ClassCastException e) {
      throw new IllegalArgumentException("Property " + property + " does not evaluate to a String");
    }
  }

  private EnforcerRuleUtils() {}
}
