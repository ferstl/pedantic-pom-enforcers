package com.github.ferstl.maven.pomenforcers.artifact;

import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import static com.google.common.base.Objects.equal;


@XmlAccessorType(XmlAccessType.FIELD)
public class Artifact {

  @XmlElement(name = "groupId")
  private String groupId;

  @XmlElement(name = "artifactId")
  private String artifactId;

  Artifact() {}

  public Artifact(String groupId, String artifactId) {
    this.groupId = groupId;
    this.artifactId = artifactId;
  }

  public String getGroupId() {
    return this.groupId;
  }

  public String getArtifactId() {
    return this.artifactId;
  }

  @Override
  public String toString() {
    return this.groupId + ":" + this.artifactId;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof Artifact)) {
      return false;
    }

    Artifact other = (Artifact) obj;
    return equal(this.groupId, other.groupId)
        && equal(this.artifactId, other.artifactId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.groupId, this.artifactId);
  }

//  public static void main(String[] args) throws Exception {
//    JAXBContext ctx = JAXBContext.newInstance(Dependencies.class);
//    Unmarshaller unmarshaller = ctx.createUnmarshaller();
//    Dependencies artifact = (Dependencies) unmarshaller.unmarshal(new StringReader(
//        "<dependencies>" +
//          "<dependency>" +
//            "<groupId>com.foo</groupId>" +
//            "<artifactId>my-lib</artifactId>" +
//            "<version>1.0-SNAPSHOT</version>" +
//            "<classifier>test</classifier>" +
//            "<type>zip</type>" +
//          "</dependency>" +
//        "</dependencies>"));
//
//    System.out.println(artifact);
//  }
}
