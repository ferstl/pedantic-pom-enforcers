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
