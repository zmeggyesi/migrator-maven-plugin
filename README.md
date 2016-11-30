# migrator-maven-plugin

Used to generate a Bazel-compatible listing of all dependencies transitively. Provides a listing of `maven_jar()` directives for the `WORKSPACE` file, and on request, the relevant `@references` for the `BUILD` files.

## Preparation

- Clone the repo and run `mvn install` to install the plugin in your local repo before running

## Injecting

```
<plugin>
	<groupId>hu.skawa</groupId>
	<artifactId>migrator-maven-plugin</artifactId>
	<version>0.5.0</version>
</plugin>
```

## Parameters

- `outputFilePrefix[String]`: Determines the prefix of the output files. If set, console output is suppressed and directed to the selected files.
- `outputDirectives[String:true|false]`: Instructs the Migrator to output the `WORKSPACE` directives into a file named `${outputFilePrefix}-directives`
- `outputReferences[String:true|false]`: Instructs the Migrator to output the `BUILD` directives into a file named `${outputFilePrefix}-references`

## Use

Run the plugin by executing `mvn hu.skawa:migrator-maven-plugin:transform`, optionally adding the above parameters
