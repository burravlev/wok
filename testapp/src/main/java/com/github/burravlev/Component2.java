package com.github.burravlev;

import com.github.burravlev.annotation.Component;
import com.github.burravlev.annotation.Value;

@Component
public class Component2 {
    private final String value;

    public Component2(@Value("${value}") String value) {
        this.value = value;
    }
}
