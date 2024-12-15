package com.github.burravlev.context;

import com.squareup.javapoet.ClassName;

public abstract class ClassNameParser {
    public static ClassName parse(String fqn) {
        int dotIndex = fqn.lastIndexOf('.');
        String packageName = fqn.substring(0, dotIndex);
        String className = fqn.substring(dotIndex + 1);
        return ClassName.get(packageName, className);
    }
}
