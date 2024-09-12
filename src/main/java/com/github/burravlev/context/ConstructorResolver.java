package com.github.burravlev.context;

import com.github.burravlev.util.Assert;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import java.util.ArrayList;
import java.util.List;

abstract class ConstructorResolver {
    static ExecutableElement resolve(Element element) {
        Assert.nonNull(element, "Cannot extract constructor");
        List<ExecutableElement> possibleConstructors = new ArrayList<>();
        for (Element enclosedElement : element.getEnclosedElements()) {
            if (enclosedElement.getKind() == ElementKind.CONSTRUCTOR) {
                possibleConstructors.add((ExecutableElement) enclosedElement);
            }
        }
        if (possibleConstructors.size() != 1) {
            throw new IllegalStateException("There are " + possibleConstructors.size() + " constructors in "
                + element.getSimpleName() + ". Only provide one constructor");
        }
        return possibleConstructors.get(0);
    }
}