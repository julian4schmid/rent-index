package com.julianschmid.rentindex.util;

import java.io.InputStream;
import java.util.Properties;

public class PropertiesLoader {
    public static Properties getProperties(String path, String fileName) {
        try (InputStream input = PropertiesLoader.class.getClassLoader()
                .getResourceAsStream(path + fileName)) {
            if (input == null) throw new IllegalStateException("file not found");

            Properties prop = new Properties();
            prop.load(input);
            return prop;
        } catch (Exception e) {
            throw new RuntimeException("Failed to load API token", e);
        }
    }
}
