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
