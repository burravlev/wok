package com.github.burravlev.datasource;

import javax.lang.model.type.TypeMirror;
import java.util.Map;

abstract class QueryTypeUtil {
    private static final Map<String, String> SQL_TYPES = Map.of(
        "java.lang.String", ".setString($L, $L)",
        "int", ".setInt($L, $L)",
        "java.lang.Integer", ".setInt($L, $L)",
        "long", ".setLong($L, $L)",
        "java.lang.Long", ".setLong($L, $L)"
    );

    static String getExpression(TypeMirror type) {
        return SQL_TYPES.get(type.toString());
    }
}
