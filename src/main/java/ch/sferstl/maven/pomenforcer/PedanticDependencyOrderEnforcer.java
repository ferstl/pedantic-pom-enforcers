package ch.sferstl.maven.pomenforcer;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.enforcer.rule.api.EnforcerRuleException;
import org.apache.maven.enforcer.rule.api.EnforcerRuleHelper;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.w3c.dom.Document;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;

import ch.sferstl.maven.pomenforcer.reader.DeclaredDependenciesReader;


public class PedanticDependencyOrderEnforcer extends AbstractPedanticEnforcer {

  private final ArtifactOrdering artifactOrdering;

  public PedanticDependencyOrderEnforcer() {
    Set<ArtifactElement> orderBy = Sets.newLinkedHashSet();
    orderBy.add(ArtifactElement.SCOPE);
    orderBy.add(ArtifactElement.GROUP_ID);
    orderBy.add(ArtifactElement.ARTIFACT_ID);

    this.artifactOrdering = new ArtifactOrdering();
    this.artifactOrdering.orderBy(orderBy);
  }

  public void setOrderBy(String dependencyElements) {
    Set<ArtifactElement> orderBy = Sets.newLinkedHashSet();
    Function<String, ArtifactElement> transformer = new Function<String, ArtifactElement>() {
      @Override
      public ArtifactElement apply(String input) {
        return ArtifactElement.getByElementName(input);
      }
    };
    this.splitAndAddToCollection(dependencyElements, orderBy, transformer);
    this.artifactOrdering.orderBy(orderBy);
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
    this.artifactOrdering.setPriorities(ArtifactElement.GROUP_ID, groupIdPriorities);
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
    this.artifactOrdering.setPriorities(ArtifactElement.ARTIFACT_ID, artifactIdPriorities);
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
    this.artifactOrdering.setPriorities(ArtifactElement.SCOPE, scopePriorities);
  }

  @Override
  public void execute(EnforcerRuleHelper helper) throws EnforcerRuleException {
    MavenProject project = this.getMavenProject(helper);

    Log log = helper.getLog();
    log.info("Enforcing dependency order.");
    log.info("  -> Dependencies have to be ordered by: "
           + COMMA_JOINER.join(this.artifactOrdering.getOrderBy()));
    log.info("  -> Scope priorities: "
           + COMMA_JOINER.join(this.artifactOrdering.getPriorities(ArtifactElement.SCOPE)));
    log.info("  -> Group ID priorities: "
           + COMMA_JOINER.join(this.artifactOrdering.getPriorities(ArtifactElement.GROUP_ID)));
    log.info("  -> Artifact ID priorities: "
           + COMMA_JOINER.join(this.artifactOrdering.getPriorities(ArtifactElement.ARTIFACT_ID)));

    // Read the POM
    Document pomDoc = XmlParser.parseXml(project.getFile());

    Collection<Artifact> declaredDependencies = new DeclaredDependenciesReader(pomDoc).read();
    Collection<Artifact> projectDependencies = project.getDependencyArtifacts();

    ArtifactMatcher artifactMatcher = new ArtifactMatcher();
    Collection<Artifact> dependencyArtifacts =
        artifactMatcher.matchArtifacts(declaredDependencies, projectDependencies);

    Ordering<Artifact> dependencyOrdering = this.artifactOrdering.createArtifactOrdering();

    if (!dependencyOrdering.isOrdered(dependencyArtifacts)) {
      ImmutableList<Artifact> sortedDependencies =
          dependencyOrdering.immutableSortedCopy(dependencyArtifacts);
      throw new EnforcerRuleException(
          "Wrong dependency order. Correct order is:" + sortedDependencies);
    }
  }
}
