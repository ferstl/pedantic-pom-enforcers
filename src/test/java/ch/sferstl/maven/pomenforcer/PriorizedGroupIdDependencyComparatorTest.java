package ch.sferstl.maven.pomenforcer;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

import org.apache.maven.model.Dependency;
import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;


public class PriorizedGroupIdDependencyComparatorTest {

  private LinkedHashSet<String> priorizedGroupIds;
  private List<Dependency> depList;


  @Before
  public void setUp() throws Exception {

    this.priorizedGroupIds = Sets.newLinkedHashSet();
    this.priorizedGroupIds.add("com.foo.bar");
    this.priorizedGroupIds.add("com.foo");
    this.priorizedGroupIds.add("ch.baz.donotmatch");
    this.priorizedGroupIds.add("ch.baz.items");

    this.depList = Lists.newArrayList(
        this.createDependency("com.foo.bar.some.more.items"),
        this.createDependency("com.foo.bar.some.more.items.b"),
        this.createDependency("xxx"),
        this.createDependency("aaa.bbb.ccc"),
        this.createDependency("com.foo.some.more.items"),
        this.createDependency("ch.baz.items.a"),
        this.createDependency("com.foo.bar.some.more.items"),
        this.createDependency("com.foo.bar"),
        this.createDependency("com.foo.bar.some.more.items.a"),
        this.createDependency("com.foo"),
        this.createDependency("ch.baz.items"));
  }

  @Test
  public void testSorting() {

    PriorizedGroupIdDependencyComparator comparator = new PriorizedGroupIdDependencyComparator(this.priorizedGroupIds);
    Collections.sort(this.depList, comparator);

    Collection<String> sortedGroupIds = this.getGroupIds(this.depList);

    assertThat(sortedGroupIds, contains(
        "com.foo.bar",
        "com.foo.bar.some.more.items",
        "com.foo.bar.some.more.items",
        "com.foo.bar.some.more.items.a",
        "com.foo.bar.some.more.items.b",
        "com.foo",
        "com.foo.some.more.items",
        "ch.baz.items",
        "ch.baz.items.a",
        "aaa.bbb.ccc",
        "xxx"));
  }

  @Test
  public void testNoPriorizedItems() {
    PriorizedGroupIdDependencyComparator comparator =
        new PriorizedGroupIdDependencyComparator(new LinkedHashSet<String>());

    Collections.sort(this.depList, comparator);
    Collection<String> sortedGroupIds = this.getGroupIds(this.depList);
    assertThat(sortedGroupIds, contains(
        "aaa.bbb.ccc",
        "ch.baz.items",
        "ch.baz.items.a",
        "com.foo",
        "com.foo.bar",
        "com.foo.bar.some.more.items",
        "com.foo.bar.some.more.items",
        "com.foo.bar.some.more.items.a",
        "com.foo.bar.some.more.items.b",
        "com.foo.some.more.items",
        "xxx"));

  }

  private Collection<String> getGroupIds(List<Dependency> depList) {
    Collection<String> sortedGroupIds = Collections2.transform(depList, new Function<Dependency, String>() {
      @Override
      public String apply(Dependency input) {
        return input.getGroupId();
      }
    });
    return sortedGroupIds;
  }


  private Dependency createDependency(String groupId) {
    Dependency dependency = new Dependency();
    dependency.setGroupId(groupId);
    return dependency;
  }
}
