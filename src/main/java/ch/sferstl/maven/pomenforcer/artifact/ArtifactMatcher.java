package ch.sferstl.maven.pomenforcer.artifact;

import java.util.Collection;

import org.apache.maven.artifact.Artifact;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;


public class ArtifactMatcher {

  /**
   * Matches a subset of artifacts with a superset of artifacts.
   * @param subset subset.
   * @param superset superset.
   * @return The superset's artifacts that match the subset's artifacts in the order in which
   *         they occur in the subset.
   */
  public Collection<Artifact> matchArtifacts(
      final Collection<Artifact> subset, final Collection<Artifact> superset) {

    Function<Artifact, Artifact> matchFunction = new Function<Artifact, Artifact>() {
      @Override
      public Artifact apply(Artifact artifact) {
        for (Artifact supersetArtifact : superset) {
          if (supersetArtifact.getGroupId().equals(artifact.getGroupId())
           && supersetArtifact.getArtifactId().equals(artifact.getArtifactId())) {
            return supersetArtifact;
          }
        }
        throw new IllegalStateException(
            "Could not match artifact '" + artifact + "' with superset '."+ superset + "'.");
      }
    };
    return Collections2.transform(subset, matchFunction);
  }
}
