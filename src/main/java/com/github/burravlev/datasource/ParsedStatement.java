package com.github.burravlev.datasource;

import java.util.ArrayList;
import java.util.List;

public class ParsedStatement {
    private String query;
    List<QueryParam> params = new ArrayList<>();

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public List<QueryParam> getParams() {
        return params;
    }

    public void setParams(List<QueryParam> queryParam) {
        this.params = queryParam;
    }
}
