package com.julianschmid.rentindex.loader;

import com.julianschmid.rentindex.model.Building;
import com.julianschmid.rentindex.model.Landlord;
import com.julianschmid.rentindex.util.PropertiesUtil;

import java.util.*;

public final class BuildingDataLoader {

    private BuildingDataLoader() {
    }

    public static Map<String, Building> load(String filename) {
        System.out.println("load building data");

        Properties props = PropertiesUtil.getProperties(filename);

        // store landlord information
        Map<Integer, Landlord> landlordMap = new HashMap<>();
        int id = 1;

        while (props.containsKey("landlord." + id)) {
            String baseKey = "landlord." + id;

            String name = props.getProperty(baseKey);
            String street = props.getProperty(baseKey + ".street");
            String zipcode = props.getProperty(baseKey + ".zipcode");
            String email = props.getProperty(baseKey + ".email");

            if (name == null || street == null || zipcode == null || email == null) {
                throw new IllegalArgumentException("Incomplete data for " + baseKey);
            }

            Landlord landlord = new Landlord(name, street, zipcode, email);
            landlordMap.put(id, landlord);
            id++;
        }

        String[] buildingIds = props.getProperty("list.of.building.ids").split(",");
        Map<String, Building> buildings = new HashMap<>();
        for (String buildingId : buildingIds) {
            String street = props.getProperty(buildingId + ".street");
            String zipcode = props.getProperty(buildingId + ".zipcode");
            String landlords = props.getProperty(buildingId + ".landlords");

            List<Landlord> landlordList = new ArrayList<>();
            for (String landlordId : landlords.split(",")) {
                landlordList.add(landlordMap.get(Integer.parseInt(landlordId)));
            }

            buildings.put(buildingId, new Building(buildingId, street, zipcode, landlordList));
        }

        return buildings;
    }
}
