package com.github.ferstl.maven.pomenforcers.artifact;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "dependency")
public class DependencyInfo extends Artifact {

  @XmlElement(name = "scope")
  private String scope;

  @XmlElement(name = "classifier")
  private String classifier;

  public String getClassifier() {
    return this.classifier;
  }

}
