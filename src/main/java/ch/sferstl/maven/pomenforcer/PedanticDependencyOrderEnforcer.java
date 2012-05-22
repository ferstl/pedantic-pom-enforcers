package ch.sferstl.maven.pomenforcer;

import java.util.Collection;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.enforcer.rule.api.EnforcerRuleException;
import org.apache.maven.enforcer.rule.api.EnforcerRuleHelper;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.w3c.dom.Document;

import com.google.common.base.Functions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Ordering;

import ch.sferstl.maven.pomenforcer.artifact.ArtifactElement;
import ch.sferstl.maven.pomenforcer.artifact.ArtifactMatcher;
import ch.sferstl.maven.pomenforcer.reader.DeclaredDependenciesReader;


public class PedanticDependencyOrderEnforcer extends AbstractPedanticDependencyOrderEnforcer {

  @Override
  public void execute(EnforcerRuleHelper helper) throws EnforcerRuleException {
    MavenProject project = getMavenProject(helper);

    Log log = helper.getLog();
    log.info("Enforcing dependency order.");
    log.info("  -> Dependencies have to be ordered by: "
           + COMMA_JOINER.join(getArtifactSorter().getOrderBy()));
    log.info("  -> Scope priorities: "
           + COMMA_JOINER.join(getArtifactSorter().getPriorities(ArtifactElement.SCOPE)));
    log.info("  -> Group ID priorities: "
           + COMMA_JOINER.join(getArtifactSorter().getPriorities(ArtifactElement.GROUP_ID)));
    log.info("  -> Artifact ID priorities: "
           + COMMA_JOINER.join(getArtifactSorter().getPriorities(ArtifactElement.ARTIFACT_ID)));

    // Read the POM
    Document pomDoc = XmlParser.parseXml(project.getFile());

    Collection<Artifact> declaredDependencies = new DeclaredDependenciesReader(pomDoc).read();
    Collection<Artifact> projectDependencies = project.getDependencyArtifacts();

    ArtifactMatcher<Artifact> artifactMatcher = new ArtifactMatcher<>(Functions.<Artifact>identity());
    Collection<Artifact> dependencyArtifacts =
        artifactMatcher.matchArtifacts(declaredDependencies, projectDependencies);

    Ordering<Artifact> dependencyOrdering = getArtifactSorter().createOrdering();

    if (!dependencyOrdering.isOrdered(dependencyArtifacts)) {
      ImmutableList<Artifact> sortedDependencies =
          dependencyOrdering.immutableSortedCopy(dependencyArtifacts);
      throw new EnforcerRuleException("One does not simply declare dependencies! "
        + "Your dependencies have to be sorted this way: " + sortedDependencies);
    }
  }
}
