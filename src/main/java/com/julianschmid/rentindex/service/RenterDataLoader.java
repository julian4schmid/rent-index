package com.julianschmid.rentindex.service;

import com.julianschmid.rentindex.model.*;
import com.julianschmid.rentindex.util.*;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class RenterDataLoader {

    private RenterDataLoader() {
    }

    public static List<Renter> loadRenters(String filename) throws IOException {
        String path = ResourceUtil.getDataPath();
        List<Renter> renters = new ArrayList<>();

        try (InputStream is = RenterDataLoader.class.getClassLoader().getResourceAsStream(path + filename);
             Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheetAt(0); // Use first sheet

            // Read header row and map column names to indexes
            Row headerRow = sheet.getRow(0);
            Map<String, Integer> colMap = ExcelUtil.createColMap(headerRow);

            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row == null || row.getCell(0) == null || row.getCell(0).getCellType() == CellType.BLANK) continue;

                String fullNames = ExcelUtil.getString(row, ExcelUtil.getColumnIndex(colMap, "Mieter"));
                String woman = ExcelUtil.getString(row, ExcelUtil.getColumnIndex(colMap, "Frau"));
                String man = ExcelUtil.getString(row, ExcelUtil.getColumnIndex(colMap, "Herr"));

                List<Tenant> tenants = new ArrayList<>();
                if (!woman.isBlank()) {
                    tenants.add(new Tenant(fullNames, woman));
                }
                if (!man.isBlank()) {
                    tenants.add(new Tenant(fullNames, man));
                }

                Apartment apartment = new Apartment(
                        ExcelUtil.getDouble(row, ExcelUtil.getColumnIndex(colMap, "Wfl.m2")),
                        ExcelUtil.getString(row, ExcelUtil.getColumnIndex(colMap, "Balkon")),
                        ExcelUtil.getString(row, ExcelUtil.getColumnIndex(colMap, "Lage")),
                        ExcelUtil.getString(row, ExcelUtil.getColumnIndex(colMap, "Straße")),
                        ExcelUtil.getString(row, ExcelUtil.getColumnIndex(colMap, "Zustand")),
                        ExcelUtil.getString(row, ExcelUtil.getColumnIndex(colMap, "Beheizung"))
                );

                String suspended = ExcelUtil.getString(row, ExcelUtil.getColumnIndex(colMap, "Ausgesetzt"));

                PreviousRentAdjustment previous = new PreviousRentAdjustment(
                        (int) ExcelUtil.getDouble(row, ExcelUtil.getColumnIndex(colMap, "Jahr alt")),
                        ExcelUtil.getString(row, ExcelUtil.getColumnIndex(colMap, "Monat alt")),
                        ExcelUtil.getDouble(row, ExcelUtil.getColumnIndex(colMap, "Miete alt")),
                        ExcelUtil.getDouble(row, ExcelUtil.getColumnIndex(colMap, "€/m2 alt"))
                );

                double operatingCosts = ExcelUtil.getDouble(row, ExcelUtil.getColumnIndex(colMap, "BK Voraus"));
                double heatingCosts = ExcelUtil.getDouble(row, ExcelUtil.getColumnIndex(colMap, "HK Voraus"));

                Renter renter = new Renter(tenants, apartment, suspended, previous, operatingCosts, heatingCosts);
                renters.add(renter);
            }

        }

        return renters;
    }


}
