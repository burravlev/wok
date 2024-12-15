# WOK 
___
APT-based dependency injection for server-side applications
## Quick Start
___
1. Add dependency 
  ```xml
   <dependency>
     <groupId>com.github.burravlev</groupId>
     <artifactId>wok</artifactId>
     <version>1.0-SNAPSHOT</version>
   </dependency>
   ```
2. Add generated sources property
```xml
   <properties> 
     <generatedSources>${project.build.directory}/generated-sources</generatedSources>
   </properties>
```
3. Add build configuration to project
```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <version>3.13.0</version>
            <configuration>
                <generatedSourcesDirectory>${project.build.directory}/generated-sources/</generatedSourcesDirectory>
                <annotationProcessors>
                    <annotationProcessor>
                        com.github.burravlev.processor.AnnotationProcessor
                    </annotationProcessor>
                </annotationProcessors>
                <annotationProcessorPaths>
                    <annotationProcessorPath>
                        <groupId>com.github.burravlev</groupId>
                        <artifactId>wok</artifactId>
                        <version>0.0.2</version>
                    </annotationProcessorPath>
                </annotationProcessorPaths>
            </configuration>
        </plugin>
    </plugins>
</build>
```
4. Create bean class annotated with @Component:

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