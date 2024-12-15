package com.github.burravlev.processor;

import com.github.burravlev.context.ApplicationContextGenerator;
import com.github.burravlev.context.BeanDefinition;
import com.github.burravlev.context.BeanDefinitionReader;
import com.github.burravlev.context.DependencyResolver;
import com.github.burravlev.datasource.DatasourceProcessor;
import com.github.burravlev.util.PropertyReader;
import com.google.auto.service.AutoService;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.PrintWriter;
import java.util.List;
import java.util.Set;

@AutoService(Processor.class)
@SupportedAnnotationTypes({
    "com.github.burravlev.annotation.App",
    "com.github.burravlev.annotation.Bean",
    "com.github.burravlev.annotation.Component",
    "com.github.burravlev.annotation.Factory",
    "com.github.burravlev.annotation.Query",
    "com.github.burravlev.annotation.Repository",
    "jakarta.inject.Singleton"
})
@SupportedSourceVersion(SourceVersion.RELEASE_17)
public class AnnotationProcessor extends AbstractProcessor {
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (roundEnv.processingOver()) {
            return false;
        }
        BeanDefinitionReader reader = new BeanDefinitionReader(roundEnv, processingEnv);
        if (reader.getDefinitions().isEmpty()) {
            return false;
        }
        PropertyReader.loadPropertySource(processingEnv.getFiler());

        DatasourceProcessor datasourceProcessor = new DatasourceProcessor(
            processingEnv, roundEnv
        );
        reader.addAll(datasourceProcessor.process());

        DependencyResolver dependencyResolver = new DependencyResolver(reader);
        dependencyResolver.resolve();
        List<BeanDefinition> sorted = reader.getSortedByDependenciesNum();

        try {
            JavaFileObject builderFile = processingEnv.getFiler().createSourceFile(ApplicationContextGenerator.CLASS_NAME);
            try (PrintWriter out = new PrintWriter(builderFile.openWriter())) {
                ApplicationContextGenerator generator = new ApplicationContextGenerator(sorted);
                generator.generateClass().writeTo(out);
            }
        } catch (Exception e) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Failed to build application context: " + e);
        }
        return true;
    }
}
