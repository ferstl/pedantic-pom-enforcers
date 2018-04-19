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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.github.ferstl.maven.pomenforcers.util.CommaSeparatorUtils;
import com.google.common.base.Strings;
import com.google.common.collect.Sets;

import static com.github.ferstl.maven.pomenforcers.PedanticEnforcerRule.stringToEnforcerRule;

/**
 * The compound enforcer aggregates any combination of the available pedantic
 * enforcer rules. Besides that it is easier to configure than the single rules,
 * it is also more efficient because it has to parse the POM file of each Maven
 * module only once.
 *
 * <pre>
 * ### Example
 *     &lt;rules&gt;
 *       &lt;compound implementation=&quot;com.github.ferstl.maven.pomenforcers.CompoundPedanticEnforcer&quot;&gt;
 *         &lt;enforcers&gt;POM_SECTION_ORDER,MODULE_ORDER,DEPENDENCY_MANAGEMENT_ORDER,DEPENDENCY_ORDER,DEPENDENCY_CONFIGURATION,DEPENDENCY_SCOPE,DEPENDENCY_MANAGEMENT_LOCATION,PLUGIN_MANAGEMENT_ORDER,PLUGIN_CONFIGURATION,PLUGIN_MANAGEMENT_LOCATION&lt;/enforcers&gt;
 *
 *         &lt;!-- POM_SECTION configuration --&gt;
 *         &lt;pomSectionPriorities&gt;groupId,artifactId,version,packaging&lt;/pomSectionPriorities&gt;
 *
 *         &lt;!-- MODULE_ORDER configuration --&gt;
 *         &lt;moduleOrderIgnores&gt;&gt;dist-deb,dist-rpm&lt;/moduleOrderIgnores&gt;
 *
 *         &lt;!-- DEPENDENCY_ORDER configuration --&gt;
 *         &lt;dependenciesOrderBy&gt;scope,groupId,artifactId&lt;/dependenciesOrderBy&gt;
 *         &lt;dependenciesScopePriorities&gt;compile,runtime,provided&lt;/dependenciesScopePriorities&gt;
 *         &lt;dependenciesGroupIdPriorities&gt;com.myproject,com.mylibs&lt;/dependenciesGroupIdPriorities&gt;
 *         &lt;dependenciesArtifactIdPriorities&gt;commons-,utils-&lt;/dependenciesArtifactIdPriorities&gt;
 *
 *         &lt;!-- DEPENDENCY_MANAGEMENT_ORDER configuration --&gt;
 *         &lt;dependencyManagementOrderBy&gt;scope,groupId,artifactId&lt;/dependencyManagementOrderBy&gt;
 *         &lt;dependencyManagementScopePriorities&gt;compile,runtime,provided&lt;/dependencyManagementScopePriorities&gt;
 *         &lt;dependencyManagementGroupIdPriorities&gt;com.myproject,com.mylibs&lt;/dependencyManagementGroupIdPriorities&gt;
 *         &lt;dependencyManagementArtifactIdPriorities&gt;commons-,utils-&lt;/dependencyManagementArtifactIdPriorities&gt;
 *
 *         &lt;!-- DEPENDENCY_CONFIGURATION configuration --&gt;
 *         &lt;manageDependencyVersions&gt;true&lt;/manageDependencyVersions&gt;
 *         &lt;allowUnmangedProjectVersions&gt;true&lt;/allowUnmangedProjectVersions&gt;
 *         &lt;manageDependencyExclusions&gt;true&lt;/manageDependencyExclusions&gt;
 *
 *         &lt;!-- DEPENDENCY_SCOPE configuration --&gt;
 *         &lt;compileDependencies&gt;com.example:mylib1,com.example:mylib2&lt;/compileDependencies&gt;
 *         &lt;providedDependencies&gt;javax.servlet:servlet-api&lt;/providedDependencies&gt;
 *         &lt;runtimeDependencies&gt;com.example:myruntimelib&lt;/runtimeDependencies&gt;
 *         &lt;systemDependencies&gt;com.sun:tools&lt;/systemDependencies&gt;
 *         &lt;testDependencies&gt;org.junit:junit,org.hamcrest:hamcrest-library&lt;/testDependencies&gt;
 *         &lt;importDependencies&gt;org.jboss:jboss-as-client&lt;/importDependencies&gt;
 *
 *         &lt;!-- DEPENDENCY_MANAGEMENT_LOCATION configuration --&gt;
 *         &lt;allowParentPomsForDependencyManagement&gt;true&lt;/allowParentPomsForDependencyManagement&gt;
 *         &lt;dependencyManagingPoms&gt;com.example.myproject:parent,com.example.myproject:subparent&lt;/dependencyManagingPoms&gt;
 *
 *         &lt;!-- PLUGIN_MANAGEMENT_ORDER configuration --&gt;
 *         &lt;pluginManagementOrderBy&gt;groupId,artifactId&lt;/pluginManagementOrderBy&gt;
 *         &lt;pluginManagementGroupIdPriorities&gt;com.myproject.plugins,com.myproject.testplugins&lt;/pluginManagementGroupIdPriorities&gt;
 *         &lt;pluginManagementArtifactIdPriorities&gt;mytest-,myintegrationtest-&lt;/pluginManagementArtifactIdPriorities&gt;
 *
 *         &lt;!-- PLUGIN_CONFIGURATION configuration --&gt;
 *         &lt;managePluginVersions&gt;true&lt;/managePluginVersions&gt;
 *         &lt;managePluginConfigurations&gt;true&lt;/managePluginConfigurations&gt;
 *         &lt;managePluginDependencies&gt;true&lt;/managePluginDependencies&gt;
 *
 *         &lt;!-- PLUGIN_MANAGEMENT_LOCATION configuration --&gt;
 *         &lt;allowParentPomsForPluginManagement&gt;true&lt;/allowParentPomsForPluginManagement&gt;
 *         &lt;pluginManagingPoms&gt;com.myproject:parent-pom&lt;/pluginManagingPoms&gt;
 *         &lt;!-- DEPENDENCY_ELEMENT --&gt;
 *         &lt;dependencyElementOrdering&gt;groupId,artifactid,version&lt;/dependencyElementOrdering&gt;
 *         &lt;checkDependencyElements&gt;true&lt;/heckDependencyElements&gt;
 *         &lt;checkDependencyManagementElements&gt;true&lt;/checkDependencyManagementElements&gt;
 *       &lt;/compound&gt;
 *     &lt;/rules&gt;
 * </pre>
 * @id n/a
 * @since 1.0.0
 */
