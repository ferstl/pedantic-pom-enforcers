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
@XmlSchema(
    elementFormDefault=XmlNsForm.UNQUALIFIED,
    attributeFormDefault=XmlNsForm.UNQUALIFIED,
    namespace = "http://maven.apache.org/POM/4.0.0",
    location = "http://maven.apache.org/xsd/maven-4.0.0.xsd",
    xmlns = @XmlNs(namespaceURI = "http://maven.apache.org/POM/4.0.0", prefix = ""))
@XmlAccessorType(FIELD)
package com.github.ferstl.maven.pomenforcers.model;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlNs;
import javax.xml.bind.annotation.XmlNsForm;
import javax.xml.bind.annotation.XmlSchema;

import static javax.xml.bind.annotation.XmlAccessType.FIELD;

