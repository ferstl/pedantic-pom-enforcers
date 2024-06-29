# Pedantic POM Enforcers

*- One does not simply write a POM file*

[![Build Status](https://github.com/ferstl/pedantic-pom-enforcers/actions/workflows/maven.yml/badge.svg)](https://github.com/ferstl/pedantic-pom-enforcers/actions/workflows/maven.yml) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.ferstl/pedantic-pom-enforcers/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.ferstl/pedantic-pom-enforcers) [![Coverage Status](https://coveralls.io/repos/github/ferstl/pedantic-pom-enforcers/badge.svg?branch=master)](https://coveralls.io/github/ferstl/pedantic-pom-enforcers?branch=master) [![license](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

Have you ever experienced symptoms like headaches, unfocused anger or a feeling of total resignation when looking at a Maven project where everybody adds and changes stuff just as they need? Do people call you a "POM-Nazi" if you show them how to setup proper and well organized projects?

If so, the *Pedantic POM Enforcers* are absolutely the thing you need!

### What are the Pedantic POM Enforcers?

The *Pedantic POM Enforcers* consist of several Maven enforcer rules that help you keep your project setup consistent and organized. For example, the enforcer rules ensure that your POM files are organized in a well-defined manner, that your `<modules>`/`<dependencyManagement>`/`<dependencies>`/`<pluginManagement>` sections are sorted in a reasonable way and that version numbers, plugin configurations, etc. are defined only in places where it makes sense.

### Release Notes / Solved Issues

- [Version 2.2.0](https://github.com/ferstl/pedantic-pom-enforcers/releases/tag/pedantic-pom-enforcers-2.2.0) (2023-07-16, Feature Release)
- [Version 2.1.0](https://github.com/ferstl/pedantic-pom-enforcers/releases/tag/pedantic-pom-enforcers-2.1.0) (2021-09-02, Feature Release)
- [Version 2.0.0](https://github.com/ferstl/pedantic-pom-enforcers/releases/tag/pedantic-pom-enforcers-2.0.0) (2020-08-26, Feature Release, JDK8 baseline)
- [Version 1.3.2](https://github.com/ferstl/pedantic-pom-enforcers/releases/tag/pedantic-pom-enforcers-1.3.2) (2019-01-08, Bugfix Release)
- [Version 1.3.1](https://github.com/ferstl/pedantic-pom-enforcers/releases/tag/pedantic-pom-enforcers-1.3.1) (2017-11-12, Bugfix Release)
- [Version 1.3.0](https://github.com/ferstl/pedantic-pom-enforcers/releases/tag/pedantic-pom-enforcers-1.3.0) (2017-10-31, Java 9 Support)
- [Version 1.2.0](https://github.com/ferstl/pedantic-pom-enforcers/issues?q=milestone%3A%22Version+1.2.0%22+is%3Aclosed) (2015-01-06, Feature Release)
- [Version 1.1.2](https://github.com/ferstl/pedantic-pom-enforcers/issues?milestone=1&state=closed) (2014-02-15, Bugfix Release)

### How to use the Pedantic POM Enforcers

The *Pedantic POM Enforcers* are available on [Maven Central](https://repo1.maven.org/maven2/com/github/ferstl/pedantic-pom-enforcers/). So no further repository configuration is required.

To activate the enforcer rules, just declare them in the configuration of the [`maven-enforcer-plugin`](http://maven.apache.org/enforcer/maven-enforcer-plugin/). The simplest way of doing this is using the [`CompoundPedanticEnforcer`](https://github.com/ferstl/pedantic-pom-enforcers/wiki/CompoundPedanticEnforcer), which is able to aggregate all chosen enforcer rules. The compound enforcer is also more efficient than using the single enforcer rules separately.

    <build>
      <plugins>
        <plugin>
          <groupId>org.apache.maven.plugins</groupId>
          <artifactId>maven-enforcer-plugin</artifactId>
          <version>3.3.0</version>
          <!-- Add the pedantic-pom-enforcers as dependency -->
          <dependencies>
            <dependency>
              <groupId>com.github.ferstl</groupId>
              <artifactId>pedantic-pom-enforcers</artifactId>
              <version>2.2.0</version>
            </dependency>
          </dependencies>
          <!-- Configure the enforcer rules -->
          <executions>
            <execution>
              <id>pedantic-pom-enforcers</id>
              <phase>validate</phase>
              <goals>
                <goal>enforce</goal>
              </goals>
              <configuration>
                <rules>
                  <compound implementation="com.github.ferstl.maven.pomenforcers.CompoundPedanticEnforcer">
                    <!-- Enforcers -->
                    <enforcers>POM_SECTION_ORDER,MODULE_ORDER,DEPENDENCY_MANAGEMENT_ORDER,DEPENDENCY_ORDER,PLUGIN_MANAGEMENT_ORDER</enforcers>
                  
                    <!-- Further configuration of the declared enforcer rules -->
                    ...
                  </compound>
                </rules>
                <fail>true</fail>
              </configuration>
            </execution>
          </executions>
        </plugin>
        ...
      </plugins>
    </build>

#### Why comma-separated Strings?

You may wonder why comma-separated strings are used to configure the enforcer rules although the maven-enforcer-plugin supports structured configurations like

    <!-- Note: This configuration is not valid for the Pedantic POM Enforcers! -->
    <enforcers>
      <enforcer>POM_SECTION_ORDER</enforcer>
      <enforcer>MODULE_ORDER</enforcer>
      ...
    </enforcers>

The reason is simple: The comma-separated strings can be defined as `<properties>` in a POM file. In case a submodule requires a different configuration, the properties can easily be overwritten in the submodule's POM. This allows to add or remove specific enforcer rules or to alter configuration values without redefining the whole plugin configuration.

### Available Enforcer Rules

These are the currently available *Pedantic POM Enforcers*. The ID in parentheses is required by the `CompoundPedanticEnforcer`'s configuration. For further details take a look at the [Wiki](https://github.com/ferstl/pedantic-pom-enforcers/wiki/PedanticEnforcerRule).

| Enforcer                                                                                     | ID                             | Description                                                                                                            |
|:---------------------------------------------------------------------------------------------|:-------------------------------|:-----------------------------------------------------------------------------------------------------------------------|
| [CompoundPedanticEnforcer](CompoundPedanticEnforcer)                                         | n/a                            | Used to aggregate several pedantic enforcer rules.                                                                     |
| [PedanticPomSectionOrderEnforcer](PedanticPomSectionOrderEnforcer)                           | POM_SECTION_ORDER              | This enforcer makes sure that the sections in your POM files are in a defined order.                                   |
| [PedanticModuleOrderEnforcer](PedanticModuleOrderEnforcer)                                   | MODULE_ORDER                   | This enforcer makes sure that your `<modules>` section is sorted alphabetically.                                         |
| [PedanticDependencyManagementOrderEnforcer](PedanticDependencyManagementOrderEnforcer)       | DEPENDENCY_MANAGEMENT_ORDER    | This enforcer makes sure that all artifacts in your dependency management are ordered.                                 |
| [PedanticDependencyManagementLocationEnforcer](PedanticDependencyManagementLocationEnforcer) | DEPENDENCY_MANAGEMENT_LOCATION | Enforces that only a well-defined set of POMs may declare dependency management.                                       |
| [PedanticDependencyOrderEnforcer](PedanticDependencyOrderEnforcer)                           | DEPENDENCY_ORDER               | This enforcer makes sure that all artifacts in your dependencies section are ordered.                                  |
| [PedanticDependencyConfigurationEnforcer](PedanticDependencyConfigurationEnforcer)           | DEPENDENCY_CONFIGURATION       | This enforcer makes sure that dependency versions and exclusions are declared in the `<dependencyManagement>` section. |
| [PedanticDependencyElementEnforcer](PedanticDependencyElementEnforcer)                       | DEPENDENCY_ELEMENT             | This enforcer makes sure that elements in the `<dependencyManagement>` and `<dependencies>` sections are ordered.          |
| [PedanticDependencyScopeEnforcer](PedanticDependencyScopeEnforcer)                           | DEPENDENCY_SCOPE               | Enforces that the configured dependencies have to be defined within a specific scope.                                  |
| [PedanticPluginManagementOrderEnforcer](PedanticPluginManagementOrderEnforcer)               | PLUGIN_MANAGEMENT_ORDER        | This enforcer makes sure that all plugins in your plugin management section are ordered.                               |
| [PedanticPluginConfigurationEnforcer](PedanticPluginConfigurationEnforcer)                   | PLUGIN_CONFIGURATION           | Enforces that plugin versions, configurations and dependencies are defined in the `<pluginManagement>` section.        |
| [PedanticPluginElementEnforcer](PedanticPluginElementEnforcer)                               | PLUGIN_ELEMENT                 | This enforcer makes sure that elements in the `<pluginManagement>` and `<plugins>` sections are ordered.                   |
| [PedanticPluginManagementLocationEnforcer](PedanticPluginManagementLocationEnforcer)         | PLUGIN_MANAGEMENT_LOCATION     | Enforces that only a well-defined set of POMs may declare plugin management.                                           |

