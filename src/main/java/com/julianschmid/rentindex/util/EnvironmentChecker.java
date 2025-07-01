package com.julianschmid.rentindex.util;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class EnvironmentChecker {
    public static boolean folderExists(String folderName) {
        URL url = EnvironmentChecker.class.getClassLoader().getResource(folderName);
        if (url == null) return false;
        try {
            Path path = Paths.get(url.toURI());
            return Files.exists(path);
        } catch (Exception e) {
            return false;
        }
    }
}
