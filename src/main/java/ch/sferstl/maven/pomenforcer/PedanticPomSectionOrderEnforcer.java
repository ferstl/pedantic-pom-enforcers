package ch.sferstl.maven.pomenforcer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.apache.maven.enforcer.rule.api.EnforcerRuleException;
import org.apache.maven.enforcer.rule.api.EnforcerRuleHelper;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;


public class PedanticPomSectionOrderEnforcer extends AbstractPedanticEnforcer {

  @Override
  public void execute(EnforcerRuleHelper helper) throws EnforcerRuleException {
    MavenProject project = this.getMavenProject(helper);

    Log log = helper.getLog();
    log.info("Enforcing correct POM section order.");

    // Read the POM
    Document pomDoc = this.parseXml(project.getFile());

    // Get the declared POM sections
    NodeList childNodes = pomDoc.getFirstChild().getChildNodes();
    ArrayList<PomSection> pomSections = Lists.newArrayList();
    for (int i = 0; i < childNodes.getLength(); i++) {
      Node node = childNodes.item(i);
      if (node.getNodeType() == Node.ELEMENT_NODE) {
        pomSections.add(PomSection.bySectionName(node.getNodeName()));
      }
    }

    // The default ordering is the order of the PomSection enum.
    Ordering<PomSection> ordering = Ordering.from(new Comparator<PomSection>() {
      @Override
      public int compare(PomSection s1, PomSection s2) {
        return s1.compareTo(s2);
      }
    });

    if (!ordering.isOrdered(pomSections)) {
      List<String> sortedPomSections =
          Lists.transform(ordering.immutableSortedCopy(pomSections), new Function<PomSection, String>() {
        @Override
        public String apply(PomSection input) {
          return input.getSectionName();
        }
      });
      throw new EnforcerRuleException("Wrong POM section order. Should be: " + sortedPomSections);
    }


  }

}
