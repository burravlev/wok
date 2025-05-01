# WOK Dependency Injection

___
Really simple APT-based dependency injection library written for learning purpose

## Quick Start
[Download as maven dependency from GitHub packages](#github-packages)

___

1. Add inject as a dependency.
```xml
<dependency>
    <groupId>com.github.burravlev</groupId>
    <artifactId>inject</artifactId>
    <version>0.0.3</version>
</dependency>
```
2. Add annotation-processor as a dependency with provided scope.

```xml
<dependency>
    <groupId>com.github.burravlev</groupId>
    <artifactId>annotation-processor</artifactId>
    <version>0.0.3</version>
    <scope>provided</scope>
</dependency>
```
3. Add maven compiler plugin with configured annotation processor
```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <version>3.12.1</version>
            <configuration>
                <source>17</source>
                <target>17</target>
                <encoding>UTF-8</encoding>
                <generatedSourcesDirectory>${project.build.directory}/generated-sources/annotations</generatedSourcesDirectory>
                <annotationProcessors>
                    <annotationProcessor>
                        com.github.burravlev.processor.AnnotationProcessor
                    </annotationProcessor>
                </annotationProcessors>
            </configuration>
        </plugin>
    </plugins>
</build>
```

4. Run ```mvn clean install```

5. Create bean class annotated with @Component:

```java 

@Component
public class Example {
}
```

Example factory class:

```java

@Factory
public class ExampleFactory {
    @Bean
    public ExampleBean exampleBean() {
        return new ExampleBean();
    }
}
```

5. App run

```java

@App
public class Main {
    public static void main(String[] args) {
        ApplicationGraph graph = Application.run(Main.class);
    }
}
```

# GitHub packages
Configure settings.xmx (replace username and password with your username and github token)

[Docs](https://docs.github.com/ru/packages/working-with-a-github-packages-registry/working-with-the-apache-maven-registry)

```xml
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0
                      http://maven.apache.org/xsd/settings-1.0.0.xsd">

    <activeProfiles>
        <activeProfile>github</activeProfile>
    </activeProfiles>

    <profiles>
        <profile>
            <id>github</id>
            <repositories>
                <repository>
                    <id>central</id>
                    <url>https://repo1.maven.org/maven2</url>
                </repository>
                <repository>
                    <id>github</id>
                    <url>https://maven.pkg.github.com/burravlev/wok</url>
                    <snapshots>
                        <enabled>true</enabled>
                    </snapshots>
                </repository>
            </repositories>
        </profile>
    </profiles>

    <servers>
        <server>
            <id>github</id>
            <username>{username}</username>
            <password>{TOKEN}</password>
        </server>
    </servers>
</settings>
```