public class CompoundPedanticEnforcer extends AbstractPedanticEnforcer {

  /**
   * See {@link PedanticPomSectionOrderEnforcer#setSectionPriorities(String)}.
   * @configParam
   * @since 1.0.0
   */
  private String pomSectionPriorities;

  /**
   * See {@link PedanticModuleOrderEnforcer#setIgnoredModules(String)}.
   * @configParam
   * @since 1.0.0
   */
  private String moduleOrderIgnores;

  /**
   * See {@link PedanticDependencyOrderEnforcer#setOrderBy(String)}.
   * @configParam
   * @since 1.0.0
   */
  private String dependenciesOrderBy;

  /**
   * See {@link PedanticDependencyOrderEnforcer#setGroupIdPriorities(String)}.
   * @configParam
   * @since 1.0.0
   */
  private String dependenciesGroupIdPriorities;

  /**
   * See {@link PedanticDependencyOrderEnforcer#setArtifactIdPriorities(String)}.
   * @configParam
   * @since 1.0.0
   */
  private String dependenciesArtifactIdPriorities;

  /**
   * See {@link PedanticDependencyOrderEnforcer#setScopePriorities(String)}.
   * @configParam
   * @since 1.0.0
   */
  private String dependenciesScopePriorities;

  /**
   * See {@link PedanticDependencyManagementOrderEnforcer#setOrderBy(String)}.
   * @configParam
   * @since 1.0.0
   */
  private String dependencyManagementOrderBy;

  /**
   * See
   * {@link PedanticDependencyManagementOrderEnforcer#setGroupIdPriorities(String)}.
   * @configParam
   * @since 1.0.0
   */
  private String dependencyManagementGroupIdPriorities;

  /**
   * See
   * {@link PedanticDependencyManagementOrderEnforcer#setArtifactIdPriorities(String)}.
   * @configParam
   * @since 1.0.0
   */
  private String dependencyManagementArtifactIdPriorities;

