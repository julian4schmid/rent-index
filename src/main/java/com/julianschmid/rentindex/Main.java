package com.julianschmid.rentindex;

import com.julianschmid.rentindex.api.VpiDownloader;
import com.julianschmid.rentindex.model.*;
import com.julianschmid.rentindex.service.*;
import com.julianschmid.rentindex.util.*;

import java.util.Properties;

import java.util.List;

public class Main {
    public static void main(String[] args) {

        // VPI data up to date?
        boolean upToDate = true;

        // check if real data is available
        boolean realData = ResourceUtil.realDataAvailable();
        // use sample data if no real data is available

        // Download VPI data if API token is available and data
        if (realData && !upToDate) {
            try {
                Properties prop = PropertiesLoader.getProperties("token.properties");
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

        String overview = "Indexmieten_Übersicht.xlsx";
        try {
            List<VpiRecord> records = VpiDataLoader.loadSortedVpiRecords("download/vpi.csv");
            List<Renter> renters = RenterDataLoader.loadRenters(overview);
            VpiService.addVpiToRenters(renters, records);
            RenterExcelWriter.createNewOverview(overview, renters);

            records.forEach(System.out::println);
            renters.forEach(System.out::println);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}