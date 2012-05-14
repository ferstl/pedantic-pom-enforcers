package ch.sferstl.maven.pomenforcer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;

import org.apache.maven.model.Dependency;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;


public class PriorizedGroupIdDependencyComparatorTest {

  private LinkedHashSet<String> priorizedGroupIds;


  @Before
  public void setUp() throws Exception {

    this.priorizedGroupIds = Sets.newLinkedHashSet();
    this.priorizedGroupIds.add("com.foo.bar");
    this.priorizedGroupIds.add("com.foo");
    this.priorizedGroupIds.add("ch.baz.donotmatch");
    this.priorizedGroupIds.add("ch.baz.items");
  }

  @Test
  public void test() {
    ArrayList<Dependency> depList = Lists.newArrayList(
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

    PriorizedGroupIdDependencyComparator comparator = new PriorizedGroupIdDependencyComparator(this.priorizedGroupIds);
    Collections.sort(depList, comparator);
    for (Dependency dependency : depList) {
      System.out.println(dependency.getGroupId());
    }
  }


  private Dependency createDependency(String groupId) {
    Dependency dependency = new Dependency();
    dependency.setGroupId(groupId);
    return dependency;
  }
}
