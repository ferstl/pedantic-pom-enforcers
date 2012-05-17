package ch.sferstl.maven.pomenforcer;

import java.util.Collection;
import java.util.Set;

import org.apache.maven.enforcer.rule.api.EnforcerRuleException;
import org.apache.maven.enforcer.rule.api.EnforcerRuleHelper;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.w3c.dom.Document;

import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;

import ch.sferstl.maven.pomenforcer.reader.DeclaredDependenciesReader;


public class PedanticDependencyOrderEnforcer extends AbstractPedanticEnforcer {

  /** Comma separated list of group IDs that should be first (in order of declaration) in the dependencies section. */
  private Set<String> firstGroupIds;


  public PedanticDependencyOrderEnforcer() {
    this.firstGroupIds = Sets.newLinkedHashSet();
  }

  @Override
  public void execute(EnforcerRuleHelper helper) throws EnforcerRuleException {
    MavenProject project = this.getMavenProject(helper);

    Log log = helper.getLog();
    log.info("Enforcing dependency order. Priorized group IDs: " + this.firstGroupIds);

    // Read the POM
    Document pomDoc = this.parseXml(project.getFile());

    Collection<Dependency> declaredDependencies = new DeclaredDependenciesReader(pomDoc).read();
    Collection<Dependency> projectDependencies = Lists.newArrayList(project.getDependencies());

    declaredDependencies = this.completeDeclaredDependencies(declaredDependencies, projectDependencies);
    Function<Dependency, String> transformer = new Function<Dependency, String>() {
      @Override
      public String apply(Dependency input) {
        return input.getGroupId();
      }
    };
    PriorityComparator<String, Dependency> groupIdComparator =
        new PriorityComparator<>(this.firstGroupIds, transformer, new StringStartsWithEquivalence());

    Ordering<Dependency> dependencyOrdering = Ordering.from(DependencyComparator.SCOPE)
                                                      .compound(groupIdComparator)
                                                      .compound(DependencyComparator.ARTIFACT_ID);

    if (!dependencyOrdering.isOrdered(declaredDependencies)) {
      ImmutableList<Dependency> sortedDependencies = dependencyOrdering.immutableSortedCopy(declaredDependencies);
      throw new EnforcerRuleException("Wrong dependency order. Correct order is:" + sortedDependencies);
    }

  }

  /**
   * Sets the group IDs that should be listed first in the dependencies declaration.
   * All group IDs that <strong>start with</strong> any of the priorized group IDs in the given list, are required
   * to be located first in the dependencies section.
   * @param commaSeparatedGroupIds Comma separated list of group IDs.
   */
  public void setFirstGroupIds(String commaSeparatedGroupIds) {
    Iterable<String> priorizedGroupIds = Splitter.on(",").split(commaSeparatedGroupIds);
    this.firstGroupIds = Sets.newLinkedHashSet(priorizedGroupIds);
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
  };

}
