package com.github.burravlev.datasource;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

abstract class RelationLoader {
    private static final Map<String, TypeElement> relations = new HashMap<>();

    static void load(Set<? extends Element> elements) {
        elements.stream()
            .filter(el -> el.asType().getKind() == TypeKind.DECLARED)
            .map(TypeElement.class::cast)
            .forEach(type -> {
                relations.put(type.toString(), type);
            });
    }

    static TypeElement get(String name) {
        TypeElement element = relations.get(name);
        if (element == null) {
            throw new IllegalStateException("Type '" + name + "' is not marked with @Relation");
        }
        return element;
    }
}
