# WOK

___
APT-based dependency injection for server-side applications

## Quick Start

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

3. Run ```mvn clean install```

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