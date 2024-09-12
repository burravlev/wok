package com.github.burravlev.context;

import com.github.burravlev.annotation.Value;
import com.github.burravlev.util.PrimitiveUtil;

import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class DependencyResolver {
    private final BeanDefinitionReader reader;

    public DependencyResolver(BeanDefinitionReader reader) {
        this.reader = reader;
    }

    public void resolve() {
        for (BeanDefinition definition : reader.getDefinitions()) {
            Collection<? extends VariableElement> parameters = definition.parameters();
            for (VariableElement parameter : parameters) {
                BeanDefinition dependency = findDependencyForParam(definition, parameter);
                if (dependency != null) {
                    definition.addDependency(parameter.getSimpleName().toString(), dependency);
                }
            }
        }
    }

    private BeanDefinition findDependencyForParam(BeanDefinition definition, VariableElement parameter) {
        TypeMirror paramType = parameter.asType();
        if (PrimitiveUtil.isPrimitive(paramType)) {
            Value value = parameter.getAnnotation(Value.class);
            if (value != null) {
                return null;
            }
        }
        String paramFqn = paramType.toString();
        List<BeanDefinition> candidates = reader.getDefinitionsByFqn(paramFqn);
        if (candidates.isEmpty()) {
            throw new IllegalStateException(
                "'%s' requires a bean of type '%s' which does not exist".formatted(definition.fqn(), paramFqn)
            );
        }
        return tryToResolve(candidates, parameter);
    }

    private BeanDefinition tryToResolve(List<BeanDefinition> candidates, VariableElement param) {
        candidates = new ArrayList<>(candidates);
        candidates.removeIf(candidate -> !candidate.name().equals(param.toString()));
        if (candidates.size() > 1) {
            throw new IllegalStateException(
                "Ambiguous dependency. Parameter '%s %s' has %d candidates: %s".formatted(
                    param.asType(), param, candidates.size(),
                    candidates.stream().map(BeanDefinition::fqn).collect(Collectors.joining(", "))
                )
            );
        }
        return candidates.get(0);
    }
}