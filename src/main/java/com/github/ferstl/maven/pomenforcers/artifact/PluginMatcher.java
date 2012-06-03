/*
 * Copyright (c) 2012 by The Author(s)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.ferstl.maven.pomenforcers.artifact;

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
