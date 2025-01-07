package com.github.burravlev;

import java.sql.ResultSet;

public interface RowMapper<T> {
    T fromResultSet(ResultSet resultSet);
}
