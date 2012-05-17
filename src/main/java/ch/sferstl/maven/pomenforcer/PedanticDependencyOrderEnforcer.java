package ch.sferstl.maven.pomenforcer;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.apache.maven.enforcer.rule.api.EnforcerRuleException;
import org.apache.maven.enforcer.rule.api.EnforcerRuleHelper;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.w3c.dom.Document;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
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

    Joiner joiner = Joiner.on(",");
    Log log = helper.getLog();
    log.info("Enforcing dependency order.");
    log.info("  -> Dependencies have to be ordered by: " + joiner.join(this.orderBy));
    log.info("  -> Scope priorities: " + joiner.join(this.scopePriorities));
    log.info("  -> Group ID priorities: " + joiner.join(this.groupIdPriorities));
    log.info("  -> Artifact ID priorities: " + joiner.join(this.artifactIdPriorities));

    // Read the POM
    Document pomDoc = XmlParser.parseXml(project.getFile());

    Collection<Dependency> declaredDependencies = new DeclaredDependenciesReader(pomDoc).read();
    Collection<Dependency> projectDependencies = Lists.newArrayList(project.getDependencies());

    declaredDependencies = this.completeDeclaredDependencies(declaredDependencies, projectDependencies);

    Ordering<Dependency> dependencyOrdering = this.createDependencyOrdering();

    if (!dependencyOrdering.isOrdered(declaredDependencies)) {
      ImmutableList<Dependency> sortedDependencies = dependencyOrdering.immutableSortedCopy(declaredDependencies);
      throw new EnforcerRuleException("Wrong dependency order. Correct order is:" + sortedDependencies);
    }

  }

  /**
   * Completes the declared dependencies with information from the project's dependencies.
   * @param declaredDependencies declared dependencies.
   * @param projectDependencies project dependencies.
   * @return The completed list of declared dependencies.
   */
  private Collection<Dependency> completeDeclaredDependencies(
      final Collection<Dependency> declaredDependencies, final Collection<Dependency> projectDependencies) {

    // TODO: really?!
    Function<Dependency, Dependency> completeFunction = new Function<Dependency, Dependency>() {
      @Override
      public Dependency apply(Dependency input) {
        for (Dependency dependency : projectDependencies) {
          if (dependency.getGroupId().equals(input.getGroupId())
           && dependency.getArtifactId().equals(input.getArtifactId())) {
            return dependency.clone();
          }
        }
        throw new IllegalStateException(
            "Found declared dependency '" + input + "' which is not available in the project's dependencies.");
      }
    };
    return Collections2.transform(declaredDependencies, completeFunction);
  }

  private Ordering<Dependency> createDependencyOrdering() {
    List<Comparator<Dependency>> comparators = Lists.newArrayListWithCapacity(this.orderBy.size());
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

    Ordering<Dependency> ordering;
    if (comparators.size() > 0) {
      ordering = Ordering.from(comparators.get(0));
      for (Comparator<Dependency> comparator : comparators.subList(1, comparators.size())) {
        ordering.compound(comparator);
      }
    } else {
      throw new IllegalStateException("Undefined dependency order. Either define it or remove the <orderBy> "
                                    + "configuration to use the default ordering.");
    }
    return ordering;
  }

}
