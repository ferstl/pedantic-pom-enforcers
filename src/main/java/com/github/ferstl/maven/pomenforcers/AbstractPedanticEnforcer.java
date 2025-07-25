/*
 * Copyright (c) 2012 - 2023 the original author or authors.
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

import java.util.Objects;

import javax.xml.bind.JAXB;

import org.apache.maven.enforcer.rule.api.AbstractEnforcerRule;
import org.apache.maven.enforcer.rule.api.EnforcerLevel;
import org.apache.maven.enforcer.rule.api.EnforcerRuleException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluator;
import org.w3c.dom.Document;

import com.github.ferstl.maven.pomenforcers.model.ProjectModel;
import com.github.ferstl.maven.pomenforcers.util.XmlUtils;

public abstract class AbstractPedanticEnforcer extends AbstractEnforcerRule {

  private final ExpressionEvaluator helper;
  
  private final MavenProject project;
  
  private Document pom;
  private ProjectModel projectModel;

  /**
   * If set to <code>true</code>, the enforcer rule will only issue a warning in the log and not fail the build.
   * Enabling this option is a good way to start using the enforcer rules in an already existing project.
   *
   * @configParam
   * @default false
   * @since 2.0.0
   */
  private boolean warnOnly;
  
  public AbstractPedanticEnforcer(final MavenProject project, final ExpressionEvaluator helper) {
	  this.project = Objects.requireNonNull(project);
	  this.helper = Objects.requireNonNull(helper);
  }

  @Override
  public final void execute() throws EnforcerRuleException {
    Document pom = XmlUtils.parseXml(project.getFile());
    ProjectModel model = JAXB.unmarshal(project.getFile(), ProjectModel.class);
    
    initialize(pom, model);

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
  void initialize(Document pom, ProjectModel projectModel) {
    this.pom = pom;
    this.projectModel = projectModel;
  }

  protected ExpressionEvaluator getHelper() {
    return this.helper;
  }
  
  protected MavenProject getMavenProject() {
	return this.project;
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
  public String getCacheId() {
    return getClass() + "-uncachable";
  }
}
