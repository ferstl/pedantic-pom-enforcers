package ch.sferstl.maven.pomenforcer;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.maven.model.Dependency;

import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.collect.Collections2;
import com.google.common.collect.Sets;

import ch.sferstl.maven.pomenforcer.artifact.ArtifactSorter;
import ch.sferstl.maven.pomenforcer.artifact.DependencyElement;

public abstract class AbstractPedanticDependencyOrderEnforcer extends AbstractPedanticEnforcer {

  private final ArtifactSorter<Dependency, DependencyElement> artifactSorter;

  public AbstractPedanticDependencyOrderEnforcer() {
    Set<DependencyElement> orderBy = Sets.newLinkedHashSet();
    orderBy.add(DependencyElement.SCOPE);
    orderBy.add(DependencyElement.GROUP_ID);
    orderBy.add(DependencyElement.ARTIFACT_ID);
    this.artifactSorter = new ArtifactSorter<>();
    this.artifactSorter.orderBy(orderBy);
  }

  public void setOrderBy(String dependencyElements) {
    Set<DependencyElement> orderBy = Sets.newLinkedHashSet();
    Function<String, DependencyElement> transformer = new Function<String, DependencyElement>() {
      @Override
      public DependencyElement apply(String input) {
        return DependencyElement.getByElementName(input);
      }
    };
    this.splitAndAddToCollection(dependencyElements, orderBy, transformer);
    this.artifactSorter.orderBy(orderBy);
  }

  /**
   * Sets the group IDs that should be listed first in the dependencies declaration. All group IDs
   * that <strong>start with</strong> any of the priorized group IDs in the given list, are required
   * to be located first in the dependencies section.
   * @param groupIds Comma separated list of group IDs.
   */
  public void setGroupIdPriorities(String groupIds) {
    LinkedHashSet<String> groupIdPriorities = Sets.newLinkedHashSet();
    this.splitAndAddToCollection(groupIds, groupIdPriorities);
    this.artifactSorter.setPriorities(DependencyElement.GROUP_ID, groupIdPriorities);
  }

  /**
   * Sets the artifact IDs that should be listed first in the dependencies declaration. All artifact
   * IDs that <strong>start with</strong> any of the priorized IDs in the given list, are required
   * to be located first in the dependencies section.
   * @param artifactIds Comma separated list of artifact IDs.
   */
  public void setArtifactIdPriorities(String artifactIds) {
    LinkedHashSet<String> artifactIdPriorities = Sets.newLinkedHashSet();
    this.splitAndAddToCollection(artifactIds, artifactIdPriorities);
    this.artifactSorter.setPriorities(DependencyElement.ARTIFACT_ID, artifactIdPriorities);
  }

  /**
   * Sets the scopes that should be listed first in the dependencies declaration. All scopes that
   * equal any of the scopes in the given list, are required to be located first in the dependencies
   * section.
   * @param scopes Comma separated list of scopes.
   */
  public void setScopePriorities(String scopes) {
    LinkedHashSet<String> scopePriorities = Sets.newLinkedHashSet();
    this.splitAndAddToCollection(scopes, scopePriorities);
    this.artifactSorter.setPriorities(DependencyElement.SCOPE, scopePriorities);
  }

  public ArtifactSorter<Dependency, DependencyElement> getArtifactSorter() {
    return this.artifactSorter;
  }

  protected Collection<Dependency> matchDependencies(
      final Collection<Dependency> subset, final Collection<Dependency> superset) {

    Function<Dependency, Dependency> matchFunction = new Function<Dependency, Dependency>() {
      @Override
      public Dependency apply(Dependency dependency) {
        for (Dependency supersetDependency : superset) {
          if (supersetDependency.getGroupId().equals(dependency.getGroupId())
           && supersetDependency.getArtifactId().equals(dependency.getArtifactId())) {
            Dependency matchedDependency = supersetDependency.clone();
            matchedDependency.setScope(Objects.firstNonNull(supersetDependency.getScope(), "compile"));
            return matchedDependency;
          }
        }
        throw new IllegalStateException(
            "Could not match dependency '" + dependency + "' with superset '." + superset + "'.");
      }
    };
    return Collections2.transform(subset, matchFunction);
  }
}