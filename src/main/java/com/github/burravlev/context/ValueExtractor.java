package com.github.burravlev.context;

import com.github.burravlev.annotation.Value;
import com.github.burravlev.util.PrimitiveUtil;
import com.github.burravlev.util.PropertyReader;
import com.squareup.javapoet.CodeBlock;

import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

abstract class ValueExtractor {
    private static final String PROPERTY_PATTER = "\\$\\{(.*?)}";

    static boolean extractValue(StringBuilder builder, VariableElement parameter) {
        TypeMirror type = parameter.asType();
        if (!PrimitiveUtil.isPrimitive(type)) {
            return false;
        }
        Value value = parameter.getAnnotation(Value.class);
        if (value == null) {
            return false;
        }
        String val;
        if (value.value().matches(PROPERTY_PATTER)) {
            val = getProperty(value.value());
        } else {
            val = value.value();
        }
        if (type.toString().equals("java.lang.String")) {
            builder.append("\"");
            builder.append(val);
            builder.append("\"");
        } else {
            builder.append(val);
        }
        return true;
    }

    private static String getProperty(String key) {
        key = key.replace("${", "");
        key = key.replace("}", "");
        key = key.trim();
        return PropertyReader.getProperty(key);
    }
}