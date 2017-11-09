/*
 * Copyright (c) 2012 - 2015 by Stefan Ferstl <st.ferstl@gmail.com>
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

import org.apache.maven.enforcer.rule.api.EnforcerLevel;
import org.apache.maven.enforcer.rule.api.EnforcerRule;
import org.apache.maven.enforcer.rule.api.EnforcerRule2;
import org.apache.maven.enforcer.rule.api.EnforcerRuleException;
import org.apache.maven.enforcer.rule.api.EnforcerRuleHelper;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.w3c.dom.Document;
import com.github.ferstl.maven.pomenforcers.model.ProjectModel;
import com.github.ferstl.maven.pomenforcers.serializer.PomSerializer;
import com.github.ferstl.maven.pomenforcers.util.EnforcerRuleUtils;
import com.github.ferstl.maven.pomenforcers.util.XmlUtils;

public abstract class AbstractPedanticEnforcer implements EnforcerRule2 {

  private EnforcerRuleHelper helper;
  private Log log;
  private Document pom;
  private ProjectModel projectModel;

  /**
   * If set to <code>true</code>, the enforcer rule will only issue a warning in the log and not fail the build.
   * Enabling this option is a good way to start using the enforcer rules in an already existing project.
   *
   * @since 1.4.0
   */
  private boolean warnOnly;

  @Override
  public final void execute(EnforcerRuleHelper helper) throws EnforcerRuleException {
    MavenProject project = EnforcerRuleUtils.getMavenProject(helper);
    Document pom = XmlUtils.parseXml(project.getFile());
    PomSerializer pomSerializer = new PomSerializer(pom);
    ProjectModel model = pomSerializer.read();

    initialize(helper, pom, model);

    ErrorReport report = new ErrorReport(getDescription());
    doEnforce(report);

    if (report.hasErrors()) {
      throw new EnforcerRuleException(report.toString());
    }
  }

  /**
   * Initialization method. Use this method when the enforcer rule is not instantiated by the
   * maven-enforcer-plugin.
   *
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

  protected abstract PedanticEnforcerRule getDescription();

  protected abstract void doEnforce(ErrorReport report);

  protected abstract void accept(PedanticEnforcerVisitor visitor);

  @Override
  public EnforcerLevel getLevel() {
    return this.warnOnly ? EnforcerLevel.WARN : EnforcerLevel.ERROR;
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
}
