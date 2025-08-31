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
package com.github.ferstl.maven.pomenforcers.model.functions;

import com.google.common.base.Equivalence;


public class StringStartsWithEquivalence extends Equivalence<String> {

  private static final Equivalence<String> INSTANCE = new StringStartsWithEquivalence();

  public static Equivalence<String> stringStartsWith() {
    return INSTANCE;
  }

  @Override
  protected boolean doEquivalent(String a, String b) {
    return a.startsWith(b);
  }

  @Override
  protected int doHash(String t) {
    return t.hashCode();
  }

  private StringStartsWithEquivalence() {
  }

}
