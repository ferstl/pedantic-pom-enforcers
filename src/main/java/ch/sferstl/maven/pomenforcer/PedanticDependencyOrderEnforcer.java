package ch.sferstl.maven.pomenforcer;

import java.util.Collection;

import org.apache.maven.enforcer.rule.api.EnforcerRuleException;
import org.apache.maven.enforcer.rule.api.EnforcerRuleHelper;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.w3c.dom.Document;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Ordering;

import ch.sferstl.maven.pomenforcer.artifact.DependencyElement;
import ch.sferstl.maven.pomenforcer.reader.DeclaredDependenciesReader;


public class PedanticDependencyOrderEnforcer extends AbstractPedanticDependencyOrderEnforcer {

  @Override
  protected void doEnforce(EnforcerRuleHelper helper, Document pom) throws EnforcerRuleException {
    MavenProject project = getMavenProject(helper);

    Log log = helper.getLog();
    log.info("Enforcing dependency order.");
    log.info("  -> Dependencies have to be ordered by: "
           + COMMA_JOINER.join(getArtifactSorter().getOrderBy()));
    log.info("  -> Scope priorities: "
           + COMMA_JOINER.join(getArtifactSorter().getPriorities(DependencyElement.SCOPE)));
    log.info("  -> Group ID priorities: "
           + COMMA_JOINER.join(getArtifactSorter().getPriorities(DependencyElement.GROUP_ID)));
    log.info("  -> Artifact ID priorities: "
           + COMMA_JOINER.join(getArtifactSorter().getPriorities(DependencyElement.ARTIFACT_ID)));

    Collection<Dependency> declaredDependencies = new DeclaredDependenciesReader(pom).read();
    Collection<Dependency> projectDependencies = project.getDependencies();

    Collection<Dependency> dependencyArtifacts = matchDependencies(declaredDependencies, projectDependencies);
    Ordering<Dependency> dependencyOrdering = getArtifactSorter().createOrdering();

    if (!dependencyOrdering.isOrdered(dependencyArtifacts)) {
      ImmutableList<Dependency> sortedDependencies =
          dependencyOrdering.immutableSortedCopy(dependencyArtifacts);
      throw new EnforcerRuleException("One does not simply declare dependencies! "
        + "Your dependencies have to be sorted this way: " + sortedDependencies);
    }
  }

  @Override
  protected void accept(PedanticEnforcerVisitor visitor) {
    visitor.visit(this);
  }
}
