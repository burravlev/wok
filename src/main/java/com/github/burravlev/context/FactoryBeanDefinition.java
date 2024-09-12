package com.github.burravlev.context;

import com.squareup.javapoet.CodeBlock;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.util.*;
import java.util.stream.Stream;

public class FactoryBeanDefinition implements BeanDefinition {
    private final String name;
    private final BeanDefinition factory;
    private final ExecutableElement factoryMethod;
    private final String fqn;
    private final List<String> allFqn;
    private final Map<String, BeanDefinition> dependencies = new HashMap<>();
    private String args;

    public FactoryBeanDefinition(BeanDefinition factory, ExecutableElement factoryMethod) {
        this.dependencies.put("$factory", factory);
        this.name = BeanNameExtractor.getName(factoryMethod);
        this.factory = factory;
        this.factoryMethod = factoryMethod;
        this.fqn = factoryMethod.getReturnType().toString();
        this.allFqn = extractAllFqn(getElement(factoryMethod.getReturnType()));
    }

    @Override
    public CodeBlock initExpression() {
        return CodeBlock.of("var $L = $L.$L($L)",
            name, factory.name(), factoryMethod.getSimpleName().toString(),
            args());
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public String fqn() {
        return fqn;
    }

    @Override
    public List<String> allFqn() {
        return List.copyOf(allFqn);
    }

    @Override
    public List<BeanDefinition> dependencies() {
        return dependencies.values()
            .stream()
            .toList();
    }

    @Override
    public List<? extends VariableElement> parameters() {
        return factoryMethod.getParameters();
    }

    @Override
    public void addDependency(String paramName, BeanDefinition dependency) {
        this.dependencies.put(paramName, dependency);
    }

    public List<String> extractAllFqn(TypeElement typeElement) {
        return Stream.concat(
                Stream.of(typeElement),
                Stream.concat(
                    getInterfaces(typeElement).stream(),
                    getSuperclasses(typeElement).stream()
                )
            )
            .map(type -> type.getQualifiedName().toString())
            .toList();
    }

    public Collection<TypeElement> getInterfaces(TypeElement typeElement) {
        return typeElement.getInterfaces()
            .stream()
            .map(this::getElement)
            .toList();
    }

    public Collection<TypeElement> getSuperclasses(TypeElement typeElement) {
        List<TypeElement> superClasses = new ArrayList<>();
        TypeElement current = typeElement;
        while (current.getSuperclass().getKind() == TypeKind.DECLARED) {
            current = getElement(current.getSuperclass());
            superClasses.add(current);
        }
        return superClasses;
    }

    private TypeElement getElement(TypeMirror mirror) {
        return (TypeElement) ((DeclaredType) mirror).asElement();
    }

    private String args() {
        if (args == null) {
            StringBuilder sb = new StringBuilder();
            List<? extends VariableElement> parameters = parameters();
            for (int i = 0; i < parameters.size(); i++) {
                VariableElement parameter = parameters.get(i);
                if (!ValueExtractor.extractValue(sb, parameter)) {
                    sb.append(dependencies.get(parameter.getSimpleName().toString()).name());
                }
                if (i < parameters.size() - 1) {
                    sb.append(", ");
                }
            }
            this.args = sb.toString();
        }
        return this.args;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FactoryBeanDefinition that = (FactoryBeanDefinition) o;
        return Objects.equals(name, that.name) && Objects.equals(factory, that.factory) && Objects.equals(factoryMethod, that.factoryMethod) && Objects.equals(fqn, that.fqn) && Objects.equals(allFqn, that.allFqn) && Objects.equals(dependencies, that.dependencies) && Objects.equals(args, that.args);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, factory, factoryMethod, fqn, allFqn, dependencies, args);
    }
}
