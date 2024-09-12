package com.github.burravlev.datasource;

import com.github.burravlev.annotation.Query;
import com.github.burravlev.context.BeanNameExtractor;
import com.squareup.javapoet.ClassName;

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class DatasourceDefinition {
    private final TypeElement typeElement;
    private final List<ExecutableElement> markedMethods;
    private final String fqn;
    private final List<String> allFqn;

    public DatasourceDefinition(TypeElement typeElement) {
        this.typeElement = typeElement;
        this.markedMethods = typeElement.getEnclosedElements()
            .stream()
            .filter(el -> el.getKind() == ElementKind.METHOD)
            .map(ExecutableElement.class::cast)
            .peek(el -> {
                if (el.getAnnotation(Query.class) == null) {
                    throw new IllegalStateException("All interface methods should be marked with @Query");
                }
            })
            .collect(Collectors.toList());
        this.fqn = typeElement.getQualifiedName().toString();
        this.allFqn = extractAllFqn();
    }

    public void addMarkedMethod(ExecutableElement method) {
        this.markedMethods.add(method);
    }

    public TypeElement getTypeElement() {
        return typeElement;
    }

    public List<ExecutableElement> getMarkedMethods() {
        return List.copyOf(markedMethods);
    }

    public String name() {
        return BeanNameExtractor.getName(typeElement);
    }

    public String fqn() {
        return fqn;
    }

    public List<String> allFqn() {
        return List.copyOf(allFqn);
    }

    private List<String> extractAllFqn() {
        List<String> allFqn = getInterfaces()
            .stream()
            .map(i -> i.getQualifiedName().toString())
            .collect(Collectors.toList());
        allFqn.add(fqn);
        return allFqn;
    }

    public Collection<TypeElement> getInterfaces() {
        return typeElement.getInterfaces()
            .stream()
            .map(this::getElement)
            .toList();
    }

    private TypeElement getElement(TypeMirror mirror) {
        return (TypeElement) ((DeclaredType) mirror).asElement();
    }
}
