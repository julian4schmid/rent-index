package com.julianschmid.rentindex;

import com.julianschmid.rentindex.api.VpiDownloader;
import com.julianschmid.rentindex.loader.BuildingDataLoader;
import com.julianschmid.rentindex.loader.RenterDataLoader;
import com.julianschmid.rentindex.loader.VpiDataLoader;
import com.julianschmid.rentindex.model.*;
import com.julianschmid.rentindex.service.*;
import com.julianschmid.rentindex.util.*;
import com.julianschmid.rentindex.writer.LetterWriter;
import com.julianschmid.rentindex.writer.OverviewWriter;

import java.util.Map;
import java.util.Properties;

import java.util.List;

public class Main {
    public static void main(String[] args) {

        // VPI data up to date?
        boolean upToDate = false;

        // check if real data is available
        boolean realData = ResourceUtil.realDataAvailable();
        // use sample data if no real data is available

        // Download VPI data if API token is available and data
        if (realData && !upToDate) {
            try {
                System.out.println("download VPI data");
                Properties prop = PropertiesUtil.getProperties("token.properties");
                String token = prop.getProperty("token");

                VpiDownloader downloader = new VpiDownloader(token);
                downloader.downloadAndExtractCsv(
                        "61111-0002",
                        1991,
                        "src/main/resources/download");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("VPI data was not downloaded");
        }

        try {
            String overview = "Indexmieten_Übersicht.xlsx";
            List<VpiRecord> records = VpiDataLoader.load("download/vpi.csv");
            Map<String, Building> buildings = BuildingDataLoader.load("buildings.properties");
            List<Renter> renters = RenterDataLoader.load(overview, buildings);
            VpiService.addVpiToRenters(renters, records);
            RentAdjustService.adjustRent(renters);
            OverviewWriter.createAdjustmentOverview(overview, renters);
            LetterWriter.createLetters(renters);

            renters.forEach(System.out::println);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}