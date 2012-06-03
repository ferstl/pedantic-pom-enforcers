/*
 * Copyright (c) 2012 by The Author(s)
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
package ch.sferstl.maven.pomenforcer;

import org.apache.maven.enforcer.rule.api.EnforcerRule;
import org.apache.maven.enforcer.rule.api.EnforcerRuleException;
import org.apache.maven.enforcer.rule.api.EnforcerRuleHelper;
import org.apache.maven.project.MavenProject;
import org.w3c.dom.Document;

import ch.sferstl.maven.pomenforcer.util.EnforcerRuleUtils;


public abstract class AbstractPedanticEnforcer implements EnforcerRule {

  @Override
  public void execute(EnforcerRuleHelper helper) throws EnforcerRuleException {
    // Read the POM
    MavenProject project = EnforcerRuleUtils.getMavenProject(helper);
    Document pom = XmlParser.parseXml(project.getFile());

    // Enforce
    doEnforce(helper, pom);
  }

  @Override
  public boolean isCacheable() {
    return false;
  }

  @Override
  public boolean isResultValid(EnforcerRule cachedRule) {
    return false;
  }

  @Override
  public String getCacheId() {
    return getClass() + "-uncachable";
  }

  protected abstract void doEnforce(EnforcerRuleHelper helper, Document pom) throws EnforcerRuleException;

  protected abstract void accept(PedanticEnforcerVisitor visitor);
}
