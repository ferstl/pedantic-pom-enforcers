package ch.sferstl.maven.enforcerrules;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.maven.enforcer.rule.api.EnforcerRule;
import org.apache.maven.enforcer.rule.api.EnforcerRuleException;
import org.apache.maven.enforcer.rule.api.EnforcerRuleHelper;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluationException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Ordering;


public class PedanticModuleOrderEnforcer implements EnforcerRule {

  private DocumentBuilder docBuilder;

  public PedanticModuleOrderEnforcer() {
    try {
      this.docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
    } catch (ParserConfigurationException e) {
      throw new IllegalStateException("Cannot create document builder", e);
    }
  }

  private Document parseXml(File file) {
    try {
      return this.docBuilder.parse(file);
    } catch (SAXException | IOException e) {
      throw new IllegalStateException("Unable to parse XML file " + file, e);
    }
  }

  /** {@inheritDoc} */
  @Override
  public void execute(EnforcerRuleHelper helper) throws EnforcerRuleException {
    MavenProject project;
    try {
      project = (MavenProject) helper.evaluate("${project}");
    } catch (ExpressionEvaluationException e) {
      throw new EnforcerRuleException("Unable to get maven project", e);
    }

    Document pomDoc = this.parseXml(project.getFile());

    List<String> declaredModules = new DeclaredModulesReader(pomDoc).read();
    Ordering<String> moduleOrdering = Ordering.natural();
    if (!moduleOrdering.isOrdered(declaredModules)) {
      ImmutableList<String> orderedModules = moduleOrdering.immutableSortedCopy(declaredModules);
      throw new EnforcerRuleException("Wrong module order. Correct order: " + orderedModules);
    }

    System.out.println(declaredModules);

  }

  /** {@inheritDoc} */
  @Override
  public boolean isCacheable() {
    return false;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isResultValid(EnforcerRule cachedRule) {
    return false;
  }

  /** {@inheritDoc} */
  @Override
  public String getCacheId() {
    return "uncachable";
  }

}
