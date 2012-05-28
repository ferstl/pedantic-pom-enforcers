package ch.sferstl.maven.pomenforcer;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.maven.enforcer.rule.api.EnforcerRuleException;
import org.apache.maven.enforcer.rule.api.EnforcerRuleHelper;
import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.w3c.dom.Document;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;

import ch.sferstl.maven.pomenforcer.artifact.ArtifactSorter;
import ch.sferstl.maven.pomenforcer.artifact.PluginElement;
import ch.sferstl.maven.pomenforcer.reader.DeclaredPluginManagementReader;

public class PedanticPluginManagementOrderEnforcer extends AbstractPedanticEnforcer {

  private final ArtifactSorter<Plugin, PluginElement> artifactSorter;

  public PedanticPluginManagementOrderEnforcer() {
    Set<PluginElement> orderBy = Sets.newLinkedHashSet();
    orderBy.add(PluginElement.GROUP_ID);
    orderBy.add(PluginElement.ARTIFACT_ID);
    this.artifactSorter = new ArtifactSorter<>();
    this.artifactSorter.orderBy(orderBy);
  }

  public void setOrderBy(String pluginElements) {
    Set<PluginElement> orderBy = Sets.newLinkedHashSet();
    Function<String, PluginElement> transformer = new Function<String, PluginElement>() {
      @Override
      public PluginElement apply(String input) {
        return PluginElement.getByElementName(input);
      }
    };
    this.splitAndAddToCollection(pluginElements, orderBy, transformer);
    this.artifactSorter.orderBy(orderBy);
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
    this.artifactSorter.setPriorities(PluginElement.GROUP_ID, groupIdPriorities);
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
    this.artifactSorter.setPriorities(PluginElement.ARTIFACT_ID, artifactIdPriorities);
  }

  @Override
  protected void doEnforce(EnforcerRuleHelper helper, Document pom) throws EnforcerRuleException {
    MavenProject project = getMavenProject(helper);
    Log log = helper.getLog();
    log.info("Enforcing plugin management order.");
    log.info("  -> Plugins have to be ordered by: "
           + COMMA_JOINER.join(this.artifactSorter.getOrderBy()));
    log.info("  -> Group ID priorities: "
           + COMMA_JOINER.join(this.artifactSorter.getPriorities(PluginElement.GROUP_ID)));
    log.info("  -> Artifact ID priorities: "
           + COMMA_JOINER.join(this.artifactSorter.getPriorities(PluginElement.ARTIFACT_ID)));

    Collection<Plugin> declaredPluginManagement =
        new DeclaredPluginManagementReader(pom).read();

    Collection<Plugin> managedPlugins = matchPlugins(declaredPluginManagement, project.getPluginManagement().getPlugins());

    Ordering<Plugin> pluginOrdering = this.artifactSorter.createOrdering();

    if (!pluginOrdering.isOrdered(managedPlugins)) {
      ImmutableList<Plugin> sortedDependencies =
          pluginOrdering.immutableSortedCopy(managedPlugins);
      throw new EnforcerRuleException("One does not simply declare plugin management! "
          + "Your plugin management has to be ordered this way:" + sortedDependencies);
    }
  }

  @Override
  protected void accept(PedanticEnforcerVisitor visitor) {
    visitor.visit(this);
  }

  private Collection<Plugin> matchPlugins(
      final Collection<Plugin> subset, final Collection<Plugin> superset) {

    Function<Plugin, Plugin> matchFunction = new Function<Plugin, Plugin>() {
      @Override
      public Plugin apply(Plugin dependency) {
        for (Plugin supdersetDependency : superset) {
          if (supdersetDependency.getGroupId().equals(dependency.getGroupId())
           && supdersetDependency.getArtifactId().equals(dependency.getArtifactId())) {
            return supdersetDependency;
          }
        }
        throw new IllegalStateException(
            "Could not match dependency '" + dependency + "' with superset '." + superset + "'.");
      }
    };
    return Collections2.transform(subset, matchFunction);
  }
}
