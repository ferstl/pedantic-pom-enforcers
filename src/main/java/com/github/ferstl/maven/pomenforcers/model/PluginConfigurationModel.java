package com.github.ferstl.maven.pomenforcers.model;

import java.util.List;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAnyElement;

import org.w3c.dom.Element;

public class PluginConfigurationModel {

  @XmlAnyElement
  private List<Element> configItems;

  public boolean isConfigured() {
    return this.configItems != null && this.configItems.size() > 0;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (!(obj instanceof PluginConfigurationModel)) {
      return false;
    }

    PluginConfigurationModel other = (PluginConfigurationModel) obj;
    return Objects.equals(this.configItems, other.configItems);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.configItems);
  }
}
