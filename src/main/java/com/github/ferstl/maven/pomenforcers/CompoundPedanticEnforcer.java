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

/**
 * The compound enforcer aggregates any combination of the available pedantic
 * enforcer rules. Besides that it is easier to configure than the single rules,
 * it is also more efficient because it has to parse the POM file of each Maven
 * module only once.
 *
 * <pre>
 * ### Example
 *     <rules>
 *       <compound implementation="com.github.ferstl.maven.pomenforcers.CompoundPedanticEnforcer">
 *         <enforcers>POM_SECTION_ORDER,MODULE_ORDER,DEPENDENCY_MANAGEMENT_ORDER,DEPENDENCY_ORDER,DEPENDENCY_DONFIGURATION,DEPENDENCY_SCOPE,PLUGIN_MANAGEMENT_ORDER,PLUGIN_CONFIGURATION,PLUGIN_MANAGEMENT_LOCATION</enforcers>
 *         <pomSectionPriorities>roupId,artifactId,version,packaging</pomSectionPriorities>
 *
 *         <moduleOrderIgnores>>dist-deb,dist-rpm</moduleOrderIgnores>
 *
 *         <dependenciesOrderBy>scope,groupId,artifactId</dependenciesOrderBy>
 *         <dependenciesScopePriorities>compile,runtime,provided</dependenciesScopePriorities>
 *         <dependenciesGroupIdPriorities>com.myproject,com.mylibs</dependenciesGroupIdPriorities>
 *         <dependenciesArtifactIdPriorities>commons-,utils-</dependenciesArtifactIdPriorities>
 *
 *         <manageDependencyVersions>true</manageDependencyVersions>
 *         <allowUnmangedProjectVersions>true</allowUnmangedProjectVersions>
 *         <manageDependencyExclusions>true</manageDependencyExclusions>
 *
 *         <compileDependencies>com.example:mylib1,com.example:mylib2</compileDependencies>
 *         <providedDependencies>javax.servlet:servlet-api</providedDependencies>
 *         <runtimeDependencies>com.example:myruntimelib</runtimeDependencies>
 *         <systemDependencies>com.sun:tools</systemDependencies>
 *         <testDependencies>org.junit:junit,org.hamcrest:hamcrest-library</testDependencies>
 *         <importDependencies>org.jboss:jboss-as-client</importDependencies>
 *
 *         <dependencyManagementOrderBy>scope,groupId,artifactId</dependencyManagementOrderBy>
 *         <dependencyManagementScopePriorities>compile,runtime,provided</dependencyManagementScopePriorities>
 *         <dependencyManagementGroupIdPriorities>com.myproject,com.mylibs</dependencyManagementGroupIdPriorities>
 *         <dependencyManagementArtifactIdPriorities>commons-,utils-</dependencyManagementArtifactIdPriorities>
 *
 *         <pluginManagementOrderBy>groupId,artifactId</pluginManagementOrderBy>
 *         <pluginManagementGroupIdPriorities>com.myproject.plugins,com.myproject.testplugins</pluginManagementGroupIdPriorities>
 *         <pluginManagementArtifactIdPriorities>mytest-,myintegrationtest-</pluginManagementArtifactIdPriorities>
 *
 *         <managePluginVersions>true</managePluginVersions>
 *         <managePluginConfigurations>true</managePluginConfigurations>
 *         <managePluginDependencies>true</managePluginDependencies>
 *
 *         <pluginManagingPoms>com.myproject:parent-pom</pluginManagingPoms>
 *       </compound>
 *     </rules>
 * @id n/a
 */
public class CompoundPedanticEnforcer extends AbstractPedanticEnforcer {

  /**
   * See {@link PedanticPomSectionOrderEnforcer#setSectionPriorities(String)}.
   * @configParam
   */
  private String pomSectionPriorities;

  /**
   * See {@link PedanticModuleOrderEnforcer#setIgnoredModules(String)}.
   * @configParam
   */
  private String moduleOrderIgnores;

  /**
   * See {@link PedanticDependencyOrderEnforcer#setOrderBy(String)}.
   * @configParam
   */
  private String dependenciesOrderBy;

