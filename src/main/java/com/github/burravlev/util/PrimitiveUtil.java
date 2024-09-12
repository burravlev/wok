package com.github.burravlev.util;

import javax.lang.model.type.TypeMirror;
import java.util.Set;

public abstract class PrimitiveUtil {
    private static final Set<String> PRIMITIVES = Set.of(
        "java.lang.String",
        "java.lang.Integer",
        "java.lang.Double",
        "java.lang.Float",
        "java.lang.Boolean",
        "java.lang.Long"
    );

    public static boolean isPrimitive(TypeMirror typeMirror) {
        if (typeMirror.getKind().isPrimitive()) {
            return true;
        }
        return PRIMITIVES.contains(typeMirror.toString());
    }
}