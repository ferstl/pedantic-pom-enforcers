package com.github.ferstl.maven.pomenforcers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.apache.maven.enforcer.rule.api.EnforcerRuleException;
import org.apache.maven.enforcer.rule.api.EnforcerRuleHelper;
import org.apache.maven.project.MavenProject;
import org.w3c.dom.Document;

import com.github.ferstl.maven.pomenforcers.util.CommaSeparatorUtils;
import com.github.ferstl.maven.pomenforcers.util.EnforcerRuleUtils;
import com.github.ferstl.maven.pomenforcers.util.XmlUtils;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

public class PedanticPluginManagementLocationEnforcer extends AbstractPedanticEnforcer {

  private final Set<PomInfo> pluginManagingPoms;

  public PedanticPluginManagementLocationEnforcer() {
    this.pluginManagingPoms = new HashSet<>();
  }

  @Override
  protected void doEnforce(EnforcerRuleHelper helper, Document pom) throws EnforcerRuleException {
    MavenProject mavenProject = EnforcerRuleUtils.getMavenProject(helper);
    if (containsPluginManagement(pom) && !isPluginManagementAllowed(mavenProject)) {
      throw new EnforcerRuleException("One does not simply declare plugin management. " +
      		"Only these POMs are allowed to manage plugins: " + this.pluginManagingPoms);
    }
  }

  public void setPluginManagingPoms(String pluginManagingPoms) {
    PomInfoTransformer pomInfoTransformer = new PomInfoTransformer();
    CommaSeparatorUtils.splitAndAddToCollection(pluginManagingPoms, this.pluginManagingPoms, pomInfoTransformer);
  }

  private boolean containsPluginManagement(Document pom) {
    return XmlUtils.evaluateXPath("/project/build/pluginManagement", pom) != null;

  }

  private boolean isPluginManagementAllowed(final MavenProject project) {
    Predicate<PomInfo> pomInfoFilter = new Predicate<PomInfo>() {
      @Override
      public boolean apply(PomInfo input) {
        return project.getGroupId().equals(input.getGroupId())
            && project.getArtifactId().equals(input.getArtifactId());
      }
    };
    return this.pluginManagingPoms.isEmpty() ||
           Collections2.filter(this.pluginManagingPoms, pomInfoFilter).size() != 0;
  }


  @Override
  protected void accept(PedanticEnforcerVisitor visitor) {
    visitor.visit(this);
  }

  private class PomInfo {
    private final String groupId;
    private final String artifactId;

    public PomInfo(String groupId, String artifactId) {
      this.groupId = groupId;
      this.artifactId = artifactId;
    }

    public String getGroupId() {
      return this.groupId;
    }

    public String getArtifactId() {
      return this.artifactId;
    }

    @Override
    public String toString() {
      return this.groupId + ":" + this.artifactId;
    }
  }

  private class PomInfoTransformer implements Function<String, PomInfo> {

    private final Splitter colonSplitter = Splitter.on(":");

   @Override
   public PomInfo apply(String input) {
     ArrayList<String> pomElements = Lists.newArrayList(this.colonSplitter.split(input));

     if(pomElements.size() != 2) {
       throw new IllegalArgumentException("Cannot read POM information: " + input);
     }

     return new PomInfo(pomElements.get(0), pomElements.get(1));
    }
  }

}
