package com.github.burravlev.context;

import com.github.burravlev.annotation.Bean;
import com.github.burravlev.util.Assert;
import com.github.burravlev.util.StringUtils;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;

public abstract class BeanNameExtractor {
    public static String getName(Element element) {
        return StringUtils.toCamelCase(element.getSimpleName().toString());
    }

    public static String getName(ExecutableElement factoryMethod) {
        if (factoryMethod.getKind() != ElementKind.METHOD) {
            throw new IllegalStateException("Try to extract bean name from non method element: " +
                factoryMethod.getKind().toString());
        }
        Bean beanAnnotation = factoryMethod.getAnnotation(Bean.class);
        Assert.nonNull(beanAnnotation, "Factory method didn't marked with @Bean");
        if (StringUtils.isNotBlank(beanAnnotation.name())) {
            return beanAnnotation.name();
        }
        return StringUtils.toCamelCase(factoryMethod.getSimpleName().toString());
    }
}