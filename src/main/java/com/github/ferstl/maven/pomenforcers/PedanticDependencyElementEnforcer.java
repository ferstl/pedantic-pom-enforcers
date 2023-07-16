/*
 * Copyright (c) 2012 - 2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.ferstl.maven.pomenforcers;


import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import com.github.ferstl.maven.pomenforcers.priority.PriorityOrdering;
import com.github.ferstl.maven.pomenforcers.util.XmlUtils;
import static com.github.ferstl.maven.pomenforcers.PedanticEnforcerRule.DEPENDENCY_ELEMENT;
import static com.github.ferstl.maven.pomenforcers.util.CommaSeparatorUtils.splitAndAddToCollection;
import static com.google.common.collect.Sets.newLinkedHashSet;
import static java.util.Arrays.asList;

/**
 * This enforcer makes sure that elements in the &lt;dependencyManagement&gt; and &lt;dependencies&gt; sections are ordered.
 * <pre>
 * ### Example
 *     &lt;rules&gt;
 *       &lt;dependencyElements implementation=&quot;com.github.ferstl.maven.pomenforcers.PedanticDependencyElementEnforcer&quot;&gt;
 *         &lt;!-- Define the order within dependencies --&gt;
 *         &lt;elementPriorities&gt;groupId,artifactId,version&lt;/elementPriorities&gt;
 *         &lt;!-- Check the dependency management section --&gt;
 *         &lt;checkDependencyManagement&gt;true&lt;/checkDependencyManagement&gt;
 *         &lt;!-- Check the dependencies section --&gt;
 *         &lt;checkDependencies&gt;true&lt;/checkDependencies&gt;
 *       &lt;/dependencyElements&gt;
 *     &lt;/rules&gt;
 * </pre>
 *
 * @id {@link PedanticEnforcerRule#DEPENDENCY_ELEMENT}
 * @since 2.0.0
 */
public class PedanticDependencyElementEnforcer extends AbstractPedanticEnforcer {

  private static final Set<String> DEFAULT_ORDER = newLinkedHashSet(asList("groupId", "artifactId", "version", "classifier", "type", "scope", "systemPath", "optional", "exclusions"));

  private PriorityOrdering<String, String> elementOrdering;
  private boolean checkDependencies;
  private boolean checkDependencyManagement;

  public PedanticDependencyElementEnforcer() {
    this.elementOrdering = new PriorityOrdering<>(DEFAULT_ORDER, Function.identity());
    this.checkDependencies = true;
    this.checkDependencyManagement = true;
  }


  /**
   * Comma-separated list of section elements in the order as they should appear. This will overwrite the default
   * order by putting all unspecified elements at the end.
   *
   * @param elements Comma separated list of elements as they should appear.
   * @configParam
   * @default groupId, artifactId, version, classifier, type, scope, systemPath, optional, exclusions
   * @since 2.0.0
   */
  public void setElementPriorities(String elements) {
    Set<String> elementPriorities = newLinkedHashSet();
    splitAndAddToCollection(elements, elementPriorities);
    elementPriorities.addAll(DEFAULT_ORDER);

    this.elementOrdering = new PriorityOrdering<>(elementPriorities, Function.identity());
  }

  /**
   * Check the &lt;dependencies&gt; section.
   *
   * @param checkDependencies <code>true</code> to check the &lt;dependencies&gt; section, <code>false</code> else.
   * @configParam
   * @default true
   * @since 2.0.0
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
   * @since 2.0.0
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
      analyzeNodes("dependencyManagement", "/project/dependencyManagement/dependencies/dependency", report);
    }

    if (this.checkDependencies) {
      analyzeNodes("dependencies", "/project/dependencies/dependency", report);
    }
  }

  private void analyzeNodes(String context, String rootPath, ErrorReport errorReport) {
    NodeList nodes = XmlUtils.evaluateXPathAsNodeList(rootPath, getPom());

    List<Map<String, String>> unorderedNodes = new ArrayList<>();
    for (int i = 0; i < nodes.getLength(); i++) {
      Node node = nodes.item(i);
      NodeList nodeElements = node.getChildNodes();

      Map<String, String> elementMap = createElementMap(nodeElements);
      if (!isOrdered(elementMap.keySet())) {
        unorderedNodes.add(elementMap);
      }
    }

    report(context, errorReport, unorderedNodes);
  }

  private Map<String, String> createElementMap(NodeList elements) {
    Map<String, String> elementMap = new LinkedHashMap<>();
    for (int j = 0; j < elements.getLength(); j++) {
      Node element = elements.item(j);

      if (element instanceof Element) {
        elementMap.put(element.getNodeName(), element.getTextContent());
      }
    }

    return elementMap;
  }

  private boolean isOrdered(Collection<String> keys) {
    return this.elementOrdering.isOrdered(keys);
  }

  private void report(String context, ErrorReport errorReport, List<Map<String, String>> unorderedNodes) {
    if (unorderedNodes.isEmpty()) {
      return;
    }

    List<String> actualOrder = new ArrayList<>();
    List<String> requiredOrder = new ArrayList<>();

    for (Map<String, String> elements : unorderedNodes) {
      actualOrder.addAll(prepareForDiff(elements.keySet(), elements));
      requiredOrder.addAll(prepareForDiff(this.elementOrdering.immutableSortedCopy(elements.keySet()), elements));
    }

    errorReport.addLine("<" + context + ">: " + " Dependencies have to be declared this way:");
    errorReport.emptyLine();
    errorReport.addDiff(actualOrder, requiredOrder, "Actual Order", "Required Order");
  }

  private List<String> prepareForDiff(Collection<String> keys, Map<String, String> elementContents) {
    List<String> result = new ArrayList<>(keys.size());
    result.add("<dependency>");
    for (String key : keys) {
      result.add("  <" + key + ">" + elementContents.get(key) + "</" + key + ">");
    }
    result.add("</dependency>");
    result.add("");

    return result;
  }

  @Override
  protected void accept(PedanticEnforcerVisitor visitor) {
    visitor.visit(this);
  }
}
