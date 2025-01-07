package com.github.burravlev;

import java.util.Collection;

public interface ApplicationGraph {
    Object getBean(String name);

    Collection<Object> getBeans(Class<?> type);
}
