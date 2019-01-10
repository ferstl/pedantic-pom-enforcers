/*
 * Copyright (c) 2012 - 2019 the original author or authors.
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
package com.github.ferstl.maven.pomenforcers.model;

import java.io.File;
import javax.xml.bind.Binder;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import com.github.ferstl.maven.pomenforcers.util.XmlUtils;

import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

public class ModelTest {

  @Test
  public void test() throws Exception {
    Document pom = XmlUtils.parseXml(new File("src/test/projects/example-project/pom.xml"));
    JAXBContext ctx = JAXBContext.newInstance(ProjectModel.class);

    Binder<Node> binder = ctx.createBinder();
    JAXBElement<ProjectModel> projectModel = binder.unmarshal(pom, ProjectModel.class);

    assertThat(projectModel, not(nullValue()));
  }

}
