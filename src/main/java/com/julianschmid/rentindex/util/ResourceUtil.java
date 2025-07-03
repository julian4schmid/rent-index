package com.julianschmid.rentindex.util;

import java.net.URL;

public final class ResourceUtil {
    public static boolean realDataAvailable() {
        URL url = ResourceUtil.class.getClassLoader().getResource("real/");
        if (url == null) {
            return false;
        }
        return true;
    }

    public static String getDataPath() {
        return realDataAvailable() ? "real/" : "sample/";
    }
}

