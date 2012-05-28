package ch.sferstl.maven.pomenforcer;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.maven.enforcer.rule.api.EnforcerRuleException;
import org.apache.maven.enforcer.rule.api.EnforcerRuleHelper;
import org.apache.maven.plugin.logging.Log;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;


public class PedanticPomSectionOrderEnforcer extends AbstractPedanticEnforcer {

  private final Set<PomSection> sectionPriorities;

  public PedanticPomSectionOrderEnforcer() {
    this.sectionPriorities = Sets.newLinkedHashSet();
  }

  public void setSectionPriorities(String sectionPriorities) {
    Function<String, PomSection> transformer = new Function<String, PomSection>() {
      @Override
      public PomSection apply(String input) {
        return PomSection.getBySectionName(input);
      }
    };
    this.splitAndAddToCollection(sectionPriorities, this.sectionPriorities, transformer);
  }

  @Override
  protected void doEnforce(EnforcerRuleHelper helper, Document pom) throws EnforcerRuleException {
    Log log = helper.getLog();
    log.info("Enforcing correct POM section order.");
    log.info("  -> Section priorities: " + COMMA_JOINER.join(this.sectionPriorities));

    // Get the declared POM sections
    NodeList childNodes = pom.getFirstChild().getChildNodes();
    ArrayList<PomSection> pomSections = Lists.newArrayList();
    for (int i = 0; i < childNodes.getLength(); i++) {
      Node node = childNodes.item(i);
      if (node.getNodeType() == Node.ELEMENT_NODE) {
        pomSections.add(PomSection.getBySectionName(node.getNodeName()));
      }
    }

    // The default ordering is the order of the PomSection enum.
    Ordering<PomSection> ordering = Ordering.from(PomSection.createPriorityComparator(this.sectionPriorities));

    if (!ordering.isOrdered(pomSections)) {
      List<String> sortedPomSections =
          Lists.transform(ordering.immutableSortedCopy(pomSections), new Function<PomSection, String>() {
        @Override
        public String apply(PomSection input) {
          return input.getSectionName();
        }
      });
      throw new EnforcerRuleException("One does not simply write a POM file! "
        + "Your POM file has to be organized this way: " + sortedPomSections);
    }
  }

  @Override
  protected void accept(PedanticEnforcerVisitor visitor) {
    visitor.visit(this);
  }

}
