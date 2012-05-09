package ch.sferstl.maven.pomenforcer;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.maven.enforcer.rule.api.EnforcerRule;
import org.apache.maven.enforcer.rule.api.EnforcerRuleException;
import org.apache.maven.enforcer.rule.api.EnforcerRuleHelper;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluationException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;


public class PedanticModuleOrderEnforcer implements EnforcerRule {

  private DocumentBuilder docBuilder;

  /** All modules in this list won't be checked for the correct order. */
  private final List<String> ignoredModules;

  public PedanticModuleOrderEnforcer() {
    this.ignoredModules = Lists.newArrayList();
    try {
      this.docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
    } catch (ParserConfigurationException e) {
      throw new IllegalStateException("Cannot create document builder", e);
    }
  }

  @Override
  public void execute(EnforcerRuleHelper helper) throws EnforcerRuleException {
    Log log = helper.getLog();
    log.info("Enforcing alphabetical module order. These modules are ignored: " + this.ignoredModules);
    MavenProject project;
    try {
      project = (MavenProject) helper.evaluate("${project}");
    } catch (ExpressionEvaluationException e) {
      throw new EnforcerRuleException("Unable to get maven project", e);
    }

    // Read the POM
    Document pomDoc = this.parseXml(project.getFile());

    // Remove all modules to be ignored.
    List<String> declaredModules = new DeclaredModulesReader(pomDoc).read();
    declaredModules.removeAll(this.ignoredModules);

    // Enforce the module order
    Ordering<String> moduleOrdering = Ordering.natural();
    if (!moduleOrdering.isOrdered(declaredModules)) {
      ImmutableList<String> orderedModules = moduleOrdering.immutableSortedCopy(declaredModules);
      throw new EnforcerRuleException("Wrong module order. Correct order is: " + orderedModules);
    }
  }

  private Document parseXml(File file) {
    try {
      return this.docBuilder.parse(file);
    } catch (SAXException | IOException e) {
      throw new IllegalStateException("Unable to parse XML file " + file, e);
    }
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
    return "uncachable";
  }

}
