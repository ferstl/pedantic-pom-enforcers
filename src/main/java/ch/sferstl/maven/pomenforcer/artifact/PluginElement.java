package ch.sferstl.maven.pomenforcer.artifact;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;

import org.apache.maven.model.Plugin;

import com.google.common.base.Function;
import com.google.common.collect.Maps;

import ch.sferstl.maven.pomenforcer.priority.PriorityComparator;
import ch.sferstl.maven.pomenforcer.priority.PriorityComparatorFactory;
import ch.sferstl.maven.pomenforcer.priority.StringStartsWithEquivalence;

public enum PluginElement implements PriorityComparatorFactory<String, Plugin> {

  GROUP_ID("groupId") {
    @Override
    public Comparator<Plugin> createPriorityComparator(Collection<String> priorityCollection) {
      StringStartsWithEquivalence priorityMatcher = new StringStartsWithEquivalence();
      Function<Plugin, String> transformer = new Function<Plugin, String>() {
        @Override
        public String apply(Plugin input) {
          return input.getGroupId();
        }
      };
      return new PriorityComparator<>(priorityCollection, transformer, priorityMatcher);
    }
  },

  ARTIFACT_ID("artifactId") {
    @Override
    public Comparator<Plugin> createPriorityComparator(Collection<String> priorityCollection) {
      StringStartsWithEquivalence priorityMatcher = new StringStartsWithEquivalence();
      Function<Plugin, String> transformer = new Function<Plugin, String>() {
        @Override
        public String apply(Plugin input) {
          return input.getArtifactId();
        }
      };
      return new PriorityComparator<>(priorityCollection, transformer, priorityMatcher);
    }
  };

  private static Map<String, PluginElement> elementMap;

  static {
    elementMap = Maps.newLinkedHashMap();
    for (PluginElement element : values()) {
      elementMap.put(element.getElementName(), element);
    }
  }

  public static PluginElement getByElementName(String elementName) {
    if (elementName == null) {
      throw new NullPointerException("Element name is null");
    }

    PluginElement result = elementMap.get(elementName);
    if (result == null) {
      throw new IllegalArgumentException("No plugin element with name " + elementName);
    }

    return result;
  }

  private final String elementName;

  private PluginElement(String elementName) {
    this.elementName = elementName;
  }

  public String getElementName() {
    return this.elementName;
  }
}
