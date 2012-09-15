package com.github.ferstl.maven.pomenforcers.functions;

import com.google.common.base.Equivalence;

public final class Equivalences {

  private static final Equivalence<String> STRING_STARTS_WITH = new StringStartsWithEquivalence();

  public static Equivalence<String> stringStartsWith() {
    return STRING_STARTS_WITH;
  }
  private Equivalences() {}
}