  /**
   * See
   * {@link PedanticDependencyManagementOrderEnforcer#setScopePriorities(String)}.
   * @configParam
   * @since 1.0.0
   */
  private String dependencyManagementScopePriorities;

  /**
   * See {@link PedanticDependencyManagementLocationEnforcer#setAllowParentPoms(boolean)}.
   * @configParam
   * @since 1.2.0
   */
  private Boolean allowParentPomsForDependencyManagement;

  /**
   * See {@link PedanticDependencyManagementLocationEnforcer#setDependencyManagingPoms(String)}.
   * @configParam
   * @since 1.0.0
   */
  private String dependencyManagingPoms;

  /**
   * See
   * {@link PedanticDependencyConfigurationEnforcer#setManageVersions(boolean)}.
   * @configParam
   * @since 1.0.0
   */
  private Boolean manageDependencyVersions;

  /**
   * See
   * {@link PedanticDependencyConfigurationEnforcer#setAllowUnmanagedProjectVersions(boolean)}.
   * @configParam
   * @since 1.0.0
   */
  private Boolean allowUnmangedProjectVersions;

  /**
   * See
   * {@link PedanticDependencyConfigurationEnforcer#setManageExclusions(boolean)}.
   * @configParam
   * @since 1.0.0
   */
  private Boolean manageDependencyExclusions;

  /**
   * See {@link PedanticDependencyScopeEnforcer#setCompileDependencies(String)}.
   * @configParam
   * @since 1.0.0
   */
  private String compileDependencies;

  /**
   * See {@link PedanticDependencyScopeEnforcer#setProvidedDependencies(String)}.
   * @configParam
   * @since 1.0.0
   */
  private String providedDependencies;

  /**
   * See {@link PedanticDependencyScopeEnforcer#setRuntimeDependencies(String)}.
   * @configParam
   * @since 1.0.0
   */
  private String runtimeDependencies;

  /**
   * See {@link PedanticDependencyScopeEnforcer#setSystemDependencies(String)}.
   * @configParam
   * @since 1.0.0
   */
  private String systemDependencies;

  /**
   * See {@link PedanticDependencyScopeEnforcer#setTestDependencies(String)}.
   * @configParam
   * @since 1.0.0
   */
  private String testDependencies;

  /**
   * See {@link PedanticDependencyScopeEnforcer#setImportDependencies(String)}.
   * @configParam
   * @since 1.0.0
   */
  private String importDependencies;

  /**
   * See {@link PedanticPluginManagementOrderEnforcer#setOrderBy(String)}.
   * @configParam
   * @since 1.0.0
   */
  private String pluginManagementOrderBy;

  /**
   * See
   * {@link PedanticPluginManagementOrderEnforcer#setGroupIdPriorities(String)}.
   * @configParam
   * @since 1.0.0
   */
  private String pluginManagementGroupIdPriorities;

  /**
   * See
   * {@link PedanticPluginManagementOrderEnforcer#setArtifactIdPriorities(String)}.
   * @configParam
   * @since 1.0.0
   */
  private String pluginManagementArtifactIdPriorities;


  /**
   * See {@link PedanticPluginManagementLocationEnforcer#setAllowParentPoms(boolean)}.
   * @configParam
   * @since 1.2.0
   */
  private Boolean allowParentPomsForPluginManagement;

  /**
   * See
   * {@link PedanticPluginManagementLocationEnforcer#setPluginManagingPoms(String)}.
   * @configParam
   * @since 1.0.0
   */
  private String pluginManagingPoms;

  /**
   * See {@link PedanticPluginConfigurationEnforcer#managePluginVersions}.
   * @configParam
   * @since 1.0.0
   */
  private Boolean managePluginVersions;

  /**
   * See {@link PedanticPluginConfigurationEnforcer#managePluginConfigurations}
   * @configParam
   * @since 1.0.0
   */
  private Boolean managePluginConfigurations;

  /**
   * See {@link PedanticPluginConfigurationEnforcer#managePluginDependencies}
   * @configParam
   * @since 1.0.0
   */
  private Boolean managePluginDependencies;

