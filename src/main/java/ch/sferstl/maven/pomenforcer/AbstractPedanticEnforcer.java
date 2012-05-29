package ch.sferstl.maven.pomenforcer;

import java.util.Collection;

import org.apache.maven.enforcer.rule.api.EnforcerRule;
import org.apache.maven.enforcer.rule.api.EnforcerRuleException;
import org.apache.maven.enforcer.rule.api.EnforcerRuleHelper;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluationException;
import org.w3c.dom.Document;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;


public abstract class AbstractPedanticEnforcer implements EnforcerRule {

  private static final Splitter COMMA_SPLITTER = Splitter.on(",");
  protected static final Joiner COMMA_JOINER = Joiner.on(",");

  @Override
  public void execute(EnforcerRuleHelper helper) throws EnforcerRuleException {
    // Read the POM
    MavenProject project = getMavenProject(helper);
    Document pom = XmlParser.parseXml(project.getFile());

    // Enforce
    doEnforce(helper, pom);
  }

  protected MavenProject getMavenProject(EnforcerRuleHelper helper) {
    MavenProject project;
    try {
      project = (MavenProject) helper.evaluate("${project}");
    } catch (ExpressionEvaluationException e) {
      throw new IllegalStateException("Unable to get maven project", e);
    }
    return project;
  }

  protected void splitAndAddToCollection(
      String commaSeparatedItems, Collection<String> collection) {
    Function<String, String> identity = Functions.identity();
    this.splitAndAddToCollection(commaSeparatedItems, collection, identity);
  }

  protected <T> void splitAndAddToCollection(
      String commaSeparatedItems, Collection<T> collection, Function<String, T> transformer) {
    Iterable<String> items = COMMA_SPLITTER.split(commaSeparatedItems);
    // Don't touch the collection if there is nothing to add.
    if (items.iterator().hasNext()) {
      collection.clear();
    }
    Iterables.addAll(collection, Iterables.transform(items, transformer));
  }

  protected String resolveStringProperty(String property, EnforcerRuleHelper helper) {
    if (!Strings.isNullOrEmpty(property) && property.startsWith("${") && property.endsWith("}")) {
      try {
        return (String) helper.evaluate(property);
      } catch (ExpressionEvaluationException e) {
        throw new IllegalArgumentException("Unable to resolve property " + property);
      } catch (ClassCastException e) {
        throw new IllegalArgumentException("Property " + property + " does not evaluate to String");
      }
    }
    return property;
  }

  protected abstract void doEnforce(EnforcerRuleHelper helper, Document pom) throws EnforcerRuleException;

  protected abstract void accept(PedanticEnforcerVisitor visitor);

  @Override
  public boolean isCacheable() {
    return false;
  }

  @Override
  public boolean isResultValid(EnforcerRule cachedRule) {
    return false;
  }

  @Override
  public String getCacheId() {
    return getClass() + "-uncachable";
  }

}
