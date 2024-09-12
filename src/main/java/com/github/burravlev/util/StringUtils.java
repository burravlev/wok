package com.github.burravlev.util;

public abstract class StringUtils {
    public static boolean isNotBlank(String s) {
        return s != null && !s.isBlank();
    }

    public static boolean isBlank(String s) {
        return !isNotBlank(s);
    }

    public static String toCamelCase(String s) {
        if (isBlank(s)) {
            return "";
        }
        if (s.length() == 1) {
            return s.toLowerCase();
        }
        return Character.toLowerCase(s.charAt(0)) + s.substring(1);
    }
}
