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
    private static final String PATTERN = ":\\w+\\.?\\w+";
    private static final Pattern COMPILED = Pattern.compile(PATTERN);
    private static final Map<String, String> EXPRESSIONS_BY_TYPES = Map.ofEntries(
        Map.entry("int", "setInt(%d, %s)"),
        Map.entry("java.lang.Integer", "setInt(%d, %s)"),
        Map.entry("java.lang.String", "setString(%d, %s)")
    );

    public ParsedStatement parse(String query, TypeMirror returnType, List<? extends VariableElement> parameters) {
        ParsedStatement statement = new ParsedStatement();
        statement.setQuery(parseQuery(query));
        statement.setParams(parseParams(query, parameters));
        if (!returnType.toString().equals("void")) {
            statement.setReturnType(parseReturnType(statement.getQuery(), returnType));
        }
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

    private Map<String, String> parseReturnType(String query, TypeMirror returnType) {
        String[] split = query.split("[\\s,]+");
        Map<String, String> parsed = RelationParser.fromResultSet(returnType);
        Map<String, String> result = new HashMap<>();
        for (String val : split) {
            String exp = parsed.get(val);
            if (exp != null) {
                result.put(val, String.format(exp, val));
            }
        }
        return result;
    }
}
