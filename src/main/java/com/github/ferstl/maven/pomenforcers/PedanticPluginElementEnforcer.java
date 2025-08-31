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
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluator;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import com.github.ferstl.maven.pomenforcers.priority.PriorityOrdering;
import com.github.ferstl.maven.pomenforcers.util.XmlUtils;
import static com.github.ferstl.maven.pomenforcers.PedanticEnforcerRule.PLUGIN_ELEMENT;
import static com.github.ferstl.maven.pomenforcers.util.CommaSeparatorUtils.splitAndAddToCollection;
import static com.google.common.collect.Sets.newLinkedHashSet;
import static java.util.Arrays.asList;

/**
 * This enforcer makes sure that elements in the &lt;pluginManagement&gt; and &lt;plugins&gt; sections are ordered.
 * <pre>
 * ### Example
 *     &lt;rules&gt;
 *       &lt;pluginElements implementation=&quot;com.github.ferstl.maven.pomenforcers.PedanticPluginElementEnforcer&quot;&gt;
 *         &lt;!-- Define the order within plugins --&gt;
 *         &lt;elementPriorities&gt;groupId,artifactId,version&lt;/elementPriorities&gt;
 *         &lt;!-- Check the plugin management section --&gt;
 *         &lt;checkPluginManagement&gt;true&lt;/checkPluginManagement&gt;
 *         &lt;!-- Check the plugins section --&gt;
 *         &lt;checkPlugins&gt;true&lt;/checkPlugins&gt;
 *       &lt;/pluginElements&gt;
 *     &lt;/rules&gt;
 * </pre>
 *
 * @id {@link PedanticEnforcerRule#PLUGIN_ELEMENT}
 * @since 2.0.0
 */
@Named("pluginElements")
public class PedanticPluginElementEnforcer extends AbstractPedanticEnforcer {

  private static final Set<String> DEFAULT_ORDER = newLinkedHashSet(asList("groupId", "artifactId", "version", "extensions", "inherited", "configuration", "dependencies", "executions"));

  private PriorityOrdering<String, String> elementOrdering;
  private boolean checkPlugins;
  private boolean checkPluginManagement;

  @Inject
  public PedanticPluginElementEnforcer(final MavenProject project, final ExpressionEvaluator helper) {
    super(project, helper);
    this.elementOrdering = new PriorityOrdering<>(DEFAULT_ORDER, Function.identity());
    this.checkPlugins = true;
    this.checkPluginManagement = true;
  }


  /**
   * Comma-separated list of section elements in the order as they should appear. This will overwrite the default
   * order by putting all unspecified elements at the end.
   *
   * @param elements Comma separated list of elements as they should appear.
   * @configParam
   * @default groupId, artifactId, version, extensions, inherited, configuration, dependencies, executions
   * @since 2.0.0
   */
  public void setElementPriorities(String elements) {
    Set<String> elementPriorities = newLinkedHashSet();
    splitAndAddToCollection(elements, elementPriorities);
    elementPriorities.addAll(DEFAULT_ORDER);

    this.elementOrdering = new PriorityOrdering<>(elementPriorities, Function.identity());
  }

  /**
   * Check the &lt;plugins&gt; section.
   *
   * @param checkPlugins <code>true</code> to check the &lt;plugins&gt; section, <code>false</code> else.
   * @configParam
   * @default true
   * @since 2.0.0
   */
  public void setCheckPlugins(boolean checkPlugins) {
    this.checkPlugins = checkPlugins;
  }

  /**
   * Check the &lt;pluginManagement&gt; section.
   *
   * @param checkPluginManagement <code>true</code> to check the &lt;pluginManagement&gt; section, <code>false</code> else.
   * @configParam
   * @default true
   * @since 2.0.0
   */
  public void setCheckPluginManagement(boolean checkPluginManagement) {
    this.checkPluginManagement = checkPluginManagement;
  }

  @Override
  protected PedanticEnforcerRule getDescription() {
    return PLUGIN_ELEMENT;
  }

  @Override
  protected void doEnforce(ErrorReport report) {
    if (this.checkPluginManagement) {
      analyzeNodes("pluginManagement", "/project/build/pluginManagement/plugins/plugins", report);
    }

    if (this.checkPlugins) {
      analyzeNodes("plugins", "/project/build/plugins/plugin", report);
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

    errorReport.addLine("<" + context + ">: " + " Plugins have to be declared this way:");
    errorReport.emptyLine();
    errorReport.addDiff(actualOrder, requiredOrder, "Actual Order", "Required Order");
  }

  private List<String> prepareForDiff(Collection<String> keys, Map<String, String> elementContents) {
    List<String> result = new ArrayList<>(keys.size());
    result.add("<plugin>");
    for (String key : keys) {
      result.add("  <" + key + ">" + elementContents.get(key) + "</" + key + ">");
    }
    result.add("</plugin>");
    result.add("");

    return result;
  }

  @Override
  protected void accept(PedanticEnforcerVisitor visitor) {
    visitor.visit(this);
  }
}
