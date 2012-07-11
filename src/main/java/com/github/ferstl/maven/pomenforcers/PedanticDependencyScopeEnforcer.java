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
import com.github.ferstl.maven.pomenforcers.artifact.DependencyMatcher;
import com.github.ferstl.maven.pomenforcers.reader.DeclaredDependenciesReader;
import com.github.ferstl.maven.pomenforcers.reader.XPathExpressions;
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
 */
public class PedanticDependencyScopeEnforcer extends AbstractPedanticEnforcer {

  private final Multimap<ArtifactInfo, DependencyScope> scopedDependencies;
  private final DependencyToArtifactInfoTransformer dependencyToArtifactInfoTransformer;

  public PedanticDependencyScopeEnforcer() {
    this.scopedDependencies = HashMultimap.create();
    this.dependencyToArtifactInfoTransformer = new DependencyToArtifactInfoTransformer();
  }

  public void setCompileDependencies(String compileDependencies) {
    addToArtifactinfoMap(createDependencyInfo(compileDependencies), COMPILE);
  }

  public void setProvidedDependencies(String providedDependencies) {
    addToArtifactinfoMap(createDependencyInfo(providedDependencies), PROVIDED);
  }

  public void setRuntimeDependencies(String runtimeDependencies) {
    addToArtifactinfoMap(createDependencyInfo(runtimeDependencies), RUNTIME);
  }

  public void setSystemDependencies(String systemDependencies) {
    addToArtifactinfoMap(createDependencyInfo(systemDependencies), SYSTEM);
  }

  public void setTestDependencies(String testDependencies) {
    addToArtifactinfoMap(createDependencyInfo(testDependencies), TEST);
  }

  public void setImportDependencies(String importDependencies) {
    addToArtifactinfoMap(createDependencyInfo(importDependencies), IMPORT);
  }

  @Override
  protected void doEnforce(EnforcerRuleHelper helper, Document pom) throws EnforcerRuleException {
    Log log = helper.getLog();
    log.info("Enforcing dependency scopes.");

    Collection<Dependency> dependencies = getDeclaredDependencies(pom, helper);

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

  private Collection<Dependency> getResolvedDependencies(EnforcerRuleHelper helper) {
    return EnforcerRuleUtils.getMavenProject(helper).getDependencies();
  }

  private Collection<Dependency> getDeclaredDependencies(Document pom, EnforcerRuleHelper helper) {
    Collection<Dependency> declaredDependencies =
        new DeclaredDependenciesReader(pom).read(XPathExpressions.POM_DEPENENCIES);
    Collection<Dependency> resolvedDependencies = getResolvedDependencies(helper);
    DependencyMatcher dependencyMatcher = new DependencyMatcher(resolvedDependencies, helper);
    return dependencyMatcher.match(declaredDependencies);
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
