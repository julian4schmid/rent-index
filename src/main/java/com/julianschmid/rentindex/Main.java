package com.julianschmid.rentindex;

import com.julianschmid.rentindex.api.VpiDownloader;
import com.julianschmid.rentindex.model.*;
import com.julianschmid.rentindex.service.RenterDataLoader;
import com.julianschmid.rentindex.service.VpiDataLoader;
import com.julianschmid.rentindex.util.*;

import java.util.Properties;

import java.util.List;

public class Main {
    public static void main(String[] args) {

        // VPI data up to date?
        boolean upToDate = false;

        // check if real data is available
        boolean realData = ResourceUtil.folderExists("real");
        // use sample data if no real data is available
        String path = realData ? "real/" : "sample/";

        // Download VPI data if API token is available and data
        if (realData && !upToDate) {
            try {
                Properties prop = PropertiesLoader.getProperties(path, "token.properties");
                String token = prop.getProperty("token");

                VpiDownloader downloader = new VpiDownloader(token);
                String csvFile = downloader.downloadAndExtractCsv(
                        "61111-0002",
                        1991,
                        "src/main/resources/download");
                System.out.println("CSV extracted to: " + csvFile);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        try {
            List<VpiRecord> records = VpiDataLoader.loadSortedVpiRecords("download/vpi.csv");
            List<Renter> renters = RenterDataLoader.loadRenters(path + "Indexmieten_Übersicht.xlsx");

            records.forEach(System.out::println);
            renters.forEach(System.out::println);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}