package com.github.burravlev.datasource;

import com.github.burravlev.annotation.Repository;
import com.github.burravlev.context.BeanDefinition;
import com.github.burravlev.context.GeneratedBeanDefinition;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import java.util.List;
import java.util.stream.Collectors;

public class DatasourceProcessor {
    private final ProcessingEnvironment processingEnv;
    private final RoundEnvironment roundEnv;

    public DatasourceProcessor(ProcessingEnvironment processingEnv, RoundEnvironment roundEnv) {
        this.processingEnv = processingEnv;
        this.roundEnv = roundEnv;
    }

    public List<BeanDefinition> process() {
        List<DatasourceDefinition> definitions = DatasourceLoader.extract(roundEnv.getElementsAnnotatedWith(Repository.class));
        DatasourceWriter.write(processingEnv.getFiler(), definitions);
        return definitions.stream().map(def -> new GeneratedBeanDefinition(
                def.name(),
                def.fqn(),
                def.allFqn()
            ))
            .collect(Collectors.toList());
    }
}
