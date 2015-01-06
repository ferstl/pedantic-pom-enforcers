package com.github.ferstl.maven.pomenforcers.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class ArtifactModelTest {

  @Test
  public void toStringNoGroupId() {
    ArtifactModel model = new ArtifactModel(null, "artifact", "1.0.0");

    assertEquals(":artifact:1.0.0", model.toString());
  }

  @Test
  public void toStringNoVersion() {
    ArtifactModel model = new ArtifactModel("group", "artifact");

    assertEquals("group:artifact:", model.toString());
  }

  @Test
  public void toStringNoGroupIdAndNoVersion() {
    ArtifactModel model = new ArtifactModel(null, "artifact");

    assertEquals(":artifact:", model.toString());
  }

}