  /**
   * See {@link PedanticDependencyOrderEnforcer#setGroupIdPriorities(String)}.
   * @configParam
   */
  private String dependenciesGroupIdPriorities;

  /**
   * See {@link PedanticDependencyOrderEnforcer#setArtifactIdPriorities(String)}.
   * @configParam
   */
  private String dependenciesArtifactIdPriorities;

  /**
   * See {@link PedanticDependencyOrderEnforcer#setScopePriorities(String)}.
   * @configParam
   */
  private String dependenciesScopePriorities;

  /**
   * See {@link PedanticDependencyManagementOrderEnforcer#setOrderBy(String)}.
   * @configParam
   */
  private String dependencyManagementOrderBy;

  /**
   * See
   * {@link PedanticDependencyManagementOrderEnforcer#setGroupIdPriorities(String)}.
   * @configParam
   */
  private String dependencyManagementGroupIdPriorities;

  /**
   * See
   * {@link PedanticDependencyManagementOrderEnforcer#setArtifactIdPriorities(String)}.
   * @configParam
   */
  private String dependencyManagementArtifactIdPriorities;

  /**
   * See
   * {@link PedanticDependencyManagementOrderEnforcer#setScopePriorities(String)}.
   * @configParam
   */
  private String dependencyManagementScopePriorities;

  /**
   * See
   * {@link PedanticDependencyConfigurationEnforcer#setManageVersions(boolean)}.
   * @configParam
   */
  private Boolean manageDependencyVersions;

  /**
   * See
   * {@link PedanticDependencyConfigurationEnforcer#setAllowUnmanagedProjectVersions(boolean)}.
   * @configParam
   */
  private Boolean allowUnmangedProjectVersions;

  /**
   * See
   * {@link PedanticDependencyConfigurationEnforcer#setManageExclusions(boolean)}.
   * @configParam
   */
  private Boolean manageDependencyExclusions;

  /**
   * See {@link PedanticDependencyScopeEnforcer#setCompileDependencies(String)}.
   * @configParam
   */
  private String compileDependencies;

  /**
   * See {@link PedanticDependencyScopeEnforcer#setProvidedDependencies(String)}.
   * @configParam
   */
  private String providedDependencies;

  /**
   * See {@link PedanticDependencyScopeEnforcer#setRuntimeDependencies(String)}.
   * @configParam
   */
  private String runtimeDependencies;

  /**
   * See {@link PedanticDependencyScopeEnforcer#setSystemDependencies(String)}.
   * @configParam
   */
  private String systemDependencies;

  /**
   * See {@link PedanticDependencyScopeEnforcer#setTestDependencies(String)}.
   * @configParam
   */
  private String testDependencies;

  /**
   * See {@link PedanticDependencyScopeEnforcer#setImportDependencies(String)}.
   * @configParam
   */
  private String importDependencies;

  /**
   * See {@link PedanticPluginManagementOrderEnforcer#setOrderBy(String)}.
   * @configParam
   */
  private String pluginManagementOrderBy;

  /**
   * See
   * {@link PedanticPluginManagementOrderEnforcer#setGroupIdPriorities(String)}.
   * @configParam
   */
  private String pluginManagementGroupIdPriorities;

  /**
   * See
   * {@link PedanticPluginManagementOrderEnforcer#setArtifactIdPriorities(String)}.
   * @configParam
   */
  private String pluginManagementArtifactIdPriorities;

  /**
   * See
   * {@link PedanticPluginManagementLocationEnforcer#setPluginManagingPoms(String)}.
   * @configParam
   */
  private String pluginManagingPoms;

  /**
   * See {@link PedanticPluginConfigurationEnforcer#managePluginVersions}.
   * @configParam
   */
  private Boolean managePluginVersions;

  /**
   * See {@link PedanticPluginConfigurationEnforcer#managePluginConfigurations}
   * @configParam
   */
  private Boolean managePluginConfigurations;

  /**
   * See {@link PedanticPluginConfigurationEnforcer#managePluginDependencies}
   * @configParam
   */
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
