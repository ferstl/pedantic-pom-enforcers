package ch.sferstl.maven.pomenforcer;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;

import org.apache.maven.model.Dependency;

import com.google.common.base.Function;
import com.google.common.collect.Maps;

public enum DependencyElement implements PriorityComparatorFactory<String, Dependency> {
  GROUP_ID("groupId") {
    @Override
    public Comparator<Dependency> createPriorityComparator(Collection<String> priorityList) {
      StringStartsWithEquivalence priorityMatcher = new StringStartsWithEquivalence();
      Function<Dependency, String> transformer = new Function<Dependency, String>() {
        @Override
        public String apply(Dependency input) {
          return input.getGroupId();
        }
      };
      return new PriorityComparator<>(priorityList, transformer, priorityMatcher);
    }
  },

  ARTIFACT_ID("artifactId") {
    @Override
    public Comparator<Dependency> createPriorityComparator(Collection<String> priorityList) {
      // TODO Auto-generated method stub
      StringStartsWithEquivalence priorityMatcher = new StringStartsWithEquivalence();
      Function<Dependency, String> transformer = new Function<Dependency, String>() {
        @Override
        public String apply(Dependency input) {
          return input.getArtifactId();
        }
      };
      return new PriorityComparator<>(priorityList, transformer, priorityMatcher);
    }
  },

  SCOPE("scope") {
    @Override
    public PriorityComparator<String, Dependency> createPriorityComparator(Collection<String> priorityList) {
      Function<Dependency, String> transformer = new Function<Dependency, String>() {
        @Override
        public String apply(Dependency input) {
          return input.getScope();
        }
      };
      return new PriorityComparator<>(priorityList, transformer);
    }
  };

  private static Map<String, DependencyElement> elementMap;

  static {
    elementMap = Maps.newLinkedHashMap();
    for (DependencyElement element : values()) {
      elementMap.put(element.getElementName(), element);
    }
  }

  public static DependencyElement getByElementName(String elementName) {
    if (elementName == null) {
      throw new NullPointerException("Element name is null");
    }

    DependencyElement result = elementMap.get(elementName);
    if (result == null) {
      throw new IllegalArgumentException("No dependency element with name " + elementName);
    }

    return result;
  }

  private final String elementName;

  private DependencyElement(String elementName) {
    this.elementName = elementName;
  }

  public String getElementName() {
    return this.elementName;
  }
}