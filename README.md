# Pedantic POM Enforcers
*- One does not simply write a POM file*


Have you ever experienced symptoms like headaches, unfocused anger or a feeling of total resignation when looking at a Maven project where everybody adds and changes stuff just as they need? Do people call you a "POM-Nazi" if you show them how to setup proper and well organized projects?

If so, the *Pedantic POM Enforcers* are absolutely the thing you need!


### What are the Pedantic POM Enforcers?
The *Pedantic POM Enforcers* consist of serveral Maven enforcer rules that help you keep your project setup consistent and organized. For example, the enforcer rules ensure that your POM files are organized in a well-defined manner, that your `<modules>`/`<dependencyManagement>`/`<dependencies>`/`<pluginManagement>` sections are sorted in a reasonable way and that version numbers, plugin configurations, etc. are defined only on places where it makes sense.


### How to use the Pedantic POM Enforcers
The *Pedantic POM Enforcers* are available on [Maven Central](http://central.maven.org/maven2/com/github/ferstl/pedantic-pom-enforcers/). So no further repository configuration is required.

To activate the enforcer rules, just declare them in the configuration of the `maven-enforcer-plugin`. The simplest way of doing this is using the `CompoundPedanticEnforcer`, which is able to aggregate all choosen enforcer rules. The compound enforcer is also more efficient than using the single enforcer rules separately.

    <build>
      <plugins>
        <plugin>
          <groupId>org.apache.maven.plugins</groupId>
          <artifactId>maven-enforcer-plugin</artifactId>
          <version>1.1</version>
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
          <dependencies>
            <dependency>
              <groupId>com.github.ferstl</groupId>
              <artifactId>pedantic-pom-enforcers</artifactId>
              <version>1.0-alpha-4</version>
            </dependency>
          </dependencies>
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
These are the currently available *Pedantic POM Enforcers*. The ID in parentheses is required by the `CompoundPedanticEnforcer`'s configuration. For further details take a look at the [Wiki](pedantic-pom-enforcers/wiki/PedanticEnforcerRule).

| Enforcer Rule | ID | Description |
|:------------- |:--- |:----------- |
| `PedanticPomSectionOrderEnforcer` | `POM_SECTION_ORDER` | Enforces that your POM sections are in order, e.g. 1: `<modelVersion>`, 2: `<groupId>` 3: `<artifactId>`, ...
| `PedanticModuleOrderEnforcer` | `MODULE_ORDER` | Enforces that the declared `<modules>` are ordered alphabetically.
| `PedanticDependencyManagementOrderEnforcer` | `DEPENDENCY_MANAGEMENT_ORDER` | Enforces that the dependencies in your `<dependencyManagement>` are ordered.
| `PedanticDependencyOrderEnforcer` | `DEPENDENCY_ORDER` | Enforces that your `<dependencies>` are ordered.
| `PedanticDependencyConfigurationEnforcer` | `DEPENDENCY_CONFIGURATION` | Enforces that dependency versions and exclusions are defined in `<dependencyManagement>` but not in the `<dependencies>` section.
| `PedanticDependencyScopeEnforcer` | `DEPENDENCY_SCOPE` | Enforces some dependencies to be defined within a specific `<scope>`.
| `PedanticPluginManagementOrderEnforcer` | `PLUGIN_MANAGEMENT_ORDER` | Enforces that the plugins in your `<pluginManagement>` are ordered.
| `PedanticPluginConfigurationEnforcer` | `PLUGIN_CONFIGURATION` | Enforces that plugin versions, configurations and dependencies are declared in `<pluginManagement>` but not in the `<plugins>` section.
| `PedanticPluginManagementLocationEnforcer` | `PLUGIN_MANAGEMENT_LOCATION` | Enforces that `<pluginManagement>` may only be declared in specific POMs.

