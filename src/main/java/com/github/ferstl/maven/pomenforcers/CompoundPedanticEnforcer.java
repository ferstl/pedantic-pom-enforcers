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

import java.util.Collection;

import org.apache.maven.enforcer.rule.api.EnforcerRuleException;
import org.apache.maven.enforcer.rule.api.EnforcerRuleHelper;
import org.apache.maven.project.MavenProject;
import org.w3c.dom.Document;

import com.github.ferstl.maven.pomenforcers.util.CommaSeparatorUtils;
import com.github.ferstl.maven.pomenforcers.util.EnforcerRuleUtils;
import com.github.ferstl.maven.pomenforcers.util.XmlUtils;
import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.google.common.collect.Sets;


public class CompoundPedanticEnforcer extends AbstractPedanticEnforcer {

  /** See {@link PedanticPomSectionOrderEnforcer#setSectionPriorities(String)}.*/
  private String pomSectionPriorities;

  /** See {@link PedanticModuleOrderEnforcer#setIgnoredModules(String)}. */
  private String moduleOrderIgnores;

  /** See {@link PedanticDependencyOrderEnforcer#setOrderBy(String)}.*/
  private String dependenciesOrderBy;

  /** See {@link PedanticDependencyOrderEnforcer#setGroupIdPriorities(String)}.*/
  private String dependenciesGroupIdPriorities;

  /** See {@link PedanticDependencyOrderEnforcer#setArtifactIdPriorities(String)}.*/
  private String dependenciesArtifactIdPriorities;

  /** See {@link PedanticDependencyOrderEnforcer#setScopePriorities(String)}.*/
  private String dependenciesScopePriorities;

  /** See {@link PedanticDependencyManagementOrderEnforcer#setOrderBy(String)}.*/
  private String dependencyManagementOrderBy;

  /** See {@link PedanticDependencyManagementOrderEnforcer#setGroupIdPriorities(String)}.*/
  private String dependencyManagementGroupIdPriorities;

  /** See {@link PedanticDependencyManagementOrderEnforcer#setArtifactIdPriorities(String)}.*/
  private String dependencyManagementArtifactIdPriorities;

  /** See {@link PedanticDependencyManagementOrderEnforcer#setScopePriorities(String)}.*/
  private String dependencyManagementScopePriorities;

  /** See {@link PedanticDependencyConfigurationEnforcer#setManageVersions(boolean)}. */
  private Boolean manageDependencyVersions;

  /** See {@link PedanticDependencyConfigurationEnforcer#setAllowUnmanagedProjectVersions(boolean)}. */
  private Boolean allowUnmangedProjectVersions;

  /** See {@link PedanticDependencyConfigurationEnforcer#setManageExclusions(boolean)}. */
  private Boolean manageDependencyExclusions;

  /** See {@link PedanticDependencyScopeEnforcer#setCompileDependencies(String)}. */
  private String compileDependencies;

  /** See {@link PedanticDependencyScopeEnforcer#setProvidedDependencies(String)}. */
  private String providedDependencies;

  /** See {@link PedanticDependencyScopeEnforcer#setRuntimeDependencies(String)}. */
  private String runtimeDependencies;

  /** See {@link PedanticDependencyScopeEnforcer#setSystemDependencies(String)}. */
  private String systemDependencies;

  /** See {@link PedanticDependencyScopeEnforcer#setTestDependencies(String)}. */
  private String testDependencies;

  /** See {@link PedanticDependencyScopeEnforcer#setImportDependencies(String)}. */
  private String importDependencies;

  /** See {@link PedanticPluginManagementOrderEnforcer#setOrderBy(String)}.*/
  private String pluginManagementOrderBy;

  /** See {@link PedanticPluginManagementOrderEnforcer#setGroupIdPriorities(String)}.*/
  private String pluginManagementGroupIdPriorities;

  /** See {@link PedanticPluginManagementOrderEnforcer#setArtifactIdPriorities(String)}.*/
  private String pluginManagementArtifactIdPriorities;

  /** See {@link PedanticPluginManagementLocationEnforcer#setPluginManagingPoms(String)}. */
  private String pluginManagingPoms;

  /** See {@link PedanticPluginConfigurationEnforcer#managePluginVersions} */
  private Boolean managePluginVersions;

  /** See {@link PedanticPluginConfigurationEnforcer#managePluginConfigurations} */
  private Boolean managePluginConfigurations;

