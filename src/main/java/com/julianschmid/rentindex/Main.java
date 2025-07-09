package com.julianschmid.rentindex;

import com.julianschmid.rentindex.api.VpiDownloader;
import com.julianschmid.rentindex.model.*;
import com.julianschmid.rentindex.service.*;
import com.julianschmid.rentindex.util.*;

import java.util.Map;
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
                Properties prop = PropertiesUtil.getProperties("token.properties");
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
            String overview = "Indexmieten_Übersicht.xlsx";
            List<VpiRecord> records = VpiDataLoader.load("download/vpi.csv");
            Map<String, Building> buildings = BuildingDataLoader.load("buildings.properties");
            List<Renter> renters = RenterDataLoader.load(overview, buildings);
            VpiService.addVpiToRenters(renters, records);
            RentAdjustService.adjustRent(renters);
            RenterExcelWriter.createNewOverview(overview, renters);

            records.forEach(System.out::println);
            renters.forEach(System.out::println);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}