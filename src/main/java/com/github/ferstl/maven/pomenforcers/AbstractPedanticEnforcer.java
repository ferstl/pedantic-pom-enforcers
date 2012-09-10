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
package com.github.ferstl.maven.pomenforcers;

import org.apache.maven.enforcer.rule.api.EnforcerRule;
import org.apache.maven.enforcer.rule.api.EnforcerRuleException;
import org.apache.maven.enforcer.rule.api.EnforcerRuleHelper;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.w3c.dom.Document;

import com.github.ferstl.maven.pomenforcers.model.ProjectModel;
import com.github.ferstl.maven.pomenforcers.serializer.PomSerializer;
import com.github.ferstl.maven.pomenforcers.util.EnforcerRuleUtils;
import com.github.ferstl.maven.pomenforcers.util.XmlUtils;



public abstract class AbstractPedanticEnforcer implements EnforcerRule {

  private EnforcerRuleHelper helper;
  private Log log;
  private Document pom;
  private ProjectModel projectModel;

  @Override
  public final void execute(EnforcerRuleHelper helper) throws EnforcerRuleException {
    this.helper = helper;
    this.log = helper.getLog();

    // Read the POM
    MavenProject project = EnforcerRuleUtils.getMavenProject(helper);
    this.pom = XmlUtils.parseXml(project.getFile());
    this.projectModel = new PomSerializer(this.pom).read();

    // Enforce
    doEnforce();
  }

  /**
   * Initialization method. Use this method when the enforcer rule is not instantiated by the
   * maven-enforcer-plugin.
   * @param helper Enforcer rule helper.
   * @param pom POM Document.
   * @param projectModel Project model.
   */
  void initialize(EnforcerRuleHelper helper, Document pom, ProjectModel projectModel) {
    this.helper = helper;
    this.log = helper.getLog();
    this.pom = pom;
    this.projectModel = projectModel;
  }

  protected EnforcerRuleHelper getHelper() {
    return this.helper;
  }

  protected Log getLog() {
    return this.log;
  }

  protected Document getPom() {
    return this.pom;
  }

  protected ProjectModel getProjectModel() {
    return this.projectModel;
  }

  protected abstract void doEnforce() throws EnforcerRuleException;

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
