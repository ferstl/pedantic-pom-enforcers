package ch.sferstl.maven.pomenforcer.artifact;

import org.junit.Test;

import ch.sferstl.maven.pomenforcer.artifact.ArtifactElement;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;


public class ArtifactElementTest {

  @Test
  public void testGetByElementName() {
    ArtifactElement.values();
    for (ArtifactElement element : ArtifactElement.values()) {
      assertThat(element, is(ArtifactElement.getByElementName(element.getElementName())));
    }
  }

}
