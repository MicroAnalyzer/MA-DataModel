# MA-DataModel
The internal project and source code model of [MicroAnalyzer](https://github.com/joelBIT/MicroAnalyzer). A parser plugin maps source code
and project metadata into this model. An analysis plugin maps data from its Protocol Buffer representation into AST nodes used for mining tasks.

## How To Compile Sources

If you checked out the project from GitHub you can build the project with maven using:

```
mvn clean install
```

## Usage
Build the plugin jar and place it in the *[plugin_root]/repo/joelbits/mining-model/[version]* folder of the parser or analysis plugin you
create. Add the latest version as a dependency using Maven:

```xml
        <dependency>
            <groupId>joelbits</groupId>
            <artifactId>mining-model</artifactId>
            <version>1.0</version>
        </dependency>
```

