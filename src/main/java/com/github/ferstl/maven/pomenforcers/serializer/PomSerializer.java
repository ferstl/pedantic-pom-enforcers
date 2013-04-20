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
package com.github.ferstl.maven.pomenforcers.serializer;

import javax.xml.bind.Binder;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.github.ferstl.maven.pomenforcers.model.ProjectModel;

public class PomSerializer {

  private final Document pom;
  private JAXBContext jaxbContext;
  private final Binder<Node> binder;

  public PomSerializer(Document pom) {
    this.pom = pom;
    try {
      this.jaxbContext = JAXBContext.newInstance(ProjectModel.class);
    } catch (JAXBException e) {
      throw new RuntimeException("Unable to create JAXB context", e);
    }
    this.binder = this.jaxbContext.createBinder();
  }


  public ProjectModel read() {
    try {
      return this.binder.unmarshal(this.pom, ProjectModel.class).getValue();
    } catch (JAXBException e) {
      throw new RuntimeException("Unable to read project model", e);
    }
  }

}
