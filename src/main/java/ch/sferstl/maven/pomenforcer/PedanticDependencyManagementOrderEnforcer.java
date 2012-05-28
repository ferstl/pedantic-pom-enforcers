package ch.sferstl.maven.pomenforcer;

import java.util.Collection;

import org.apache.maven.enforcer.rule.api.EnforcerRuleException;
import org.apache.maven.enforcer.rule.api.EnforcerRuleHelper;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.DependencyManagement;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.w3c.dom.Document;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;

import ch.sferstl.maven.pomenforcer.artifact.DependencyElement;
import ch.sferstl.maven.pomenforcer.reader.DeclaredDependencyManagementReader;

public class PedanticDependencyManagementOrderEnforcer
extends AbstractPedanticDependencyOrderEnforcer {

  @Override
  protected void doEnforce(EnforcerRuleHelper helper, Document pom) throws EnforcerRuleException {
    MavenProject project = getMavenProject(helper);

    Log log = helper.getLog();
    log.info("Enforcing dependency management order.");
    log.info("  -> Dependencies have to be ordered by: "
           + COMMA_JOINER.join(getArtifactSorter().getOrderBy()));
    log.info("  -> Scope priorities: "
           + COMMA_JOINER.join(getArtifactSorter().getPriorities(DependencyElement.SCOPE)));
    log.info("  -> Group ID priorities: "
           + COMMA_JOINER.join(getArtifactSorter().getPriorities(DependencyElement.GROUP_ID)));
    log.info("  -> Artifact ID priorities: "
           + COMMA_JOINER.join(getArtifactSorter().getPriorities(DependencyElement.ARTIFACT_ID)));

    Collection<Dependency> declaredDependencyManagement =
        new DeclaredDependencyManagementReader(pom).read();

    Collection<Dependency> managedDependencyArtifacts =
        matchDependencies(declaredDependencyManagement, getManagedDependencies(project));

    Ordering<Dependency> dependencyOrdering = getArtifactSorter().createOrdering();

    if (!dependencyOrdering.isOrdered(managedDependencyArtifacts)) {
      ImmutableList<Dependency> sortedDependencies =
          dependencyOrdering.immutableSortedCopy(managedDependencyArtifacts);
      throw new EnforcerRuleException("One does not simply declare dependency management! "
          + "Your dependency management has to be ordered this way:" + sortedDependencies);
    }
  }

  @Override
  protected void accept(PedanticEnforcerVisitor visitor) {
    visitor.visit(this);
  }

  private Collection<Dependency> getManagedDependencies(MavenProject project) {
    DependencyManagement dependencyManagement = project.getDependencyManagement();
    Collection<Dependency> managedDependencies;
    if (dependencyManagement != null) {
      managedDependencies = dependencyManagement.getDependencies();
    } else {
      managedDependencies = Lists.newArrayList();
    }
    return managedDependencies;
  }
}
