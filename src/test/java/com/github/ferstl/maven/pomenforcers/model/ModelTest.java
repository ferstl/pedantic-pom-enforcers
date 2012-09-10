package com.github.ferstl.maven.pomenforcers.model;

import java.io.File;

import javax.xml.bind.Binder;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.github.ferstl.maven.pomenforcers.util.XmlUtils;

public class ModelTest {

  @Test
  public void test() throws Exception {
    Document pom = XmlUtils.parseXml(new File("src/it/projects/example-project/pom.xml"));
    JAXBContext ctx = JAXBContext.newInstance(ProjectModel.class);

    Binder<Node> binder = ctx.createBinder();
    JAXBElement<ProjectModel> projectElement = binder.unmarshal(pom, ProjectModel.class);

    System.out.println(projectElement.getValue());
  }

}
