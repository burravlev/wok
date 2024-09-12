package com.github.burravlev.datasource;

import javax.lang.model.element.TypeElement;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DatasourceDefinitionHolder {
    private static final Set<TypeElement> elements = new HashSet<>();

    static void add(TypeElement type) {
        elements.add(type);
    }

    static List<TypeElement> getAll() {
        return List.copyOf(elements);
    }
}
