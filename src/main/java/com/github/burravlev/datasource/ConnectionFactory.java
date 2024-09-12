package com.github.burravlev.datasource;

import com.github.burravlev.util.PropertyReader;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionFactory {
    private static final DataSource datasource;

    static {
        PropertyReader.loadProperties("application.properties");
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(PropertyReader.getProperty("jdbc.url"));
        config.setUsername(PropertyReader.getProperty("jdbc.username"));
        config.setPassword(PropertyReader.getProperty("jdbc.password"));
        datasource = new HikariDataSource(config);
    }

    public static Connection getConnection() throws SQLException {
        return datasource.getConnection();
    }
}
