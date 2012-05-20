package ch.sferstl.maven.pomenforcer;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.enforcer.rule.api.EnforcerRuleException;
import org.apache.maven.enforcer.rule.api.EnforcerRuleHelper;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.w3c.dom.Document;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;

import ch.sferstl.maven.pomenforcer.reader.DeclaredDependenciesReader;


public class PedanticDependencyOrderEnforcer extends AbstractPedanticEnforcer {

  private final Set<DependencyElement> orderBy;
  private final Set<String> groupIdPriorities;
  private final Set<String> artifactIdPriorities;
  private final Set<String> scopePriorities;


  public PedanticDependencyOrderEnforcer() {
    this.groupIdPriorities = Sets.newLinkedHashSet();
    this.artifactIdPriorities = Sets.newLinkedHashSet();
    this.scopePriorities = Sets.newLinkedHashSet();
    this.orderBy = Sets.newLinkedHashSet();
    this.orderBy.add(DependencyElement.SCOPE);
    this.orderBy.add(DependencyElement.GROUP_ID);
    this.orderBy.add(DependencyElement.ARTIFACT_ID);
  }

  public void setOrderBy(String dependencyElements) {
    Function<String, DependencyElement> transformer = new Function<String, DependencyElement>() {
      @Override
      public DependencyElement apply(String input) {
        return DependencyElement.getByElementName(input);
      }
    };
    this.splitAndAddToCollection(dependencyElements, this.orderBy, transformer);
  }

  /**
   * Sets the group IDs that should be listed first in the dependencies declaration. All group IDs
   * that <strong>start with</strong> any of the priorized group IDs in the given list, are required
   * to be located first in the dependencies section.
   * @param groupIds Comma separated list of group IDs.
   */
  public void setGroupIdPriorities(String groupIds) {
    this.splitAndAddToCollection(groupIds, this.groupIdPriorities);
  }

  /**
   * Sets the artifact IDs that should be listed first in the dependencies declaration. All artifact
   * IDs that <strong>start with</strong> any of the priorized IDs in the given list, are required
   * to be located first in the dependencies section.
   * @param artifactIds Comma separated list of artifact IDs.
   */
  public void setArtifactIdPriorities(String artifactIds) {
    this.splitAndAddToCollection(artifactIds, this.artifactIdPriorities);
  }

  /**
   * Sets the scopes that should be listed first in the dependencies declaration. All scopes that
   * equal any of the scopes in the given list, are required to be located first in the dependencies
   * section.
   * @param scopes Comma separated list of scopes.
   */
  public void setScopePriorities(String scopes) {
    this.splitAndAddToCollection(scopes, this.scopePriorities);
  }

  @Override
  public void execute(EnforcerRuleHelper helper) throws EnforcerRuleException {
    MavenProject project = this.getMavenProject(helper);

    Log log = helper.getLog();
    log.info("Enforcing dependency order.");
    log.info("  -> Dependencies have to be ordered by: " + COMMA_JOINER.join(this.orderBy));
    log.info("  -> Scope priorities: " + COMMA_JOINER.join(this.scopePriorities));
    log.info("  -> Group ID priorities: " + COMMA_JOINER.join(this.groupIdPriorities));
    log.info("  -> Artifact ID priorities: " + COMMA_JOINER.join(this.artifactIdPriorities));

    // Read the POM
    Document pomDoc = XmlParser.parseXml(project.getFile());

    Collection<Dependency> declaredDependencies = new DeclaredDependenciesReader(pomDoc).read();
    Collection<Artifact> projectDependencies = project.getDependencyArtifacts();

    Collection<Artifact> dependencyArtifaccts =
        this.matchWithArtifacts(declaredDependencies, projectDependencies);

    Ordering<Artifact> dependencyOrdering = this.createDependencyOrdering();

    if (!dependencyOrdering.isOrdered(dependencyArtifaccts)) {
      ImmutableList<Artifact> sortedDependencies =
          dependencyOrdering.immutableSortedCopy(dependencyArtifaccts);
      throw new EnforcerRuleException(
          "Wrong dependency order. Correct order is:" + sortedDependencies);
    }

  }

  /**
   * Matches the given dependencies with the given artifacts. The dependencies have to be a subset
   * of the artifacts.
   * @param dependencies dependencies.
   * @param artifacts artifacts.
   * @return The project's dependency artifacts in the order in which they were declared.
   */
  private Collection<Artifact> matchWithArtifacts(
      final Collection<Dependency> dependencies, final Collection<Artifact> artifacts) {

    Function<Dependency, Artifact> matchFunction = new Function<Dependency, Artifact>() {
      @Override
      public Artifact apply(Dependency dependency) {
        for (Artifact artifact : artifacts) {
          if (artifact.getGroupId().equals(dependency.getGroupId())
           && artifact.getArtifactId().equals(dependency.getArtifactId())) {
            return artifact;
          }
        }
        throw new IllegalStateException(
            "Could not match dependency '" + dependency + "' with artifacts '."+ artifacts + "'.");
      }
    };
    return Collections2.transform(dependencies, matchFunction);
  }

  private Ordering<Artifact> createDependencyOrdering() {
    List<Comparator<Artifact>> comparators = Lists.newArrayListWithCapacity(this.orderBy.size());
    for (DependencyElement element : this.orderBy) {
      switch(element) {
        case GROUP_ID:
          comparators.add(element.createPriorityComparator(this.groupIdPriorities));
          break;
        case ARTIFACT_ID:
          comparators.add(element.createPriorityComparator(this.artifactIdPriorities));
          break;
        case SCOPE:
          comparators.add(element.createPriorityComparator(this.scopePriorities));
          break;
        default:
          throw new IllegalArgumentException("Unsupported dependency element: '" + element.getElementName() + "'");
      }
    }

    Ordering<Artifact> ordering;
    if (comparators.size() > 0) {
      ordering = Ordering.from(comparators.get(0));
      for (Comparator<Artifact> comparator : comparators.subList(1, comparators.size())) {
        ordering = ordering.compound(comparator);
      }
    } else {
      throw new IllegalStateException("Undefined dependency order. Either define it or remove the <orderBy> "
                                    + "configuration to use the default ordering.");
    }
    return ordering;
  }

}
