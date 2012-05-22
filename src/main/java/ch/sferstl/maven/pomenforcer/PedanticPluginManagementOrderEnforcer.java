package ch.sferstl.maven.pomenforcer;
import java.util.Collection;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.enforcer.rule.api.EnforcerRuleException;
import org.apache.maven.enforcer.rule.api.EnforcerRuleHelper;
import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.w3c.dom.Document;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Ordering;

import ch.sferstl.maven.pomenforcer.artifact.ArtifactElement;
import ch.sferstl.maven.pomenforcer.artifact.ArtifactMatcher;
import ch.sferstl.maven.pomenforcer.reader.DeclaredPluginManagementReader;




public class PedanticPluginManagementOrderEnforcer extends AbstractPedanticDependencyOrderEnforcer {

  @Override
  public void execute(EnforcerRuleHelper helper) throws EnforcerRuleException {
    MavenProject project = getMavenProject(helper);

    Log log = helper.getLog();
    log.info("Enforcing plugin management order.");
    log.info("  -> Plugins have to be ordered by: "
           + COMMA_JOINER.join(getArtifactSorter().getOrderBy()));
    log.info("  -> Group ID priorities: "
           + COMMA_JOINER.join(getArtifactSorter().getPriorities(ArtifactElement.GROUP_ID)));
    log.info("  -> Artifact ID priorities: "
           + COMMA_JOINER.join(getArtifactSorter().getPriorities(ArtifactElement.ARTIFACT_ID)));

    // Read the POM
    Document pomDoc = XmlParser.parseXml(project.getFile());

    Collection<Artifact> declaredPluginManagement =
        new DeclaredPluginManagementReader(pomDoc).read();

    ArtifactMatcher<Plugin> artifactMatcher = new ArtifactMatcher<>(new PluginToArtifactTransformer());
    Collection<Artifact> dependencyArtifacts =
        artifactMatcher.matchArtifacts(declaredPluginManagement, project.getPluginManagement().getPlugins());

    Ordering<Artifact> pluginOrdering = getArtifactSorter().createOrdering();

    if (!pluginOrdering.isOrdered(dependencyArtifacts)) {
      ImmutableList<Artifact> sortedDependencies =
          pluginOrdering.immutableSortedCopy(dependencyArtifacts);
      throw new EnforcerRuleException("One does not simply declare plugin management! "
          + "Your plugin management has to be ordered this way:" + sortedDependencies);
    }
  }

  private static class PluginToArtifactTransformer implements Function<Plugin, Artifact> {
    @Override
    public Artifact apply(Plugin input) {
      return new DefaultArtifact(
          input.getGroupId(),
          input.getArtifactId(),
          input.getVersion(),
          "",
          "",
          "",
          null);
    }
  }
}
