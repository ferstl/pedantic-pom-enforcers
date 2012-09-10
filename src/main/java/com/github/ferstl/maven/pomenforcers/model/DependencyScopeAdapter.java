package com.github.ferstl.maven.pomenforcers.model;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.github.ferstl.maven.pomenforcers.DependencyScope;


class DependencyScopeAdapter extends XmlAdapter<String, DependencyScope> {

  @Override
  public DependencyScope unmarshal(String v) throws Exception {
    return DependencyScope.getByScopeName(v);
  }

  @Override
  public String marshal(DependencyScope v) throws Exception {
    return v.getScopeName();
  }

}
