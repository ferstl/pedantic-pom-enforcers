package ch.sferstl.maven.enforcerrules;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.maven.model.Dependency;


public class Main {
  
  public void run(String[] args) {
    List<Dependency> deps = new LinkedList<Dependency>();
    Dependency dependency = new Dependency();
    dependency.setGroupId("com.foo");
    dependency.setScope("compile");
    deps.add(dependency);
    dependency = new Dependency();
    dependency.setGroupId("com.foo");
    dependency.setScope("compile");
    deps.add(dependency);
    dependency = new Dependency();
    dependency.setGroupId("com.bar");
    dependency.setScope("runtime");
    deps.add(dependency);
    dependency = new Dependency();
    dependency.setGroupId("com.bar");
    dependency.setScope("compile");
    deps.add(dependency);
    dependency = new Dependency();
    dependency.setGroupId("com.foo");
    dependency.setScope("compile");
    deps.add(dependency);
    dependency = new Dependency();
    dependency.setGroupId("com.foo");
    dependency.setScope("runtime");
    deps.add(dependency);
    
    Collections.sort(deps, new CompositeComparator<Dependency>(
        DependencyComparator.SCOPE, DependencyComparator.GROUP_ID));
    for (Dependency dep : deps) {
      System.out.println(dep.getGroupId() + ":" + dep.getScope());
    }
  }
  
  public static void main(String[] args) {
    new Main().run(args);
  }
}
