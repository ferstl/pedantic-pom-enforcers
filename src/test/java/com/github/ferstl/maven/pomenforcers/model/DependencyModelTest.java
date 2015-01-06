/*
 * Copyright (c) 2012 - 2015 by Stefan Ferstl <st.ferstl@gmail.com>
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

import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class DependencyModelTest {

  @Test
  public void toStringWithDefaults() {
    DependencyModel model = new DependencyModel("group", "artifact", "1.0.0", null, null, null);

    assertEquals("group:artifact:1.0.0:jar:compile", model.toString());
  }

  @Test
  public void toStringWithClassifier() {
    DependencyModel model = new DependencyModel("group", "artifact", "1.0.0", null, "classifier", null);

    assertEquals("group:artifact:1.0.0:jar:compile:classifier", model.toString());
  }

  @Test
  public void toStringNoDefaults() {
    DependencyModel model = new DependencyModel("group", "artifact", "1.0.0", "test", "classifier", "zip");

    assertEquals("group:artifact:1.0.0:zip:test:classifier", model.toString());
  }

}