  /**
   * See {@link PedanticDependencyElementEnforcer#elementOrdering}.
   *
   * @configParam
   * @since 1.4.0
   */
  private String dependencyElementOrdering;

  /**
   * See {@link PedanticDependencyElementEnforcer#checkDependencies}.
   *
   * @configParam
   * @since 1.4.0
   */
  private Boolean checkDependencyElements;

  /**
   * See {@link PedanticDependencyElementEnforcer#checkDependencyManagement}.
   *
   * @configParam
   * @since 1.4.0
   */
  private Boolean checkDependencyManagementElements;

  /**
   * Collection of enforcers to execute.
   */
  private final Collection<PedanticEnforcerRule> enforcers;

  private final PropertyInitializationVisitor propertyInitializer;

  public CompoundPedanticEnforcer() {
    this.enforcers = Sets.newLinkedHashSet();
    this.propertyInitializer = new PropertyInitializationVisitor();
  }

  public void setEnforcers(String enforcers) {
    CommaSeparatorUtils.splitAndAddToCollection(enforcers, this.enforcers, stringToEnforcerRule());
  }

  @Override
  protected PedanticEnforcerRule getDescription() {
    return PedanticEnforcerRule.COMPOUND;
  }

  @Override
  protected void accept(PedanticEnforcerVisitor visitor) {
    visitor.visit(this);
  }

  @Override
  protected void doEnforce(ErrorReport report) {
    report.useLargeTitle();

    List<ErrorReport> ruleErrors = new ArrayList<>();
    for (PedanticEnforcerRule pedanticEnforcer : this.enforcers) {
      AbstractPedanticEnforcer rule = pedanticEnforcer.createEnforcerRule();
      rule.initialize(getHelper(), getPom(), getProjectModel());
      rule.accept(this.propertyInitializer);

      ErrorReport ruleReport = new ErrorReport(rule.getDescription());
      rule.doEnforce(ruleReport);

      if (ruleReport.hasErrors()) {
        ruleErrors.add(ruleReport);
      }
    }
    collectErrors(report, ruleErrors);
  }

  private void collectErrors(ErrorReport compundReport, List<ErrorReport> ruleErrors) {
    if (!ruleErrors.isEmpty()) {
      compundReport
        .useLargeTitle()
        .addLine("Please fix these problems:")
        .emptyLine();
      for (ErrorReport ruleError : ruleErrors) {
        compundReport.addLine(ruleError.toString()).emptyLine().emptyLine();
      }
    }
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
    public void visit(PedanticDependencyManagementLocationEnforcer enforcer) {
      if(CompoundPedanticEnforcer.this.allowParentPomsForDependencyManagement != null) {
        enforcer.setAllowParentPoms(CompoundPedanticEnforcer.this.allowParentPomsForDependencyManagement);
      }
      if (!Strings.isNullOrEmpty(CompoundPedanticEnforcer.this.dependencyManagingPoms)) {
        enforcer.setDependencyManagingPoms(CompoundPedanticEnforcer.this.dependencyManagingPoms);
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
      if (CompoundPedanticEnforcer.this.allowParentPomsForPluginManagement != null) {
        enforcer.setAllowParentPoms(CompoundPedanticEnforcer.this.allowParentPomsForPluginManagement);
      }
      if (!Strings.isNullOrEmpty(CompoundPedanticEnforcer.this.pluginManagingPoms)) {
        enforcer.setPluginManagingPoms(CompoundPedanticEnforcer.this.pluginManagingPoms);
      }
    }

    @Override
    public void visit(PedanticDependencyElementEnforcer enforcer) {
      if (CompoundPedanticEnforcer.this.dependencyElementOrdering != null) {
        enforcer.setElementPriorities(CompoundPedanticEnforcer.this.dependencyElementOrdering);
      }
      if (CompoundPedanticEnforcer.this.checkDependencyElements != null) {
        enforcer.setCheckDependencies(CompoundPedanticEnforcer.this.checkDependencyElements);
      }
      if (CompoundPedanticEnforcer.this.checkDependencyManagementElements != null) {
        enforcer.setCheckDependencyManagement(CompoundPedanticEnforcer.this.checkDependencyManagementElements);
      }
    }

    @Override
    public void visit(CompoundPedanticEnforcer enforcer) {
      // nothing to do.
    }
  }

}
