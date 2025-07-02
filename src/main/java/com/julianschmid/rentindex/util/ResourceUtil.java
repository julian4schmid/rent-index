package com.julianschmid.rentindex.util;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class ResourceUtil {
    public static boolean resourceFolderExists(String folderName) {
        URL url = ResourceUtil.class.getClassLoader().getResource(folderName);
        if (url == null) return false;
        try {
            Path path = Paths.get(url.toURI());
            return Files.exists(path);
        } catch (Exception e) {
            return false;
        }
    }
}
