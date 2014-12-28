package com.github.ferstl.maven.pomenforcers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.github.ferstl.maven.pomenforcers.model.PomSection;
import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Ordering;

import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;


public class DiffErrorReport {

  public static void main(String[] args) {
    new DiffErrorReport().fuck();
  }


  public void fuck() {
     List<String> actual = slightyDifferentOrder();
//    List<String> actual = sortedOrder();
    List<String> required = requiredOrder();
    Patch<String> patch = DiffUtils.diff(actual, required);

    int origPos = 0;
    List<String> origSide = new ArrayList<>();
    List<String> reqSide = new ArrayList<>();

    List<Delta<String>> deltas = patch.getDeltas();
    for (Delta<String> delta : deltas) {
      while (origPos < delta.getOriginal().getPosition()) {
        add(origSide, " ", actual.get(origPos));
        add(reqSide, " ", actual.get(origPos));
        origPos++;
      }

      switch(delta.getType()) {
        case INSERT:
          for(int i = 0; i < delta.getRevised().size(); i++) {
            add(origSide, " ", "");
          }
          addAll(reqSide, "+", delta.getRevised().getLines());
          break;

        case CHANGE:
          addAll(origSide, "-", delta.getOriginal().getLines());
          // fill up original side
          for(int i = 0; i < delta.getRevised().size() - delta.getOriginal().size(); i++) {
            add(origSide, " ", "");
          }
          origPos += delta.getOriginal().size();


          addAll(reqSide, "+", delta.getRevised().getLines());
          // fill up required side
          for(int i = 0; i < delta.getOriginal().size() - delta.getRevised().size(); i++) {
            add(reqSide, " ", "");
          }

        break;

        case DELETE:
          addAll(origSide, "-", delta.getOriginal().getLines());
          origPos += delta.getOriginal().size();
          for(int i = 0; i < delta.getOriginal().size(); i++) {
            add(reqSide, " ", "");
          }
          break;
        default:
          throw new IllegalStateException("Unsupported Delta: " + delta.getType());
      }
    }


    System.out.println(sideBySide(origSide, reqSide));
  }

  private void add(Collection<String> c, String prefix, String content) {
    c.add(prefix + " " + content);
  }

  private void addAll(Collection<String> c, String prefix, Collection<String> content) {
    for (String string : content) {
      add(c, prefix, string);
    }
  }

  private String sideBySide(List<String> left, List<String> right) {
    int leftSize = left.size();
    int rightSize = right.size();
    int maxSize = Math.max(leftSize, rightSize);

    StringBuilder sb = new StringBuilder();
    for(int i = 0; i < maxSize; i++) {
      String leftLine = i < leftSize ? left.get(i) : "";
      String rightLine = i < rightSize ? right.get(i) : "";

      String leftPadded = Strings.padEnd(leftLine, 25, ' ');
      String rightPadded = Strings.padEnd(rightLine, 25, ' ');

      sb.append(leftPadded)
      .append(" | ")
      .append(rightPadded)
      .append("\n");
    }

    // Remove last newline
    if (sb.length() > 0) {
      sb.deleteCharAt(sb.length() - 1);
    }
    return sb.toString();
  }

  private static List<String> requiredOrder() {
    return FluentIterable.from(Arrays.asList(PomSection.values()))
      .transform(new Function<PomSection, String>() {

        @Override
        public String apply(PomSection input) {
          return input.getSectionName();
        }})
      .toList();
  }

  private static List<String> sortedOrder() {
    return Ordering.usingToString().sortedCopy(requiredOrder());
  }

  private static List<String> slightyDifferentOrder() {
    List<String> list = new ArrayList<>(requiredOrder());
    swap(list, 0, 5);
    swap(list, 4, 10);

    return list;
  }

  private static void swap(List<String> list, int i1, int i2) {
    String tmp = list.get(i1);
    list.set(i1, list.get(i2));
    list.set(i2, tmp);
  }
}
