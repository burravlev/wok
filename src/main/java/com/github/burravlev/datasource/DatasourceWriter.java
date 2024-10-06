package com.github.burravlev.datasource;

import com.github.burravlev.annotation.Query;
import com.github.burravlev.context.ClassNameParser;
import com.github.burravlev.util.Assert;
import com.github.burravlev.util.StringUtils;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import javax.annotation.processing.Filer;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

abstract class DatasourceWriter {
    static void write(Filer filer, Collection<DatasourceDefinition> elements) {
        elements.forEach(def -> write(filer, def));
    }

    private static void write(Filer filer, DatasourceDefinition definition) {
        TypeElement superInterface = definition.getTypeElement();
        try {
            TypeSpec typeSpec = TypeSpec.classBuilder(
                    superInterface.getSimpleName().toString() + "$Generated"
                )
                .addSuperinterface(TypeName.get(superInterface.asType()))
                .addModifiers(Modifier.PUBLIC)
                .addMethods(writeMethods(definition.getMarkedMethods()))
                .build();
            JavaFile javaFile = JavaFile.builder(extractPackageName(superInterface), typeSpec)
                .build();
            javaFile.writeTo(filer);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private static Collection<MethodSpec> writeMethods(Collection<ExecutableElement> methods) {
        return methods.stream()
            .map(DatasourceWriter::writeMethod)
            .collect(Collectors.toList());
    }

    private static MethodSpec writeMethod(ExecutableElement method) {
        Query query = method.getAnnotation(Query.class);
        Assert.nonNull(query, "All @Repository repository methods should be marked with @Query.");
        DatasourceReader reader = new DatasourceReader();
        ParsedStatement statement = reader.parse(query.value(), method.getReturnType(), method.getParameters());
        MethodSpec.Builder builder = MethodSpec.overriding(method)
            .addCode("try ($T connection = $T.getConnection()) {\n",
                Connection.class, ConnectionFactory.class);
        List<QueryParam> found = statement.getParams();
        builder.addCode("    $T ps = connection.prepareStatement($S);\n",
            PreparedStatement.class, statement.getQuery());
        for (QueryParam expression : found) {
            builder.addCode("    ps.$L;\n", expression.expression());
        }
        Map<String, String> returnFields = statement.getReturnType();
        if (returnFields != null) {
            builder.addCode("    $T resultSet = ps.executeQuery();\n", ResultSet.class);
            builder.addCode("    var result = new $T();\n", ClassNameParser.parse(method.getReturnType().toString()));
            for (Map.Entry<String, String> exp : returnFields.entrySet()) {
                String camel = StringUtils.snakeToCamel(exp.getKey());
                String setter = "set" + Character.toUpperCase(camel.charAt(0)) + camel.substring(1);
                builder.addCode("    result.$L(resultSet$L);\n", setter, exp.getValue());
            }
        } else {
            builder.addCode("    ps.execute();\n");
        }
        if (returnFields != null) {
            builder.addCode("    return result;\n");
        }
        builder.addCode("""
            } catch ($T e) {
                throw new $T(e);
            }
            """, SQLException.class, IllegalStateException.class);
        return builder.build();
    }

    private static String extractPackageName(TypeElement typeElement) {
        String name = typeElement.toString();
        int index = name.lastIndexOf('.');
        return name.substring(0, index);
    }
}