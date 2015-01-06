package com.github.ferstl.maven.pomenforcers.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class DependencyModelTest {

  @Test
  public void toStringWithDefaults() {
    DependencyModel model = new DependencyModel("group", "artifact", "1.0.0", null, null, null);

    assertEquals("group:artifact:1.0.0:jar:compile", model.toString());
  }

  @Test
  public void toStringWithClassifier() {
    DependencyModel model = new DependencyModel("group", "artifact", "1.0.0", null, "classifier", null);

    assertEquals("group:artifact:1.0.0:jar:compile:classifier", model.toString());
  }

  @Test
  public void toStringNoDefaults() {
    DependencyModel model = new DependencyModel("group", "artifact", "1.0.0", "test", "classifier", "zip");

    assertEquals("group:artifact:1.0.0:zip:test:classifier", model.toString());
  }

}
