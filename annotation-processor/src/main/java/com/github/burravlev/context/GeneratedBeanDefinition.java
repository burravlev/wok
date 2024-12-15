package com.github.burravlev.context;

import com.github.burravlev.util.Assert;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;

import javax.lang.model.element.VariableElement;
import java.util.*;

public class GeneratedBeanDefinition implements BeanDefinition {
    private final String name;
    private final String fqn;
    private final List<String> allFqn;
    private final Map<String, BeanDefinition> dependencies = new HashMap<>();
    private final List<? extends VariableElement> parameters;
    private String args;

    public GeneratedBeanDefinition(String name, String fqn, List<String> allFqn) {
        this(name, fqn, allFqn, Collections.emptyList());
    }

    public GeneratedBeanDefinition(String name,
                                   String fqn,
                                   List<String> allFqn,
                                   List<? extends VariableElement> parameters) {
        this.name = name;
        this.fqn = fqn;
        Assert.nonNull(parameters, "Parameters collection cannot be null");
        this.parameters = parameters;
        this.allFqn = List.copyOf(allFqn);
    }

    @Override
    public CodeBlock initExpression() {
        ClassName className = ClassNameParser.parse(fqn + "$Generated");
        return CodeBlock.of("var $L = new $T($L)", name, className, args());
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

    @Override
    public List<? extends VariableElement> parameters() {
        return List.copyOf(parameters);
    }

    @Override
    public void addDependency(String param, BeanDefinition dependency) {
        this.dependencies.put(param, dependency);
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
}
