package com.github.burravlev.datasource;

import com.github.burravlev.util.StringUtils;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import java.util.HashMap;
import java.util.Map;

abstract class RelationParser {
    private static final Map<String, String> RESULT_SET = Map.ofEntries(
        Map.entry("int", ".getInt(\"%s\")"),
        Map.entry("long", ".getLong(\"%s\")"),
        Map.entry("float", ".getFloat(\"%s\")"),
        Map.entry("double", ".getDouble(%s)"),
        Map.entry("boolean", ".getBoolean(%s)"),
        Map.entry("char", ".getChar(%s)"),
        Map.entry("java.lang.Integer", ".getInt(%s)"),
        Map.entry("java.lang.Long", ".getLong(\"%s\")"),
        Map.entry("java.lang.Float", ".getFloat(%s)"),
        Map.entry("java.lang.Double", ".getDouble(%s)"),
        Map.entry("java.lang.Boolean", ".getBoolean(%s)"),
        Map.entry("java.lang.String", ".getString(%s)")
    );

    static Map<String, String> fromResultSet(TypeMirror type) {
        String qualifiedName = type.toString();
        TypeElement element = RelationLoader.get(qualifiedName);
        Map<String, String> result = new HashMap<>();
        element.getEnclosedElements()
            .stream().filter(e -> e.getKind().isField())
            .forEach(field -> {
                String fieldQualifiedName = field.asType().toString();
                result.put(StringUtils.camelToSnake(field.toString()), RESULT_SET.getOrDefault(fieldQualifiedName, ".getObject(\"%s\")"));
            });
        return result;
    }
}
