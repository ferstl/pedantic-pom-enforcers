package ch.sferstl.maven.pomenforcer;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.maven.enforcer.rule.api.EnforcerRule;
import org.apache.maven.enforcer.rule.api.EnforcerRuleHelper;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluationException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;


public abstract class AbstractPedanticEnforcer implements EnforcerRule {

  private static final Splitter COMMA_SPLITTER = Splitter.on(",");
  private DocumentBuilder docBuilder;

  public AbstractPedanticEnforcer() {
    try {
      this.docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
    } catch (ParserConfigurationException e) {
      throw new IllegalStateException("Cannot create document builder", e);
    }
  }

  protected Document parseXml(File file) {
    try {
      return this.docBuilder.parse(file);
    } catch (SAXException | IOException e) {
      throw new IllegalStateException("Unable to parse XML file " + file, e);
    }
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

  protected void splitAndAddToCollection(String commaSeparatedItems, Collection<String> collection) {
    Function<String, String> identity = Functions.identity();
    this.splitAndAddToCollection(commaSeparatedItems, collection, identity);
  }

  protected <T> void splitAndAddToCollection(String commaSeparatedItems, Collection<T> collection, Function<String, T> transformer) {
    Iterable<String> items = COMMA_SPLITTER.split(commaSeparatedItems);
    // Don't touch the collection if there is nothing to add.
    if (items.iterator().hasNext()) {
      collection.clear();
    }
    Iterables.addAll(collection, Iterables.transform(items, transformer));
  }

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
    return this.getClass() + "-uncachable";
  }

}
