package com.github.ferstl.maven.pomenforcers;


import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import com.github.ferstl.maven.pomenforcers.priority.PriorityOrdering;
import com.github.ferstl.maven.pomenforcers.util.XmlUtils;
import com.google.common.base.Functions;

import static com.github.ferstl.maven.pomenforcers.PedanticEnforcerRule.DEPENDENCY_ELEMENT;
import static java.util.Arrays.asList;

public class PedanticDependencyElementEnforcer extends AbstractPedanticEnforcer {

  private final List<String> orderedElements;
  private final PriorityOrdering<String, String> elementOrdering;

  public PedanticDependencyElementEnforcer() {
    this.orderedElements = asList("artifactId", "groupId", "version", "classifier", "type", "scope", "systemPath", "optional", "exclusions");
    this.elementOrdering = new PriorityOrdering<>(this.orderedElements, Functions.<String>identity());
  }

  @Override
  protected PedanticEnforcerRule getDescription() {
    return DEPENDENCY_ELEMENT;
  }

  @Override
  protected void doEnforce(ErrorReport report) {
    NodeList nodes = XmlUtils.evaluateXPathAsNodeList("/project/dependencies/dependency", getPom());

    for (int i = 0; i < nodes.getLength(); i++) {
      Node node = nodes.item(i);
      NodeList nodeElements = node.getChildNodes();

      Map<String, String> nodeContents = analyzeElements(nodeElements);
      reportIfUnordered(report, nodeContents);
    }
  }

  private Map<String, String> analyzeElements(NodeList elements) {
    Map<String, String> elementContents = new LinkedHashMap<>();
    for (int j = 0; j < elements.getLength(); j++) {
      Node element = elements.item(j);

      if (element instanceof Element) {
        elementContents.put(element.getNodeName(), element.getTextContent());
      }
    }
    return elementContents;
  }

  private void reportIfUnordered(ErrorReport report, Map<String, String> elementContents) {
    Set<String> elementNames = elementContents.keySet();
    if (!this.elementOrdering.isOrdered(elementNames)) {
      List<String> actualOrder = prepareForDiff(elementNames, elementContents);
      List<String> requiredOrder = prepareForDiff(this.elementOrdering.immutableSortedCopy(elementNames), elementContents);

      report.addDiff(actualOrder, requiredOrder, "Actual Order", "Required Order");
      report.addLine("");
    }
  }

  private List<String> prepareForDiff(Collection<String> keys, Map<String, String> elementContents) {
    List<String> result = new ArrayList<>(keys.size());
    for (String key : keys) {
      result.add("  <" + key + ">" + elementContents.get(key) + "</" + key + ">");
    }

    return result;
  }

  @Override
  protected void accept(PedanticEnforcerVisitor visitor) {
  }
}
