package com.github.ferstl.maven.pomenforcers;

import java.util.Collection;
import java.util.List;

import org.apache.maven.enforcer.rule.api.EnforcerRuleException;
import org.apache.maven.enforcer.rule.api.EnforcerRuleHelper;
import org.apache.maven.model.Plugin;
import org.w3c.dom.Document;

import com.github.ferstl.maven.pomenforcers.reader.DeclaredPluginsReader;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

// project.getParent().getFile() == null, if parent POM not available (i.e. in local repo)
// project.getParent() != null, if there is a parent
//
// rule: if project has a parent
//       -> plugin version has to be declared in parent's <pluginManagement>
//       -> plugin configuration has to be defined in parent's <pluginManagement>
//          EXCEPT the configuration is declared in within an <execution>
public class PedanticPluginConfigurationEnforcer extends AbstractPedanticEnforcer {

  @Override
  protected void doEnforce(EnforcerRuleHelper helper, Document pom) throws EnforcerRuleException {
    List<Plugin> declaredPlugins = new DeclaredPluginsReader(pom).read();

    Collection<Plugin> versionedPlugins = Collections2.filter(declaredPlugins, new Predicate<Plugin>() {
      @Override
      public boolean apply(Plugin input) {
        return input.getVersion() != null;
      }
    });

    if (versionedPlugins.size() != 0) {
      throw new EnforcerRuleException("One does not simply declare plugin versions! "
          + "Use <pluginManagement> to declare the version of these plugins: " + versionedPlugins);
    }
  }

  @Override
  protected void accept(PedanticEnforcerVisitor visitor) {
    visitor.visit(this);
  }

}
