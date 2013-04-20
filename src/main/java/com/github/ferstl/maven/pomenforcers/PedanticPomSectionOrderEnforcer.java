/*
 * Copyright (c) 2013 by Stefan Ferstl <st.ferstl@gmail.com>
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
import java.util.List;
import java.util.Set;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.github.ferstl.maven.pomenforcers.model.PomSection;
import com.github.ferstl.maven.pomenforcers.util.CommaSeparatorUtils;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;

import static com.github.ferstl.maven.pomenforcers.ErrorReport.toList;
import static com.github.ferstl.maven.pomenforcers.model.PomSection.pomSectionToString;
import static com.github.ferstl.maven.pomenforcers.model.PomSection.stringToPomSection;



/**
 * This enforcer makes sure that the sections in your POM files are in a defined order.
 * <pre>
 * ### Example
 *     &lt;rules&gt;
 *       &lt;pomSectionOrder implementation=&quot;ch.sferstl.maven.pomenforcer.PedanticPomSectionOrderEnforcer&quot;&gt;
 *         &lt;!-- Use project coordinates before parent declaration --&gt;
 *         &lt;sectionPriorities&gt;groupId,artifactId,version,packaging&lt;/sectionPriorities&gt;
 *       &lt;/pomSectionOrder&gt;
 *     &lt;/rules&gt;
 * </pre>
 * @id {@link PedanticEnforcerRule#POM_SECTION_ORDER}
 */
public class PedanticPomSectionOrderEnforcer extends AbstractPedanticEnforcer {

  private final Set<PomSection> sectionPriorities;

  public PedanticPomSectionOrderEnforcer() {
    this.sectionPriorities = Sets.newLinkedHashSet();
  }

  /**
   * Comma separated list of section priorities.
   *
   * @param sectionPriorities Comma separated list of section priorities.
   * @configParam
   * @default modelVersion,prerequisites,parent,groupId,artifactId,version
   *          ,packaging,name,description,url,licenses,organization
   *          ,inceptionYear,ciManagement,mailingLists,issueManagement,
   *          developers ,contributors,scm,repositories,pluginRepositories
   *          ,distributionManagement ,modules,properties,dependencyManagement
   *          ,dependencies,build,profiles,reporting,reports
   */
  public void setSectionPriorities(String sectionPriorities) {
    CommaSeparatorUtils.splitAndAddToCollection(sectionPriorities, this.sectionPriorities, stringToPomSection());
  }

  @Override
  protected PedanticEnforcerRule getDescription() {
    return PedanticEnforcerRule.POM_SECTION_ORDER;
  }

  @Override
  protected void accept(PedanticEnforcerVisitor visitor) {
    visitor.visit(this);
  }

  @Override
  protected void doEnforce(ErrorReport report) {
    Node docElement = getPom().getDocumentElement();
    NodeList sectionNodes = docElement.getChildNodes();
    ArrayList<PomSection> pomSections = new ArrayList<>();
    for (int i = 0; i < sectionNodes.getLength(); i++) {
      Node node = sectionNodes.item(i);
      if (node.getNodeType() == Node.ELEMENT_NODE) {
        pomSections.add(PomSection.getBySectionName(node.getNodeName()));
      }
    }

    // The default ordering is the order of the PomSection enum.
    Ordering<PomSection> ordering = PomSection.createPriorityOrdering(this.sectionPriorities);

    if (!ordering.isOrdered(pomSections)) {
      List<PomSection> sortedPomSections = ordering.immutableSortedCopy(pomSections);

      report.addLine("Your POM is currently organized like this:")
            .addLine(toList(pomSections, pomSectionToString()))
            .emptyLine()
            .addLine("... but it has to be organized this way:")
            .addLine(toList(sortedPomSections, pomSectionToString()));
    }
  }

}
