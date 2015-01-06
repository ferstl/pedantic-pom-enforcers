package com.github.ferstl.maven.pomenforcers.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.google.common.base.Strings;

import difflib.Chunk;
import difflib.Delta;
import difflib.DiffUtils;


public final class SideBySideDiffUtil {

  public static String diff(Collection<String> actual, Collection<String> required) {

    SideBySideContext context = new SideBySideContext(actual, required);
    int offset = 0;

    for (Delta<String> delta : context.deltas) {
      Chunk<String> original = delta.getOriginal();
      Chunk<String> revised = delta.getRevised();
      int currentPosition = original.getPosition() + offset;

      switch(delta.getType()) {
        case INSERT:
          offset += context.expand(currentPosition, revised.size());
          context.setRightContent(currentPosition, revised.getLines());
          break;

        case CHANGE:
          int difference = revised.size() - original.size();
          if (difference > 0) {
            offset += context.expand(currentPosition + original.size(), difference);
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

  private SideBySideDiffUtil() {
    throw new AssertionError("not instantiable");
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

    public SideBySideContext(Collection<String> original, Collection<String> revised) {
      List<String> originalList = original instanceof List ? (List<String>) original : new ArrayList<>(original);
      List<String> revisedList = revised instanceof List ? (List<String>) revised : new ArrayList<>(revised);

      this.deltas = DiffUtils.diff(originalList, revisedList).getDeltas();
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

    private int expand(int index, int size) {
      String[] emptyLines = new String[size];
      Arrays.fill(emptyLines, "");
      List<String> emptyLinesAsList = Arrays.asList(emptyLines);

      this.left.addAll(index, emptyLinesAsList);
      this.right.addAll(index, emptyLinesAsList);

      return size;
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