  /** See {@link PedanticPluginConfigurationEnforcer#managePluginDependencies} */
  private Boolean managePluginDependencies;


  /** Collection of enforcers to execute. */
  private final Collection<PedanticEnforcerRule> enforcers;

  private final PropertyInitializationVisitor propertyInitializer;

  public CompoundPedanticEnforcer() {
    this.enforcers = Sets.newLinkedHashSet();
    this.propertyInitializer = new PropertyInitializationVisitor();
  }

  public void setEnforcers(String enforcers) {
    CommaSeparatorUtils.splitAndAddToCollection(enforcers, this.enforcers, new Function<String, PedanticEnforcerRule>() {
      @Override
      public PedanticEnforcerRule apply(String input) {
        return PedanticEnforcerRule.valueOf(input);
      }
    });
  }

  @Override
  public void execute(EnforcerRuleHelper helper) throws EnforcerRuleException {
    MavenProject project = EnforcerRuleUtils.getMavenProject(helper);
    Document pom = XmlUtils.parseXml(project.getFile());
    doEnforce(helper, pom);
  }

  @Override
  protected void doEnforce(EnforcerRuleHelper helper, Document pom) throws EnforcerRuleException {
    for (PedanticEnforcerRule pedanticEnforcer : this.enforcers) {
      AbstractPedanticEnforcer rule = pedanticEnforcer.createEnforcerRule();
      rule.accept(this.propertyInitializer);
      rule.doEnforce(helper, pom);
    }
  }

  @Override
  protected void accept(PedanticEnforcerVisitor visitor) {
    visitor.visit(this);
  }

  private class PropertyInitializationVisitor implements PedanticEnforcerVisitor {

    @Override
    public void visit(PedanticPomSectionOrderEnforcer enforcer) {
      if (!Strings.isNullOrEmpty(CompoundPedanticEnforcer.this.pomSectionPriorities)) {
        enforcer.setSectionPriorities(CompoundPedanticEnforcer.this.pomSectionPriorities);
      }
    }

    @Override
    public void visit(PedanticModuleOrderEnforcer enforcer) {
      if (!Strings.isNullOrEmpty(CompoundPedanticEnforcer.this.moduleOrderIgnores)) {
        enforcer.setIgnoredModules(CompoundPedanticEnforcer.this.moduleOrderIgnores);
      }
    }

    @Override
    public void visit(PedanticDependencyManagementOrderEnforcer enforcer) {
      if (!Strings.isNullOrEmpty(CompoundPedanticEnforcer.this.dependencyManagementOrderBy)) {
        enforcer.setOrderBy(CompoundPedanticEnforcer.this.dependencyManagementOrderBy);
      }
      if (!Strings.isNullOrEmpty(CompoundPedanticEnforcer.this.dependencyManagementGroupIdPriorities)) {
        enforcer.setGroupIdPriorities(CompoundPedanticEnforcer.this.dependencyManagementGroupIdPriorities);
      }
      if (!Strings.isNullOrEmpty(CompoundPedanticEnforcer.this.dependencyManagementArtifactIdPriorities)) {
        enforcer.setArtifactIdPriorities(CompoundPedanticEnforcer.this.dependencyManagementArtifactIdPriorities);
      }
      if (!Strings.isNullOrEmpty(CompoundPedanticEnforcer.this.dependencyManagementScopePriorities)) {
        enforcer.setScopePriorities(CompoundPedanticEnforcer.this.dependencyManagementScopePriorities);
      }
    }

    @Override
    public void visit(PedanticDependencyOrderEnforcer enforcer) {
      if (!Strings.isNullOrEmpty(CompoundPedanticEnforcer.this.dependenciesOrderBy)) {
        enforcer.setOrderBy(CompoundPedanticEnforcer.this.dependenciesOrderBy);
      }
      if (!Strings.isNullOrEmpty(CompoundPedanticEnforcer.this.dependenciesGroupIdPriorities)) {
        enforcer.setGroupIdPriorities(CompoundPedanticEnforcer.this.dependenciesGroupIdPriorities);
      }
      if (!Strings.isNullOrEmpty(CompoundPedanticEnforcer.this.dependenciesArtifactIdPriorities)) {
        enforcer.setArtifactIdPriorities(CompoundPedanticEnforcer.this.dependenciesArtifactIdPriorities);
      }
      if (!Strings.isNullOrEmpty(CompoundPedanticEnforcer.this.dependenciesScopePriorities)) {
        enforcer.setScopePriorities(CompoundPedanticEnforcer.this.dependenciesScopePriorities);
      }
    }

