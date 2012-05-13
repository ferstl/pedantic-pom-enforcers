package ch.sferstl.maven.pomenforcer;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.maven.enforcer.rule.api.EnforcerRule;
import org.apache.maven.enforcer.rule.api.EnforcerRuleHelper;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluationException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;


public abstract class AbstractPedanticEnforcer implements EnforcerRule {

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
