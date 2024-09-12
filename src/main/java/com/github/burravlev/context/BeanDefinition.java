package com.github.burravlev.context;

import com.squareup.javapoet.CodeBlock;

import javax.lang.model.element.VariableElement;
import java.util.Collection;
import java.util.List;

public interface BeanDefinition {
    CodeBlock initExpression();

    String name();

    String fqn();

    List<String> allFqn();

    List<BeanDefinition> dependencies();

    Collection<? extends VariableElement> parameters();

    void addDependency(String param, BeanDefinition dependency);
}