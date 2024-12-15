package com.github.burravlev.context;

import org.reflections.Reflections;
import org.reflections.Store;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.QueryFunction;

import static org.reflections.scanners.Scanners.SubTypes;

public abstract class Application {
    private static final QueryFunction<Store, Class<?>> TYPE_QUERY = SubTypes.of(ApplicationGraph.class).asClass();

    public static ApplicationGraph run(Class<?> sourceClass) {
        ConfigurationBuilder reflectionsConfig = new ConfigurationBuilder()
            .forPackage("com.github.burravlev");
        var reflections = new Reflections(reflectionsConfig);
        var applicationContext = loadGeneratedApplicationContext(reflections);
        try {
            return (ApplicationGraph) applicationContext.getDeclaredConstructor()
                .newInstance();
        } catch (Exception e) {
            throw new IllegalStateException("cannot initialize application context: " + e);
        }
    }

    private static Class<?> loadGeneratedApplicationContext(Reflections reflections) {
        return reflections
            .get(TYPE_QUERY)
            .stream()
            .findFirst()
            .orElseThrow(() ->
                new IllegalStateException("cannot find generated application context class")
            );
    }
}