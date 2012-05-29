package ch.sferstl.maven.pomenforcer.artifact;

import java.util.Collection;

import org.apache.maven.model.Plugin;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;

public class PluginMatcher {

  private final MatchFunction matchFunction;

  public PluginMatcher(Collection<Plugin> superset) {
    this.matchFunction = new MatchFunction(superset);
  }

  public Collection<Plugin> match(Collection<Plugin> subset) {
    return Collections2.transform(subset, this.matchFunction);
  }


  private static class MatchFunction implements Function<Plugin, Plugin> {

    private final Collection<Plugin> superset;

    public MatchFunction(Collection<Plugin> superset) {
      this.superset = superset;
    }

    @Override
    public Plugin apply(Plugin plugin) {
      for (Plugin supdersetDependency : this.superset) {
        if (supdersetDependency.getGroupId().equals(plugin.getGroupId())
         && supdersetDependency.getArtifactId().equals(plugin.getArtifactId())) {
          return supdersetDependency;
        }
      }
      throw new IllegalStateException(
          "Could not match plugin '" + plugin + "' with superset '." + this.superset + "'.");
    }
  }
}
