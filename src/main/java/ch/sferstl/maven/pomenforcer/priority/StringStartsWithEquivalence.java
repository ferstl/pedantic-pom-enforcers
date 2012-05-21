package ch.sferstl.maven.pomenforcer.priority;

import com.google.common.base.Equivalence;


public class StringStartsWithEquivalence extends Equivalence<String> {

  @Override
  protected boolean doEquivalent(String a, String b) {
    return a.startsWith(b);
  }

  @Override
  protected int doHash(String t) {
    return t.hashCode();
  }

}
