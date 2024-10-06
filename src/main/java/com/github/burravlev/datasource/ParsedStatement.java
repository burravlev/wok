package com.github.burravlev.datasource;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class ParsedStatement {
    private Map<String, String> returnType;
    private String query;
    private List<QueryParam> params = new ArrayList<>();
}
