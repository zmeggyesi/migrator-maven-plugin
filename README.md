# migrator-maven-plugin

Used to generate a Bazel-compatible listing of all dependencies transitively. Provides a listing of `maven_jar()` directives for the `WORKSPACE` file (optionally with SHA1 hashes and source servers), and on request, the relevant `@references` for the `BUILD` files. Can also export the servers used in the project, in case a dependency was not downloaded from Central (Bazel defaults to Central if no server is defined for an artifact).

## Preparation

Clone the repo and run `mvn install` to install the plugin in your local repo before running.

If you're looking to build a specific version, first you'll need to check out the appropriate tag/commit, and change the version number in the POM plugin declaration!

## Injecting

```
<plugin>
	<groupId>hu.skawa</groupId>
	<artifactId>migrator-maven-plugin</artifactId>
	<version>1.1.1</version>
</plugin>
```

## Parameters

- Global
  - `outputFilePrefix[String]`: Determines the prefix of the output files. If set, console output is suppressed and directed to the selected files.
- Dependency Export
  - `outputDirectives[Boolean:true|false]`: Instructs the Migrator to output the `WORKSPACE` directives into a file named `${outputFilePrefix}-${projectName}-directives`
  - `outputReferences[Boolean:true|false]`: Instructs the Migrator to output the `BUILD` directives into a file named `${outputFilePrefix}-${projectName}-references`. Defaults to false.
  - `addHashes[Boolean:true|false]`: Instructs the Migrator to add SHA1 hashes of each artifact to the `WORKSPACE` directives. Defaults to false.
  - `addServers[Boolean:true|false]`: Instructs the Migrator to add the last server where the artifact was downloaded from. Uses only the JAR server, not the POM server, as Bazel only seems to care where the JAR can be downloaded from.

## Goals

- `dependencies`: Export the project dependencies into `WORKSPACE`- and (optionally) `BUILD`-style declarations. Due to the availability of Maven resolution, all dependencies are exported, including transitive ones.
- `servers`: Export only the servers defined for the project into `maven_server()` directives. This includes servers declared in the project POM, *as well as* servers declared in your personal/global `settings.xml` files.  
The export assumes that your personal/global settings are in their default places, since Bazel assumes that as well. Therefore, the settings files are not referenced.

## Use

Run the plugin by executing `mvn hu.skawa:migrator-maven-plugin:${GOAL}`, optionally adding the above parameters.

If you have a modular project, the build *will* invoke the reactor, and the plugin is executed for each project in turn. This will result in multiple output files, each prefixed by your chosen prefix and the project name.

## Known issues

- The plugin relies on the presence of the Aether `_remote.repositories` file beside the artifacts to determine the latest server it was resolved from. If not found, a warning is emitted and the server name will be empty.
