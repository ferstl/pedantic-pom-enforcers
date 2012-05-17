package ch.sferstl.maven.pomenforcer;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;


public class DependencyElementTest {

  @Test
  public void testGetByElementName() {
    DependencyElement.values();
    for (DependencyElement element : DependencyElement.values()) {
      assertThat(element, is(DependencyElement.getByElementName(element.getElementName())));
    }
  }

}
