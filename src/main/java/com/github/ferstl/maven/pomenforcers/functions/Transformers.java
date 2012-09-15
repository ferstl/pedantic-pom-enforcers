package com.github.ferstl.maven.pomenforcers.functions;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Plugin;

import com.github.ferstl.maven.pomenforcers.PedanticEnforcerRule;
import com.github.ferstl.maven.pomenforcers.PomSection;
import com.github.ferstl.maven.pomenforcers.artifact.DependencyElement;
import com.github.ferstl.maven.pomenforcers.artifact.PluginElement;
import com.github.ferstl.maven.pomenforcers.model.ArtifactModel;
import com.github.ferstl.maven.pomenforcers.model.DependencyModel;
import com.github.ferstl.maven.pomenforcers.model.PluginModel;
import com.google.common.base.Function;

public final class Transformers {

  private static final Function<String, PedanticEnforcerRule> STRING_TO_ENFORCER_RULE =
      new StringToEnforcerRuleTransformer();
  private static final Function<String, PomSection> STRING_TO_POM_SECTION = new StringToPomSectionTransformer();
  private static final Function<PomSection, String> POM_SECTION_TO_STRING = new PomSectionToStringTransformer();
  private static final Function<String, DependencyElement> STRING_TO_DEPENDENCY_ELEMENT =
      new StringToDependencyElementTransformer();
  private static final Function<String, PluginElement> STRING_TO_PLUGIN_ELEMENT =
      new StringToPluginElementTransformer();
  private static final StringToArtifactTransformer STRING_TO_ARTIFACT = new StringToArtifactTransformer();
  private static final Function<Plugin, PluginModel> PLUGIN_TO_PLUGIN_MODEL =
      new PluginToPluginModelTransformer();
  private static final Function<Dependency, DependencyModel> DEPENDENCY_TO_DEPENDENCY_MODEL =
      new DependencyToDependencyModelTransformer();


  public static Function<String, PedanticEnforcerRule> stringToEnforcerRule() {
    return STRING_TO_ENFORCER_RULE;
  }

  public static Function<String, PomSection> stringToPomSection() {
    return STRING_TO_POM_SECTION;
  }

  public static Function<PomSection, String> pomSectionToString() {
    return POM_SECTION_TO_STRING;
  }

  public static Function<String, DependencyElement> stringToDependencyElement() {
    return STRING_TO_DEPENDENCY_ELEMENT;
  }

  public static Function<String, PluginElement> stringToPluginElement() {
    return STRING_TO_PLUGIN_ELEMENT;
  }

  public static Function<String, ArtifactModel> stringToArtifactModel() {
    return STRING_TO_ARTIFACT;
  }

  public static Function<Plugin, PluginModel> pluginToPluginModel() {
    return PLUGIN_TO_PLUGIN_MODEL;
  }

  public static Function<Dependency, DependencyModel> dependencyToDependencyModel() {
    return DEPENDENCY_TO_DEPENDENCY_MODEL;
  }

  private Transformers() {}

  private static class StringToEnforcerRuleTransformer implements Function<String, PedanticEnforcerRule> {
    @Override
    public PedanticEnforcerRule apply(String input) {
      return PedanticEnforcerRule.valueOf(input);
    }
  }

  private static class StringToPomSectionTransformer implements Function<String, PomSection> {
    @Override
    public PomSection apply(String input) {
      return PomSection.getBySectionName(input);
    }
  }

  private static class PomSectionToStringTransformer implements Function<PomSection, String> {
    @Override
    public String apply(PomSection input) {
      return input.getSectionName();
    }
  }

  private static class StringToDependencyElementTransformer implements Function<String, DependencyElement> {
    @Override
    public DependencyElement apply(String input) {
      return DependencyElement.getByElementName(input);
    }
  }

  private static class StringToPluginElementTransformer implements Function<String, PluginElement> {
    @Override
    public PluginElement apply(String input) {
      return PluginElement.getByElementName(input);
    }
  }

  private static class PluginToPluginModelTransformer implements Function<Plugin, PluginModel> {
    @Override
    public PluginModel apply(Plugin input) {
      return new PluginModel(input.getGroupId(), input.getArtifactId(), input.getVersion());
    }
  }

  private static class DependencyToDependencyModelTransformer implements Function<Dependency, DependencyModel> {
    @Override
    public DependencyModel apply(Dependency input) {
      return new DependencyModel(
          input.getGroupId(), input.getArtifactId(), input.getVersion(), input.getScope(), input.getClassifier());
    }
  }
}
