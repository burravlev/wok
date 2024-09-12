package com.github.burravlev.util;

public abstract class Assert {
    public static void nonNull(Object object, String message) {
        if (object == null) {
            throw new IllegalStateException(message);
        }
    }
}
