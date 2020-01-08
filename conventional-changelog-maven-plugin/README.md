# Maven plugin

This artifact is the Maven plugin.


## Usage

### Command line

The plugin can be called with a simple command line:

```shell
$ mvn com.anton-johansson:conventional-changelog-maven-plugin:0.0.1:generate -DnumberOfVersions=0
```

### Version lifecycle

It can also be used in combination with the [version lifecycle Maven plugin](https://github.com/anton-johansson/version-lifecycle-maven-plugin):

```xml
    <build>
        <plugins>
            <plugin>
                <groupId>com.anton-johansson</groupId>
                <artifactId>version-lifecycle-maven-plugin</artifactId>
                <version>0.1.0</version>
                <extensions>true</extensions>
                <configuration>
                    <releaseCommitMessagePattern>release: [version]</releaseCommitMessagePattern>
                    <snapshotCommitMessagePattern>chore: Preparing for the next development iteration</snapshotCommitMessagePattern>
                </configuration>
            </plugin>
            <plugin>
                <groupId>com.anton-johansson</groupId>
                <artifactId>conventional-changelog-maven-plugin</artifactId>
                <version>0.1.0</version>
                <executions>
                    <execution>
                        <id>generate-changelog</id>
                        <phase>version-process-release</phase>
                        <goals>
                            <goal>generate</goal>
                        </goals>
                        <configuration>
                            <numberOfVersions>0</numberOfVersions>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
```


## Parameters

| Name                 | Property            | Default value    | Description                                                                |
| -------------------- | ------------------- | ---------------- | -------------------------------------------------------------------------- |
| `numberOfVersions`   | `numberOfVersions`  | `1`              | The number of versions to generate changelog for. Zero means all of them.  |
| `versionTagPrefix`   | `versionTagPrefix`  | `v`              | The version prefix that is used for release tags.                          |
| `fileName`           | `fileName`          | `CHANGELOG.md`   | The output filename.                                                       |
