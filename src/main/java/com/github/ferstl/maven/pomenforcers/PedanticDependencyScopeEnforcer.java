package com.github.ferstl.maven.pomenforcers;

import java.util.Collection;
import java.util.Set;

import org.apache.maven.enforcer.rule.api.EnforcerRuleException;
import org.apache.maven.enforcer.rule.api.EnforcerRuleHelper;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.logging.Log;
import org.w3c.dom.Document;

import com.github.ferstl.maven.pomenforcers.artifact.ArtifactInfo;
import com.github.ferstl.maven.pomenforcers.artifact.ArtifactInfoTransformer;
import com.github.ferstl.maven.pomenforcers.util.CommaSeparatorUtils;
import com.github.ferstl.maven.pomenforcers.util.EnforcerRuleUtils;
import com.google.common.base.Function;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

import static com.github.ferstl.maven.pomenforcers.DependencyScope.COMPILE;
import static com.github.ferstl.maven.pomenforcers.DependencyScope.IMPORT;
import static com.github.ferstl.maven.pomenforcers.DependencyScope.PROVIDED;
import static com.github.ferstl.maven.pomenforcers.DependencyScope.RUNTIME;
import static com.github.ferstl.maven.pomenforcers.DependencyScope.SYSTEM;
import static com.github.ferstl.maven.pomenforcers.DependencyScope.TEST;

/**
 * Enforces that the configured dependencies have to be defined within a specific scope.
 * <pre>
 * ### Example
 *     <rules>
 *       <dependencyScope implementation="ch.sferstl.maven.pomenforcer.PedanticDependencyScopeEnforcer">
 *         <!-- These dependencies can only be defined in test scope -->
 *         <testDependencies>junit:junit,org.hamcrest:hamcrest-library,org.mockito:mockito-core</testDependencies>
 *
 *         <!-- These dependencies can only be defined in provided scope -->
 *         <providedDependencies>javax.servlet:servlet-api</providedDependencies>
 *       </dependencyScope>
 *     </rules>
 * </pre>
 *
 * @id {@link PedanticEnforcerRule#DEPENDENCY_SCOPE}
 */
public class PedanticDependencyScopeEnforcer extends AbstractPedanticEnforcer {

  private final Multimap<ArtifactInfo, DependencyScope> scopedDependencies;
  private final DependencyToArtifactInfoTransformer dependencyToArtifactInfoTransformer;

  public PedanticDependencyScopeEnforcer() {
    this.scopedDependencies = HashMultimap.create();
    this.dependencyToArtifactInfoTransformer = new DependencyToArtifactInfoTransformer();
  }

  /**
   * Comma-separated list of <code>compile</code> scope dependencies in the format <code>groupId:artifactId</code>.
   * @param compileDependencies Comma-separated list of <code>compile</code> scope dependencies.
   * @configParam
   */
  public void setCompileDependencies(String compileDependencies) {
    addToArtifactinfoMap(createDependencyInfo(compileDependencies), COMPILE);
  }

  /**
   * Comma-separated list of <code>provided</code> scope dependencies in the format <code>groupId:artifactId</code>.
   * @param compileDependencies Comma-separated list of <code>provided</code> scope dependencies.
   * @configParam
   */
  public void setProvidedDependencies(String providedDependencies) {
    addToArtifactinfoMap(createDependencyInfo(providedDependencies), PROVIDED);
  }

  /**
   * Comma-separated list of <code>runtime</code> scope dependencies in the format <code>groupId:artifactId</code>.
   * @param compileDependencies Comma-separated list of <code>runtime</code> scope dependencies.
   * @configParam
   */
  public void setRuntimeDependencies(String runtimeDependencies) {
    addToArtifactinfoMap(createDependencyInfo(runtimeDependencies), RUNTIME);
  }

  /**
   * Comma-separated list of <code>system</code> scope dependencies in the format <code>groupId:artifactId</code>.
   * @param compileDependencies Comma-separated list of <code>system</code> scope dependencies.
   * @configParam
   */
  public void setSystemDependencies(String systemDependencies) {
    addToArtifactinfoMap(createDependencyInfo(systemDependencies), SYSTEM);
  }

  /**
   * Comma-separated list of <code>test</code> scope dependencies in the format <code>groupId:artifactId</code>.
   * @param compileDependencies Comma-separated list of <code>test</code> scope dependencies.
   * @configParam
   */
  public void setTestDependencies(String testDependencies) {
    addToArtifactinfoMap(createDependencyInfo(testDependencies), TEST);
  }

  /**
   * Comma-separated list of <code>import</code> scope dependencies in the format <code>groupId:artifactId</code>.
   * @param compileDependencies Comma-separated list of <code>import</code> scope dependencies.
   * @configParam
   */
  public void setImportDependencies(String importDependencies) {
    addToArtifactinfoMap(createDependencyInfo(importDependencies), IMPORT);
  }

  @Override
  protected void doEnforce(EnforcerRuleHelper helper, Document pom) throws EnforcerRuleException {
    Log log = helper.getLog();
    log.info("Enforcing dependency scopes.");

    Collection<Dependency> dependencies = EnforcerRuleUtils.getMavenProject(helper).getDependencies();

    for (Dependency dependency : dependencies) {
      ArtifactInfo artifactInfo = this.dependencyToArtifactInfoTransformer.apply(dependency);
      Collection<DependencyScope> allowedScopes = this.scopedDependencies.get(artifactInfo);
      DependencyScope dependencyScope = getScope(dependency);

      if (allowedScopes.size() > 0 && !allowedScopes.contains(dependencyScope)) {
        throw new EnforcerRuleException("One does not simply declare '" + dependencyScope.getScopeName() +
            "' scoped dependencies! Dependency " + dependency + " has to be declared in these scopes: " +
            allowedScopes);
      }
    }

  }

  @Override
  protected void accept(PedanticEnforcerVisitor visitor) {
    visitor.visit(this);
  }

  private Set<ArtifactInfo> createDependencyInfo(String dependencies) {
    Set<ArtifactInfo> dependencyInfoSet = Sets.newHashSet();
    CommaSeparatorUtils.splitAndAddToCollection(dependencies, dependencyInfoSet, new ArtifactInfoTransformer());

    return dependencyInfoSet;
  }

  private void addToArtifactinfoMap(Iterable<ArtifactInfo> artifactInfos, DependencyScope scope) {
    for (ArtifactInfo artifactInfo : artifactInfos) {
      this.scopedDependencies.put(artifactInfo, scope);
    }
  }

  private DependencyScope getScope(Dependency dependency) {
    if (dependency.getScope() == null) {
      return COMPILE;
    }
    return DependencyScope.getByScopeName(dependency.getScope());
  }

  private class DependencyToArtifactInfoTransformer implements Function<Dependency, ArtifactInfo> {

    @Override
    public ArtifactInfo apply(Dependency input) {
      return new ArtifactInfo(input.getGroupId(), input.getArtifactId());
    }

  }
}
