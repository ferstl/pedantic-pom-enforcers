package com.github.ferstl.maven.pomenforcers.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class CommaSeparatorUtilsTest {

  @Test
  public void testSplitAndAddToCollection() {
    Map<String, List<String>> tests = ImmutableMap.<String, List<String>>builder()
        .put("a,b,c", ImmutableList.of("a", "b", "c"))
        .put("a,b,,,c,", ImmutableList.of("a", "b", "c"))
        .put(" a \n,\n   \t b   ,\r\n  c \t\n", ImmutableList.of("a", "b", "c"))
        .build();
    for (Map.Entry<String, List<String>> test : tests.entrySet()) {
      List<String> l = new ArrayList<>();
      CommaSeparatorUtils.splitAndAddToCollection(test.getKey(), l);
      assertThat(l, equalTo(test.getValue()));
    }
  }
}
