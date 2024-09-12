package com.github.burravlev.context;

import com.github.burravlev.annotation.Component;
import com.github.burravlev.annotation.Factory;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import java.util.*;
import java.util.stream.Collectors;

public class BeanDefinitionReader {
    private final List<BeanDefinition> definitions = new ArrayList<>();
    private final Map<String, List<BeanDefinition>> definitionsByFqn;

    public BeanDefinitionReader(RoundEnvironment roundEnv, ProcessingEnvironment pe) {
        definitions.addAll(loadSingletonDefinitions(roundEnv));
        definitions.addAll(loadFactoryDefinition(roundEnv));
        this.definitionsByFqn = parseDefinitionsFqn();
    }

    public void addAll(List<BeanDefinition> definitions) {
        this.definitions.addAll(definitions);
        for (BeanDefinition definition : definitions) {
            for (String fqn : definition.allFqn()) {
                definitionsByFqn.computeIfAbsent(fqn, k -> new ArrayList<>())
                    .add(definition);
            }
        }
    }

    private List<? extends BeanDefinition> loadSingletonDefinitions(RoundEnvironment roundEnv) {
        return roundEnv.getElementsAnnotatedWith(Component.class)
            .stream()
            .filter(el -> el.asType().getKind() == TypeKind.DECLARED)
            .map(el -> new DefaultBeanDefinition((TypeElement) el))
            .toList();
    }

    private List<? extends BeanDefinition> loadFactoryDefinition(RoundEnvironment roundEnv) {
        List<? extends TypeElement> factories = roundEnv.getElementsAnnotatedWith(Factory.class)
            .stream()
            .filter(el -> el.asType().getKind() == TypeKind.DECLARED)
            .map(el -> (TypeElement) el)
            .toList();
        List<BeanDefinition> definitions = factories.stream()
            .map(DefaultBeanDefinition::new)
            .collect(Collectors.toList());
        for (int i = 0; i < factories.size(); i++) {
            TypeElement factory = factories.get(i);
            BeanDefinition definition = definitions.get(i);
            List<? extends ExecutableElement> methods = factory.getEnclosedElements()
                .stream()
                .filter(el -> el.getKind() == ElementKind.METHOD)
                .map(ExecutableElement.class::cast)
                .toList();
            for (ExecutableElement method : methods) {
                definitions.add(new FactoryBeanDefinition(definition, method));
            }
        }
        return definitions;
    }

    public List<BeanDefinition> getDefinitions() {
        return definitions;
    }

    List<BeanDefinition> getDefinitionsByFqn(String fqn) {
        return definitionsByFqn.getOrDefault(fqn, new ArrayList<>());
    }

    public List<BeanDefinition> getSortedByDependenciesNum() {
        Map<String, Long> numByFqn = new HashMap<>();
        for (BeanDefinition definition : definitions) {
            getNumDependencies(numByFqn, definition);
        }
        return definitions.stream()
            .sorted(Comparator.comparing(definition -> numByFqn.getOrDefault(definition.fqn(), 0L)))
            .toList();
    }

    private Long getNumDependencies(Map<String, Long> numByFqn, BeanDefinition definition) {
        Long SENTINEL = -123L;

        Long prevNumDeps = numByFqn.get(definition.fqn());
        if (SENTINEL.equals(prevNumDeps)) throw new RuntimeException("Circular dependency!");
        if (prevNumDeps != null) return prevNumDeps;

        numByFqn.put(definition.fqn(), SENTINEL);
        long numDependencies = 0;
        for (BeanDefinition dependency : definition.dependencies()) {
            numDependencies += (1 + getNumDependencies(numByFqn, dependency));
        }
        numByFqn.put(definition.fqn(), numDependencies);
        return numDependencies;
    }

    private Map<String, List<BeanDefinition>> parseDefinitionsFqn() {
        Map<String, List<BeanDefinition>> definitionsByFqn = new HashMap<>();
        for (BeanDefinition definition : definitions) {
            for (String fqn : definition.allFqn()) {
                definitionsByFqn.computeIfAbsent(fqn, k -> new ArrayList<>())
                    .add(definition);
            }
        }
        return definitionsByFqn;
    }

    @Override
    public String toString() {
        return "BeanResolver{\n" +
            "definitions=" + definitions +
            "\n, definitionsByFqn=" + definitionsByFqn +
            "\n}";
    }
}