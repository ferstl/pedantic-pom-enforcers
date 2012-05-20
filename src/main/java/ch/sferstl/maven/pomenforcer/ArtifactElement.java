package ch.sferstl.maven.pomenforcer;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;

import org.apache.maven.artifact.Artifact;

import com.google.common.base.Function;
import com.google.common.collect.Maps;

public enum ArtifactElement implements PriorityComparatorFactory<String, Artifact> {
  GROUP_ID("groupId") {
    @Override
    public Comparator<Artifact> createPriorityComparator(Collection<String> priorityCollection) {
      StringStartsWithEquivalence priorityMatcher = new StringStartsWithEquivalence();
      Function<Artifact, String> transformer = new Function<Artifact, String>() {
        @Override
        public String apply(Artifact input) {
          return input.getGroupId();
        }
      };
      return new PriorityComparator<>(priorityCollection, transformer, priorityMatcher);
    }
  },

  ARTIFACT_ID("artifactId") {
    @Override
    public Comparator<Artifact> createPriorityComparator(Collection<String> priorityCollection) {
      StringStartsWithEquivalence priorityMatcher = new StringStartsWithEquivalence();
      Function<Artifact, String> transformer = new Function<Artifact, String>() {
        @Override
        public String apply(Artifact input) {
          return input.getArtifactId();
        }
      };
      return new PriorityComparator<>(priorityCollection, transformer, priorityMatcher);
    }
  },

  SCOPE("scope") {
    @Override
    public PriorityComparator<String, Artifact> createPriorityComparator(Collection<String> priorityCollection) {
      Function<Artifact, String> transformer = new Function<Artifact, String>() {
        @Override
        public String apply(Artifact input) {
          return input.getScope();
        }
      };
      return new PriorityComparator<>(priorityCollection, transformer);
    }
  };

  private static Map<String, ArtifactElement> elementMap;

  static {
    elementMap = Maps.newLinkedHashMap();
    for (ArtifactElement element : values()) {
      elementMap.put(element.getElementName(), element);
    }
  }

  public static ArtifactElement getByElementName(String elementName) {
    if (elementName == null) {
      throw new NullPointerException("Element name is null");
    }

    ArtifactElement result = elementMap.get(elementName);
    if (result == null) {
      throw new IllegalArgumentException("No dependency element with name " + elementName);
    }

    return result;
  }

  private final String elementName;

  private ArtifactElement(String elementName) {
    this.elementName = elementName;
  }

  public String getElementName() {
    return this.elementName;
  }
}