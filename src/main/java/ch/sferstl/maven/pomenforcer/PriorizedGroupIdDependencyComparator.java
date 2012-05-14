package ch.sferstl.maven.pomenforcer;

import java.util.Comparator;
import java.util.Set;

import org.apache.maven.model.Dependency;

public class PriorizedGroupIdDependencyComparator implements Comparator<Dependency> {

  private final Set<String> priorizedGroupIds;

  public PriorizedGroupIdDependencyComparator(Set<String> firstGroupIdSet) {
    this.priorizedGroupIds = firstGroupIdSet;
  }

  @Override
  public int compare(Dependency d1, Dependency d2) {
    int rankD1 = this.priorize(d1.getGroupId());
    int rankD2 = this.priorize(d2.getGroupId());

    if (rankD1 == rankD2) {
      return d1.getGroupId().compareTo(d2.getGroupId());
    }
    return rankD1 - rankD2;

  }

  /**
   * Determine the priority of the given group ID by matching it against the
   * priorized group IDs. Lower numbers are more important than higher numbers.
   * @param groupId The group ID to priorize.
   * @return The priority of the given group ID or {@link Integer#MAX_VALUE} if
   *         the given group ID does not match any of the priorized group IDs.
   */
  private int priorize(String groupId) {
    int i = 0;
    for (String groupIdStart : this.priorizedGroupIds) {
      if (groupId.startsWith(groupIdStart)) {
        return i;
      }
      i++;
    }
    return Integer.MAX_VALUE;
  }

}