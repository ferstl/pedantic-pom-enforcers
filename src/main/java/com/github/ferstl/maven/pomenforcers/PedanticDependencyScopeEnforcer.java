package com.github.ferstl.maven.pomenforcers;

import java.util.Collection;
import java.util.Set;

import org.apache.maven.enforcer.rule.api.EnforcerRuleException;
import org.apache.maven.enforcer.rule.api.EnforcerRuleHelper;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.logging.Log;
import org.w3c.dom.Document;

import com.github.ferstl.maven.pomenforcers.artifact.Artifact;
import com.github.ferstl.maven.pomenforcers.artifact.StringToArtifactTransformer;
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
 *     &lt;rules&gt;
 *       &lt;dependencyScope implementation=&quot;ch.sferstl.maven.pomenforcer.PedanticDependencyScopeEnforcer&quot;&gt;
 *         &lt;!-- These dependencies can only be defined in test scope --&gt;
 *         &lt;testDependencies&gt;junit:junit,org.hamcrest:hamcrest-library,org.mockito:mockito-core&lt;/testDependencies&gt;
 *
 *         &lt;!-- These dependencies can only be defined in provided scope --&gt;
 *         &lt;providedDependencies&gt;javax.servlet:servlet-api&lt;/providedDependencies&gt;
 *       &lt;/dependencyScope&gt;
 *     &lt;/rules&gt;
 * </pre>
 *
 * @id {@link PedanticEnforcerRule#DEPENDENCY_SCOPE}
 */
public class PedanticDependencyScopeEnforcer extends AbstractPedanticEnforcer {

  private final Multimap<Artifact, DependencyScope> scopedDependencies;
  private final DependencyToArtifactTransformer dependencyToArtifactTransformer;

  public PedanticDependencyScopeEnforcer() {
    this.scopedDependencies = HashMultimap.create();
    this.dependencyToArtifactTransformer = new DependencyToArtifactTransformer();
  }

  /**
   * Comma-separated list of <code>compile</code> scope dependencies in the format <code>groupId:artifactId</code>.
   * @param compileDependencies Comma-separated list of <code>compile</code> scope dependencies.
   * @configParam
   */
  public void setCompileDependencies(String compileDependencies) {
    addToArtifactMap(createDependencyInfo(compileDependencies), COMPILE);
  }

  /**
   * Comma-separated list of <code>provided</code> scope dependencies in the format <code>groupId:artifactId</code>.
   * @param providedDependencies Comma-separated list of <code>provided</code> scope dependencies.
   * @configParam
   */
  public void setProvidedDependencies(String providedDependencies) {
    addToArtifactMap(createDependencyInfo(providedDependencies), PROVIDED);
  }

  /**
   * Comma-separated list of <code>runtime</code> scope dependencies in the format <code>groupId:artifactId</code>.
   * @param runtimeDependencies Comma-separated list of <code>runtime</code> scope dependencies.
   * @configParam
   */
  public void setRuntimeDependencies(String runtimeDependencies) {
    addToArtifactMap(createDependencyInfo(runtimeDependencies), RUNTIME);
  }

  /**
   * Comma-separated list of <code>system</code> scope dependencies in the format <code>groupId:artifactId</code>.
   * @param systemDependencies Comma-separated list of <code>system</code> scope dependencies.
   * @configParam
   */
  public void setSystemDependencies(String systemDependencies) {
    addToArtifactMap(createDependencyInfo(systemDependencies), SYSTEM);
  }

  /**
   * Comma-separated list of <code>test</code> scope dependencies in the format <code>groupId:artifactId</code>.
   * @param testDependencies Comma-separated list of <code>test</code> scope dependencies.
   * @configParam
   */
  public void setTestDependencies(String testDependencies) {
    addToArtifactMap(createDependencyInfo(testDependencies), TEST);
  }

  /**
   * Comma-separated list of <code>import</code> scope dependencies in the format <code>groupId:artifactId</code>.
   * @param importDependencies Comma-separated list of <code>import</code> scope dependencies.
   * @configParam
   */
  public void setImportDependencies(String importDependencies) {
    addToArtifactMap(createDependencyInfo(importDependencies), IMPORT);
  }

  @Override
  protected void doEnforce(EnforcerRuleHelper helper, Document pom) throws EnforcerRuleException {
    Log log = helper.getLog();
    log.info("Enforcing dependency scopes.");

    Collection<Dependency> dependencies = EnforcerRuleUtils.getMavenProject(helper).getDependencies();

    for (Dependency dependency : dependencies) {
      Artifact artifact = this.dependencyToArtifactTransformer.apply(dependency);
      Collection<DependencyScope> allowedScopes = this.scopedDependencies.get(artifact);
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

  private Set<Artifact> createDependencyInfo(String dependencies) {
    Set<Artifact> dependencyInfoSet = Sets.newHashSet();
    CommaSeparatorUtils.splitAndAddToCollection(dependencies, dependencyInfoSet, new StringToArtifactTransformer());

    return dependencyInfoSet;
  }

  private void addToArtifactMap(Iterable<Artifact> artifacts, DependencyScope scope) {
    for (Artifact artifact : artifacts) {
      this.scopedDependencies.put(artifact, scope);
    }
  }

  private DependencyScope getScope(Dependency dependency) {
    if (dependency.getScope() == null) {
      return COMPILE;
    }
    return DependencyScope.getByScopeName(dependency.getScope());
  }

  private static class DependencyToArtifactTransformer implements Function<Dependency, Artifact> {

    @Override
    public Artifact apply(Dependency input) {
      return new Artifact(input.getGroupId(), input.getArtifactId());
    }

  }
}
