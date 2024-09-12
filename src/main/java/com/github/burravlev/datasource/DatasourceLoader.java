package com.github.burravlev.datasource;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import java.util.List;
import java.util.Set;

public class DatasourceLoader {
    static List<DatasourceDefinition> extract(Set<? extends Element> elements) {
        return elements.stream()
            .filter(el -> el.asType().getKind() == TypeKind.DECLARED)
            .map(TypeElement.class::cast)
            .peek(type -> {
                if (!type.getKind().isInterface()) {
                    throw new IllegalStateException("repository cannot be non interface type");
                }
                DatasourceDefinitionHolder.add(type);
            })
            .map(DatasourceDefinition::new)
            .toList();
    }
}
