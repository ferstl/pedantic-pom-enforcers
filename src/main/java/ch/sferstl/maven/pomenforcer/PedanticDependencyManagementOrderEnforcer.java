package ch.sferstl.maven.pomenforcer;

import java.util.Collection;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.enforcer.rule.api.EnforcerRuleException;
import org.apache.maven.enforcer.rule.api.EnforcerRuleHelper;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.w3c.dom.Document;

import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Ordering;

import ch.sferstl.maven.pomenforcer.artifact.ArtifactElement;
import ch.sferstl.maven.pomenforcer.artifact.ArtifactMatcher;
import ch.sferstl.maven.pomenforcer.reader.DeclaredDependencyManagementReader;

public class PedanticDependencyManagementOrderEnforcer
extends AbstractPedanticDependencyOrderEnforcer {

  @Override
  public void execute(EnforcerRuleHelper helper) throws EnforcerRuleException {
    MavenProject project = getMavenProject(helper);

    Log log = helper.getLog();
    log.info("Enforcing dependency management order.");
    log.info("  -> Dependencies have to be ordered by: "
           + COMMA_JOINER.join(getArtifactOrdering().getOrderBy()));
    log.info("  -> Scope priorities: "
           + COMMA_JOINER.join(getArtifactOrdering().getPriorities(ArtifactElement.SCOPE)));
    log.info("  -> Group ID priorities: "
           + COMMA_JOINER.join(getArtifactOrdering().getPriorities(ArtifactElement.GROUP_ID)));
    log.info("  -> Artifact ID priorities: "
           + COMMA_JOINER.join(getArtifactOrdering().getPriorities(ArtifactElement.ARTIFACT_ID)));

    // Read the POM
    Document pomDoc = XmlParser.parseXml(project.getFile());

    Collection<Artifact> declaredDependencyManagement =
        new DeclaredDependencyManagementReader(pomDoc).read();

    ArtifactMatcher<Dependency> artifactMatcher = new ArtifactMatcher<>(new DependencyToArtifactTransformer());
    Collection<Artifact> dependencyArtifacts =
        artifactMatcher.matchArtifacts(
            declaredDependencyManagement, project.getDependencyManagement().getDependencies());

    Ordering<Artifact> dependencyOrdering = getArtifactOrdering().createOrdering();

    if (!dependencyOrdering.isOrdered(dependencyArtifacts)) {
      ImmutableList<Artifact> sortedDependencies =
          dependencyOrdering.immutableSortedCopy(dependencyArtifacts);
      throw new EnforcerRuleException("One does not simply declare dependency management! "
          + "Your dependency management has to be ordered this way:" + sortedDependencies);
    }
  }

  private static class DependencyToArtifactTransformer implements Function<Dependency, Artifact> {
    @Override
    public Artifact apply(Dependency input) {
      return new DefaultArtifact(
          input.getGroupId(),
          input.getArtifactId(),
          input.getVersion(),
          input.getScope() != null ? input.getScope() : "compile",
          input.getType() != null ? input.getType() : "jar",
          Strings.nullToEmpty(input.getClassifier()),
          null);
    }
  }
}
