package ch.sferstl.maven.pomenforcer;

import java.util.Collection;

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

import ch.sferstl.maven.pomenforcer.reader.DeclaredDependenciesReader;


public class PedanticDependencyOrderEnforcer extends AbstractPedanticEnforcer {

  @Override
  public void execute(EnforcerRuleHelper helper) throws EnforcerRuleException {
    MavenProject project = this.getMavenProject(helper);

    Log log = helper.getLog();
    log.info("Enforcing dependency order.");

    // Read the POM
    Document pomDoc = this.parseXml(project.getFile());

    Collection<Dependency> declaredDependencies = new DeclaredDependenciesReader(pomDoc).read();
    Collection<Dependency> projectDependencies = Lists.newArrayList(project.getDependencies());

    declaredDependencies = this.completeDeclaredDependencies(declaredDependencies, projectDependencies);
    Ordering<Dependency> dependencyOrdering = Ordering.from(DependencyComparator.SCOPE)
                                                      .compound(DependencyComparator.GROUP_ID)
                                                      .compound(DependencyComparator.ARTIFACT_ID);

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

}
