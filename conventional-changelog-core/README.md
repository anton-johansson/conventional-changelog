# Java library

This artifact is the regular Java library. It is used by the Maven plugin, but it is also perfectly fine to use it standalone.


## Usage

### Maven dependency

Add this to your POM:

```xml
    <dependency>
        <groupId>com.anton-johansson</groupId>
        <artifactId>conventional-changelog-core</artifactId>
        <version>0.1.0</version>
    </dependency>
```

### Get changes from Git metadata

```java
File repository = new File("~/projects/my-git-project");
List<ChangeSet> changeSets = new ChangeSetCollector(repository)
        .numberOfVersions(5)
        .versionTagPrefix("v")
        .collect();
```

### Write changelog

```java
try (ChangeLogWriter writer = new ChangeLogWriter(changeSets))
{
    writer
            .fileName("~/my-changelog.md")
            .write();
}
```
