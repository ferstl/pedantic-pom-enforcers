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
package com.github.ferstl.maven.pomenforcers.reader;

import java.util.List;

import org.apache.maven.model.Dependency;
import org.w3c.dom.Document;

import com.github.ferstl.maven.pomenforcers.artifact.DependencyInfo;
import com.thoughtworks.xstream.XStream;


public class DeclaredDependenciesReader extends AbstractPomSectionReader<DependencyInfo> {

  private static final String DEPENDENCIES_ALIAS = "dependencies";
  private static final String DEPENDENCY_ALIAS = "dependency";

  public DeclaredDependenciesReader(Document pom) {
    super(pom, DependencyInfo.class);
  }

  @Override
  protected void configureXStream(XStream xstream) {
    xstream.alias(DEPENDENCIES_ALIAS, List.class);
    xstream.alias(DEPENDENCY_ALIAS, DependencyInfo.class);
    xstream.omitField(Dependency.class, "exclusions");
  }

}
