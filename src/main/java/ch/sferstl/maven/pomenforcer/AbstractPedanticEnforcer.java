package ch.sferstl.maven.pomenforcer;

import org.apache.maven.enforcer.rule.api.EnforcerRule;
import org.apache.maven.enforcer.rule.api.EnforcerRuleException;
import org.apache.maven.enforcer.rule.api.EnforcerRuleHelper;
import org.apache.maven.project.MavenProject;
import org.w3c.dom.Document;

import ch.sferstl.maven.pomenforcer.util.EnforcerRuleUtils;


public abstract class AbstractPedanticEnforcer implements EnforcerRule {

  @Override
  public void execute(EnforcerRuleHelper helper) throws EnforcerRuleException {
    // Read the POM
    MavenProject project = EnforcerRuleUtils.getMavenProject(helper);
    Document pom = XmlParser.parseXml(project.getFile());

    // Enforce
    doEnforce(helper, pom);
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
    return getClass() + "-uncachable";
  }

  protected abstract void doEnforce(EnforcerRuleHelper helper, Document pom) throws EnforcerRuleException;

  protected abstract void accept(PedanticEnforcerVisitor visitor);
}
