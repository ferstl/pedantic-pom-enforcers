package com.github.ferstl.maven.pomenforcers;


import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import com.github.ferstl.maven.pomenforcers.priority.PriorityOrdering;
import com.github.ferstl.maven.pomenforcers.util.XmlUtils;
import com.google.common.base.Functions;

import static com.github.ferstl.maven.pomenforcers.PedanticEnforcerRule.DEPENDENCY_ELEMENT;
import static java.util.Arrays.asList;

public class PedanticDependencyElementEnforcer extends AbstractPedanticEnforcer {

  @Override
  protected PedanticEnforcerRule getDescription() {
    return DEPENDENCY_ELEMENT;
  }

  @Override
  protected void doEnforce(ErrorReport report) {
    List<String> orederedDependencyElements = asList("groupId", "artifactId", "version", "classifier", "type", "scope", "systemPath", "optional", "exclusions");
    PriorityOrdering<String, String> dependencyElementOrdering = new PriorityOrdering<>(orederedDependencyElements, Functions.<String>identity());

    NodeList dependencies = XmlUtils.evaluateXPathAsNodeList("/project/dependencies/dependency", getPom());
    for (int i = 0; i < dependencies.getLength(); i++) {
      Node dependency = dependencies.item(i);
      NodeList dependencyElements = dependency.getChildNodes();
      Map<String, String> dependencyContents = new LinkedHashMap<>();
      for (int j = 0; j < dependencyElements.getLength(); j++) {
        Node dependencyElement = dependencyElements.item(j);

        if (dependencyElement instanceof Element) {
          dependencyContents.put(dependencyElement.getNodeName(), dependencyElement.getTextContent());
        }
      }

      if (!dependencyElementOrdering.isOrdered(dependencyContents.keySet())) {
        List<String> requiredOrder = dependencyElementOrdering.sortedCopy(dependencyContents.keySet());
        report.addDiff(dependencyContents.keySet(), requiredOrder, "Actual Order", "Required Order");
        report.addLine("");
      }
    }
  }

  @Override
  protected void accept(PedanticEnforcerVisitor visitor) {
  }
}
