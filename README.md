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
     <generatedSources>${project.build.directory}/generated-sources/java</generatedSources>
   </properties>
```
3. Create bean class annotated with @Component:

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