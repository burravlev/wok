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

public class DefaultBeanDefinition implements BeanDefinition {
    private final TypeElement typeElement;
    private final String name;
    private final String fqn;
    private final List<String> allFqn;
    private final Map<String, BeanDefinition> dependencies = new HashMap<>();
    private String args;
    private final ExecutableElement constructor;

    public DefaultBeanDefinition(TypeElement type) {
        this.typeElement = type;
        this.name = BeanNameExtractor.getName(typeElement);
        this.fqn = type.getQualifiedName().toString();
        this.allFqn = extractAllFqn();
        this.constructor = ConstructorResolver.resolve(typeElement);
    }

    @Override
    public CodeBlock initExpression() {
        return CodeBlock.of("var $L = new $T($L)", name, typeElement, args());
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
        return List.copyOf(dependencies.values());
    }

    private String args() {
        if (args != null) {
            return args;
        }
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
        return this.args;
    }

    @Override
    public List<? extends VariableElement> parameters() {
        return constructor.getParameters();
    }

    @Override
    public void addDependency(String paramName, BeanDefinition dependency) {
        this.dependencies.put(paramName, dependency);
    }

    public List<String> extractAllFqn() {
        return Stream.concat(
                Stream.of(typeElement),
                Stream.concat(
                    getInterfaces().stream(),
                    getSuperclasses().stream()
                )
            )
            .map(type -> type.getQualifiedName().toString())
            .toList();
    }

    public Collection<TypeElement> getInterfaces() {
        return typeElement.getInterfaces()
            .stream()
            .map(this::getElement)
            .toList();
    }

    public Collection<TypeElement> getSuperclasses() {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DefaultBeanDefinition that = (DefaultBeanDefinition) o;
        return Objects.equals(typeElement, that.typeElement) && Objects.equals(name, that.name) && Objects.equals(fqn, that.fqn) && Objects.equals(allFqn, that.allFqn) && Objects.equals(dependencies, that.dependencies) && Objects.equals(args, that.args) && Objects.equals(constructor, that.constructor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(typeElement, name, fqn, allFqn, dependencies, args, constructor);
    }
}