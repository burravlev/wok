package com.github.burravlev.util;

import javax.annotation.processing.Filer;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.InputStream;
import java.util.Properties;

public abstract class PropertyReader {
    private static final Properties properties = new Properties();

    public static void loadProperties(String file) {
        try (InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream(file)) {
            properties.load(is);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public static void loadPropertySource(Filer filer) {
        try {
            FileObject fileObject = filer.getResource(StandardLocation.CLASS_OUTPUT, "",
                "application.properties");
            try (InputStream is = fileObject.openInputStream()) {
                properties.load(is);
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public static String getProperty(String name) {
        String value = properties.getProperty(name);
        Assert.nonNull(value, "Property: " + name + " is not defined!");
        return value;
    }
}
