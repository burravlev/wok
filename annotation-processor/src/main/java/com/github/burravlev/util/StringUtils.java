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

    public static String camelToSnake(String str) {
        String result = "";

        char c = str.charAt(0);
        result = result + Character.toLowerCase(c);

        for (int i = 1; i < str.length(); i++) {

            char ch = str.charAt(i);

            if (Character.isUpperCase(ch)) {
                result = result + '_';
                result
                    = result
                    + Character.toLowerCase(ch);
            }
            else {
                result = result + ch;
            }
        }
        return result;
    }

    public static String snakeToCamel(String str) {
        String[] words = str.split("[\\W_]+");
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            if (i == 0) {
                word = word.isEmpty() ? word : word.toLowerCase();
            } else {
                word = word.isEmpty() ? word : Character.toUpperCase(word.charAt(0)) + word.substring(1).toLowerCase();
            }
            builder.append(word);
        }
        return builder.toString();
    }
}
