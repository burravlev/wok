package com.github.burravlev;

import com.github.burravlev.annotation.Component;

@Component
public class Component1 {
    private final Component2 component2;

    public Component1(Component2 component2) {
        this.component2 = component2;
    }
}
