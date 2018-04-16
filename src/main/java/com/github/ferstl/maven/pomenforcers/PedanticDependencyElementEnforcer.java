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
import static com.github.ferstl.maven.pomenforcers.util.CommaSeparatorUtils.splitAndAddToCollection;
import static com.google.common.collect.Sets.newLinkedHashSet;
import static java.util.Arrays.asList;

public class PedanticDependencyElementEnforcer extends AbstractPedanticEnforcer {

  private static final Set<String> DEFAULT_ORDER = newLinkedHashSet(asList("groupId", "artifactId", "version", "classifier", "type", "scope", "systemPath", "optional", "exclusions"));

  private PriorityOrdering<String, String> elementOrdering;
  private boolean checkDependencies;
  private boolean checkDependencyManagement;

  public PedanticDependencyElementEnforcer() {
    this.elementOrdering = new PriorityOrdering<>(DEFAULT_ORDER, Functions.<String>identity());
    this.checkDependencies = true;
    this.checkDependencyManagement = true;
  }


  /**
   * Comma-separated list of section elements in the order as they should appear. This will overwrite the default
   * order by putting all unspecified elements at the end.
   *
   * @param elements Comma separated list of elements as they should appear.
   * @configParam
   * @default n/a
   * @since 1.4.0
   */
  public void setElementPriorities(String elements) {
    Set<String> elementPriorities = newLinkedHashSet();
    splitAndAddToCollection(elements, elementPriorities);
    elementPriorities.addAll(DEFAULT_ORDER);

    this.elementOrdering = new PriorityOrdering<>(elementPriorities, Functions.<String>identity());
  }

  /**
   * Check the &lt;dependencies&gt; section.
   *
   * @param checkDependencies <code>true</code> to check the &lt;dependencies&gt; section, <code>false</code> else.
   * @configParam
   * @default true
   * @since 1.4.0
   */
  public void setCheckDependencies(boolean checkDependencies) {
    this.checkDependencies = checkDependencies;
  }

  /**
   * Check the &lt;dependencyManagement&gt; section.
   *
   * @param checkDependencyManagement <code>true</code> to check the &lt;dependencyManagement&gt; section, <code>false</code> else.
   * @configParam
   * @default true
   * @since 1.4.0
   */
  public void setCheckDependencyManagement(boolean checkDependencyManagement) {
    this.checkDependencyManagement = checkDependencyManagement;
  }

  @Override
  protected PedanticEnforcerRule getDescription() {
    return DEPENDENCY_ELEMENT;
  }

  @Override
  protected void doEnforce(ErrorReport report) {
    if (this.checkDependencyManagement) {
      analyzeNodes("/project/dependencyManagement/dependencies/dependency", report);
    }

    if (this.checkDependencies) {
      analyzeNodes("/project/dependencies/dependency", report);
    }
  }

  private void analyzeNodes(String rootPath, ErrorReport report) {
    NodeList nodes = XmlUtils.evaluateXPathAsNodeList(rootPath, getPom());

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
    visitor.visit(this);
  }
}
