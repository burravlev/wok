package com.github.burravlev.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class StringUtilsTest {
    private static final String[][] DATA = new String[][] {
        {"test", "test"},
        {"testTest", "test_test"},
        {"testTesttestTest", "test_testtest_test"}
    };

    @Test
    public void shouldConvertCamelToSnake() {
        for (String[] data : DATA) {
            assertEquals(StringUtils.camelToSnake(data[0]), data[1]);
        }
    }

    @Test
    public void shouldConvertSnakeToCamel() {
        for (String[] data : DATA) {
            assertEquals(StringUtils.snakeToCamel(data[1]), data[0]);
        }
    }
}
