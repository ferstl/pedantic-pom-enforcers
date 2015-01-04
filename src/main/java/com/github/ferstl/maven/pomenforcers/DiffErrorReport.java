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

import difflib.Chunk;
import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;


public class DiffErrorReport {

  private static final String DELETION_MARKER = "-";
  private static final String INSERTION_MARKER = "+";

  public static void main(String[] args) {
    new DiffErrorReport().fuck();
  }



  public void fuck() {
//    List<String> actual = slightyDifferentOrder();
   List<String> actual = sortedOrder();
    List<String> required = requiredOrder();

    SideBySideData sideBySideData = calculateSideBySideData(actual, required);

    List<String> left = prepareSide(sideBySideData.length, actual);
    List<String> right = prepareSide(sideBySideData.length, actual);
    List<Delta<String>> deltas = sideBySideData.patch.getDeltas();
    int offset = 0;

    for (Delta<String> delta : deltas) {
      Chunk<String> original = delta.getOriginal();
      Chunk<String> revised = delta.getRevised();
      int currentPosition = original.getPosition() + offset;

      System.out.println(delta + " " + revised.getPosition());
      switch(delta.getType()) {
        case INSERT:
          expand(left, right, currentPosition, revised.size());
          setContent(right, currentPosition, INSERTION_MARKER, revised.getLines());
          offset += revised.size();
          break;

        case CHANGE:
          int difference = revised.size() - original.size();
          if (difference > 0) {
            expand(left, right, currentPosition + original.size(), difference);
            offset += difference;
          }

          clearContent(right, currentPosition + revised.size(), difference * -1);
          setContent(left, currentPosition, DELETION_MARKER, original.getLines());
          setContent(right, currentPosition, INSERTION_MARKER, revised.getLines());
          break;

        case DELETE:
          setContent(left, currentPosition, DELETION_MARKER, original.getLines());
          clearContent(right, currentPosition, original.size());
          break;

        default:
          throw new IllegalStateException("Unsupported delta type: " + delta.getType());

      }
    }

    System.out.println(sideBySide(left, right));
  }

  private void expand(List<String> left, List<String> right, int index, int size) {
    if (size < 1) {
      return;
    }

    String[] emptyLines = new String[size];
    Arrays.fill(emptyLines, "");
    List<String> emptyLinesAsList = Arrays.asList(emptyLines);

    left.addAll(index, emptyLinesAsList);
    right.addAll(index, emptyLinesAsList);
  }

  private void clearContent(List<String> l, int index, int size) {
    if (size < 1) {
      return;
    }

    for(int i = 0; i < size; i++) {
      l.set(i + index, "");
    }
  }

  private void setContent(List<String> l, int index, String prefix, Collection<String> lines) {
    int i = 0;

    for (String line : lines) {
      l.set(index + i++, formatLine(prefix, line));
    }
  }

  private String formatLine(String prefix, String line) {
    return prefix + " " + line;
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

  private SideBySideData calculateSideBySideData(List<String> content1, List<String> content2) {
    int length = Math.max(content1.size(), content2.size());
    int width = Math.max(getMaxWidth(content1), getMaxWidth(content2));

    Patch<String> patch = DiffUtils.diff(content1, content2);
    for(Delta<String> delta : patch.getDeltas()) {
      switch(delta.getType()) {
        case INSERT:
        case CHANGE:
          int expansion = delta.getRevised().size() - delta.getOriginal().size();
          length += (expansion > 0) ? expansion : 0;
          break;

        default: // NOP
      }
    }

    return new SideBySideData(patch, length, width);
  }

  private List<String> prepareSide(int finalSize, Collection<String> content) {
    List<String> side = new ArrayList<String>(finalSize);
    side.addAll(content);

    return side;
  }

  private int getMaxWidth(Collection<String> content) {
    int width = 0;
    for (String string : content) {
      width = Math.max(width, string.length());
    }

    return width;
  }

  private static class SideBySideData {
    private final int length;
    private final int width;
    private final Patch<String> patch;

    public SideBySideData(Patch<String> patch, int length, int width) {
      this.patch = patch;
      this.length = length;
      this.width = width;
    }
  }
}
