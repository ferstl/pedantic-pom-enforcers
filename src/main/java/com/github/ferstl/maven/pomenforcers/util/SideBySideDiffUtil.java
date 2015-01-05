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


public final class SideBySideDiffUtil {

  public static void main(String[] args) {
//    List<String> actual = slightyDifferentOrder();
    List<String> actual = sortedOrder();
    List<String> required = requiredOrder();
    System.out.println(SideBySideDiffUtil.diff(actual, required));
  }



  public static String diff(List<String> actual, List<String> required) {

    SideBySideContext context = new SideBySideContext(actual, required);
    int offset = 0;

    for (Delta<String> delta : context.deltas) {
      Chunk<String> original = delta.getOriginal();
      Chunk<String> revised = delta.getRevised();
      int currentPosition = original.getPosition() + offset;

      System.out.println(delta + " " + revised.getPosition());
      switch(delta.getType()) {
        case INSERT:
          context.expand(currentPosition, revised.size());
          context.setRightContent(currentPosition, revised.getLines());
          offset += revised.size();
          break;

        case CHANGE:
          int difference = revised.size() - original.size();
          if (difference > 0) {
            context.expand(currentPosition + original.size(), difference);
            offset += difference;
          } else {
            context.clearRightContent(currentPosition + revised.size(), Math.abs(difference));
          }

          context.setLeftContent(currentPosition, original.getLines());
          context.setRightContent(currentPosition, revised.getLines());
          break;

        case DELETE:
          context.setLeftContent(currentPosition, original.getLines());
          context.clearRightContent(currentPosition, original.size());
          break;

        default:
          throw new IllegalStateException("Unsupported delta type: " + delta.getType());

      }
    }

    return context.toString();
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

  /**
   * Context to manipulate both sides of the diff.
   */
  private static class SideBySideContext {
    private static final String EMPTY_MARKER = "  ";
    private static final String DELETION_MARKER = "- ";
    private static final String INSERTION_MARKER = "+ ";

    private final int width;
    private final Collection<Delta<String>> deltas;
    private final List<String> left;
    private final List<String> right;

    public SideBySideContext(List<String> original, List<String> revised) {
      this.deltas = DiffUtils.diff(original, revised).getDeltas();
      int width = Math.max(getMaxWidth(original), getMaxWidth(revised));
      this.width = width + 2; // include the markers

      int length = Math.max(original.size(), revised.size());
      length += getExpansionLength(this.deltas);
      this.left = new ArrayList<>(length);
      this.right = new ArrayList<>(length);

      for (String string : original) {
        String line = formatLine(EMPTY_MARKER, string);
        this.left.add(line);
        this.right.add(line);
      }
    }

    public void setLeftContent(int index, Collection<String> content) {
      setContent(this.left, index, DELETION_MARKER, content);
    }

    public void setRightContent(int index, Collection<String> content) {
      setContent(this.right, index, INSERTION_MARKER, content);
    }

    public void clearRightContent(int index, int size) {
      clearContent(this.right, index, size);
    }

    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      for(int i = 0; i < this.left.size(); i++) {
        String leftLine = this.left.get(i);
        String rightLine = this.right.get(i);

        String leftPadded = Strings.padEnd(leftLine, this.width, ' ');
        String rightPadded = Strings.padEnd(rightLine, this.width, ' ');

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

    private static int getMaxWidth(Collection<String> content) {
      int width = 0;
      for (String string : content) {
        width = Math.max(width, string.length());
      }

      return width;
    }

    private static int getExpansionLength(Collection<Delta<String>> deltas) {
      int length = 0;
      for(Delta<?> delta : deltas) {
        switch(delta.getType()) {
          case INSERT:
          case CHANGE:
            int expansion = delta.getRevised().size() - delta.getOriginal().size();
            length += (expansion > 0) ? expansion : 0;
            break;

          default: // NOP
        }
      }

      return length;
    }

    private String formatLine(String prefix, String line) {
      return prefix + line;
    }

    private void expand(int index, int size) {
      if (size < 1) {
        return;
      }

      String[] emptyLines = new String[size];
      Arrays.fill(emptyLines, "");
      List<String> emptyLinesAsList = Arrays.asList(emptyLines);

      this.left.addAll(index, emptyLinesAsList);
      this.right.addAll(index, emptyLinesAsList);
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
  }
}
