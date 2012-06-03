/*
 * Copyright (c) 2012 by The Author(s)
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
package ch.sferstl.maven.pomenforcer.util;

import org.apache.maven.enforcer.rule.api.EnforcerRuleHelper;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluationException;

import com.google.common.base.Strings;

public final class EnforcerRuleUtils {

  public static MavenProject getMavenProject(EnforcerRuleHelper helper) {
    try {
      return (MavenProject) helper.evaluate("${project}");
    } catch (ExpressionEvaluationException e) {
      throw new IllegalStateException("Unable to get maven project", e);
    }
  }

  public static String evaluateStringProperty(String property, EnforcerRuleHelper helper) {
    // That's exactly the way properties are detected within the maven evaluators
    if (!Strings.isNullOrEmpty(property) && property.startsWith("${") && property.endsWith("}")) {
      try {
        return (String) helper.evaluate(property);
      } catch (ExpressionEvaluationException e) {
        throw new IllegalArgumentException("Unable to resolve property " + property);
      } catch (ClassCastException e) {
        throw new IllegalArgumentException("Property " + property + " does not evaluate to a String");
      }
    }
    return property;
  }

  private EnforcerRuleUtils() {}
}
