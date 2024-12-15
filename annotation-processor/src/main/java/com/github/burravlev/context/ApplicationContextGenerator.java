package com.github.burravlev.context;

import com.squareup.javapoet.*;

import javax.lang.model.element.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApplicationContextGenerator {
    private static final String MAP_FIELD_NAME = "beans";
    private static final String MAP_CLASS_NAME = "beansByClass";
    private static final String INJECTOR_PACKAGE_NAME = "com.github.burravlev";
    public static final String CLASS_NAME = "$ApplicationContext";
    private final List<BeanDefinition> sortedBeans;

    public ApplicationContextGenerator(List<BeanDefinition> sortedBeans) {
        this.sortedBeans = sortedBeans;
    }

    public JavaFile generateClass() {
        TypeSpec typeSpec = TypeSpec.classBuilder(CLASS_NAME)
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
            .addSuperinterface(ApplicationGraph.class)
            .addField(getBeansMapField())
            .addField(getBeansByClassField())
            .addMethod(getConstructor())
            .addMethod(getBeanByNameMethod())
            .addMethod(getBeansByClassMethod())
            .addMethod(addRegisterMethod())
            .build();
        return JavaFile.builder(INJECTOR_PACKAGE_NAME, typeSpec)
            .build();
    }

    private FieldSpec getBeansMapField() {
        return FieldSpec.builder(ParameterizedTypeName.get(Map.class, String.class, Object.class), MAP_FIELD_NAME)
            .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
            .initializer("new $T<>()", HashMap.class)
            .build();
    }

    private FieldSpec getBeansByClassField() {
        ParameterizedTypeName classWildcard = ParameterizedTypeName.get(ClassName.get(Class.class), WildcardTypeName.subtypeOf(Object.class));
        ParameterizedTypeName listOfObject = ParameterizedTypeName.get(ClassName.get(List.class), WildcardTypeName.get(Object.class));
        ParameterizedTypeName mapOfClassToList = ParameterizedTypeName.get(ClassName.get(Map.class), classWildcard, listOfObject);
        return FieldSpec.builder(mapOfClassToList, MAP_CLASS_NAME)
            .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
            .initializer("new $T<>()", HashMap.class)
            .build();
    }

    private MethodSpec getConstructor() {
        MethodSpec.Builder builder = MethodSpec.constructorBuilder()
            .addModifiers(Modifier.PUBLIC);
        for (BeanDefinition definition : sortedBeans) {
            addBeanInstantiation(builder, definition);
            addBeanRegistration(builder, definition);
            addBeanByClass(builder, definition);
        }
        return builder.build();
    }

    private MethodSpec getBeanByNameMethod() {
        return MethodSpec.methodBuilder("getBean")
            .addModifiers(Modifier.PUBLIC)
            .returns(Object.class)
            .addParameter(String.class, "name")
            .addStatement("return $L.get($L)", MAP_FIELD_NAME, "name")
            .build();
    }

    private MethodSpec getBeansByClassMethod() {
        return MethodSpec.methodBuilder("getBeans")
            .addModifiers(Modifier.PUBLIC)
            .returns(ParameterizedTypeName.get(List.class, Object.class))
            .addParameter(Class.class, "targetClass")
            .addStatement("return beansByClass.get(targetClass)")
            .build();
    }

    private void addBeanInstantiation(MethodSpec.Builder builder, BeanDefinition definition) {
        builder.addStatement(definition.initExpression());
    }

    private MethodSpec addRegisterMethod() {
        return MethodSpec.methodBuilder("addBeanByClass")
            .addModifiers(Modifier.PRIVATE)
            .addParameter(ParameterizedTypeName.get(
                    ClassName.get(Class.class),
                    WildcardTypeName.subtypeOf(Object.class)),
                "target")
            .addParameter(Object.class, "bean")
            .addCode("""
                var beans = $L.getOrDefault(target, new $T());
                beans.add(bean);
                $L.put(target, beans);
                """, MAP_CLASS_NAME, ParameterizedTypeName.get(
                ClassName.get(ArrayList.class),
                WildcardTypeName.get(Object.class)), MAP_CLASS_NAME)
            .build();
    }

    private void addBeanByClass(MethodSpec.Builder builder, BeanDefinition definition) {
        for (String fqn : definition.allFqn()) {
            builder.addStatement("addBeanByClass($T.class, $L)",
                ClassNameParser.parse(fqn),
                definition.name());
        }
    }

    private void addBeanRegistration(MethodSpec.Builder builder, BeanDefinition definition) {
        String id = definition.name();
        builder.addStatement("$L.put($S, $L)", MAP_FIELD_NAME, id, id);
    }
}