package com.github.burravlev.datasource;

import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DatasourceReader {
    private static final String PATTERN = ":\\S+";
    private static final Pattern COMPILED = Pattern.compile(PATTERN);
    private static final Map<String, String> EXPRESSIONS_BY_TYPES = Map.of(
        "int", ".setInt(%d, %s)",
        "java.lang.String", "setString(%d, %s)",
        "java.lang.Integer", "setInt(%d, %s)",
        "java.lang.Long", "setLong(%d, %s)",
        "java.lang.Double", "setDouble(%d, %s)",
        "java.lang.Boolean", "setBoolean(%d, %s)"
    );

    public ParsedStatement parse(String query, List<? extends VariableElement> parameters) {
        ParsedStatement statement = new ParsedStatement();
        statement.setQuery(parseQuery(query));
        statement.setParams(parseParams(query, parameters));
        return statement;
    }

    private String parseQuery(String query) {
        return query.replaceAll(PATTERN, "?");
    }

    private List<QueryParam> parseParams(String query, List<? extends VariableElement> parameters) {
        List<QueryParam> result = new ArrayList<>();
        Map<String, VariableElement> paramsMap = new HashMap<>();
        for (VariableElement parameter : parameters) {
            paramsMap.put(parameter.toString(), parameter);
        }
        Matcher matcher = COMPILED.matcher(query);
        int index = 1;
        while (matcher.find()) {
            String paramName = matcher.group().substring(1);
            VariableElement element = paramsMap.get(paramName);
            TypeMirror typeMirror = element.asType();
            String type = typeMirror.toString();
            QueryParam queryParam = new QueryParam(EXPRESSIONS_BY_TYPES.get(type)
                .formatted(index++, paramName));
            result.add(queryParam);
        }
        return result;
    }
}