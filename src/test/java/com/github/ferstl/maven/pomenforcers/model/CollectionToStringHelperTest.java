package com.github.ferstl.maven.pomenforcers.model;

import org.junit.Test;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

public class CollectionToStringHelperTest {

  @Test
  public void toStringWithValues() {
    // act
    String result = CollectionToStringHelper.toString("Test", asList("a", "b", "c"));

    // assert
    String expected = "Test [\n"
        + "a,\n"
        + "b,\n"
        + "c\n"
        + "]";
    assertEquals(expected, result);
  }


  @Test
  public void toStringWithNullCollection() {
    // act
    String result = CollectionToStringHelper.toString("Test", null);

    // assert
    String expected = "Test [\n"
        + "]";
    assertEquals(expected, result);
  }
}
