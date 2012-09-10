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