    @Override
    public void visit(PedanticDependencyConfigurationEnforcer dependencyConfigurationEnforcer) {
      if (CompoundPedanticEnforcer.this.manageDependencyVersions != null) {
        dependencyConfigurationEnforcer.setManageVersions(CompoundPedanticEnforcer.this.manageDependencyVersions);
      }
      if (CompoundPedanticEnforcer.this.allowUnmangedProjectVersions != null) {
        dependencyConfigurationEnforcer.setAllowUnmanagedProjectVersions(
            CompoundPedanticEnforcer.this.allowUnmangedProjectVersions);
      }
      if (CompoundPedanticEnforcer.this.manageDependencyExclusions != null) {
        dependencyConfigurationEnforcer.setManageExclusions(CompoundPedanticEnforcer.this.manageDependencyExclusions);
      }
    }

    @Override
    public void visit(PedanticDependencyScopeEnforcer dependencyScopeEnforcer) {
      if (CompoundPedanticEnforcer.this.compileDependencies != null) {
        dependencyScopeEnforcer.setCompileDependencies(CompoundPedanticEnforcer.this.compileDependencies);
      }
      if (CompoundPedanticEnforcer.this.providedDependencies != null) {
        dependencyScopeEnforcer.setProvidedDependencies(CompoundPedanticEnforcer.this.providedDependencies);
      }
      if (CompoundPedanticEnforcer.this.runtimeDependencies != null) {
        dependencyScopeEnforcer.setRuntimeDependencies(CompoundPedanticEnforcer.this.runtimeDependencies);
      }
      if (CompoundPedanticEnforcer.this.systemDependencies != null) {
        dependencyScopeEnforcer.setSystemDependencies(CompoundPedanticEnforcer.this.systemDependencies);
      }
      if (CompoundPedanticEnforcer.this.testDependencies != null) {
        dependencyScopeEnforcer.setTestDependencies(CompoundPedanticEnforcer.this.testDependencies);
      }
      if (CompoundPedanticEnforcer.this.importDependencies != null) {
        dependencyScopeEnforcer.setImportDependencies(CompoundPedanticEnforcer.this.importDependencies);
      }
    }

    @Override
    public void visit(PedanticPluginManagementOrderEnforcer enforcer) {
      if (!Strings.isNullOrEmpty(CompoundPedanticEnforcer.this.pluginManagementOrderBy)) {
        enforcer.setOrderBy(CompoundPedanticEnforcer.this.pluginManagementOrderBy);
      }
      if (!Strings.isNullOrEmpty(CompoundPedanticEnforcer.this.pluginManagementGroupIdPriorities)) {
        enforcer.setGroupIdPriorities(CompoundPedanticEnforcer.this.pluginManagementGroupIdPriorities);
      }
      if (!Strings.isNullOrEmpty(CompoundPedanticEnforcer.this.pluginManagementArtifactIdPriorities)) {
        enforcer.setArtifactIdPriorities(CompoundPedanticEnforcer.this.pluginManagementArtifactIdPriorities);
      }
    }

    @Override
    public void visit(PedanticPluginConfigurationEnforcer enforcer) {
      if (CompoundPedanticEnforcer.this.managePluginVersions != null) {
        enforcer.setManageVersions(CompoundPedanticEnforcer.this.managePluginVersions);
      }
      if (CompoundPedanticEnforcer.this.managePluginConfigurations != null) {
        enforcer.setManageConfigurations(CompoundPedanticEnforcer.this.managePluginConfigurations);
      }
      if (CompoundPedanticEnforcer.this.managePluginDependencies != null) {
        enforcer.setManageDependencies(CompoundPedanticEnforcer.this.managePluginDependencies);
      }
    }

    @Override
    public void visit(PedanticPluginManagementLocationEnforcer enforcer) {
      if (!Strings.isNullOrEmpty(CompoundPedanticEnforcer.this.pluginManagingPoms)) {
        enforcer.setPluginManagingPoms(CompoundPedanticEnforcer.this.pluginManagingPoms);
      }
    }

    @Override
    public void visit(CompoundPedanticEnforcer enforcer) {
      // nothing to do.
    }
  }

}
