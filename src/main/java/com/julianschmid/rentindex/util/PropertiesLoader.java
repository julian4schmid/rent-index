package com.julianschmid.rentindex.util;

import java.io.InputStream;
import java.util.Properties;

public final class PropertiesLoader {
    public static Properties getProperties(String filename) {
        String path = ResourceUtil.getDataPath();
        try (InputStream input = PropertiesLoader.class.getClassLoader()
                .getResourceAsStream(path + filename)) {
            if (input == null) throw new IllegalStateException("file not found");

            Properties prop = new Properties();
            prop.load(input);
            return prop;
        } catch (Exception e) {
            throw new RuntimeException("Failed to load API token", e);
        }
    }
}
