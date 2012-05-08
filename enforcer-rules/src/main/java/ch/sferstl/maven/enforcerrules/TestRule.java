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
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Ordering;


public class TestRule implements EnforcerRule {

  private final DocumentBuilder docBuilder;

  public TestRule() throws ParserConfigurationException {
    this.docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
  }

  private Document parseXml(File file) throws SAXException, IOException {
    return this.docBuilder.parse(file);
  }

  /** {@inheritDoc} */
  @Override
  public void execute(EnforcerRuleHelper helper) throws EnforcerRuleException {
    Log log = helper.getLog();
    try {
      // get the various expressions out of the helper.
      MavenProject project = (MavenProject) helper.evaluate("${project}");
      Document pomDoc = this.parseXml(project.getFile());
      DeclaredDependenciesReader reader = new DeclaredDependenciesReader(pomDoc);
      List<Dependency> dependencies = reader.read();

      Ordering<Dependency> ordering = Ordering.from(DependencyComparator.SCOPE)
                                              .compound(DependencyComparator.GROUP_ID)
                                              .compound(DependencyComparator.ARTIFACT_ID);

      if (!ordering.isOrdered(dependencies)) {
        ImmutableList<Dependency> sortedDependencies = ordering.immutableSortedCopy(dependencies);
        log.error("Dependencies are not in order. Should be: " + sortedDependencies);
      }

      System.out.println(new DeclaredModulesReader(pomDoc).read());

    } catch (Exception e) {
      throw new RuntimeException(e);
    }
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
