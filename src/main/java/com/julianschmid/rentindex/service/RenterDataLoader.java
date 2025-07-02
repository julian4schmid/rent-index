package com.julianschmid.rentindex.service;

import com.julianschmid.rentindex.model.Apartment;
import com.julianschmid.rentindex.model.PreviousRentAdjustment;
import com.julianschmid.rentindex.model.Renter;
import com.julianschmid.rentindex.model.Tenant;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class RenterDataLoader {

    private RenterDataLoader() {}

    public static List<Renter> loadRenters(String fileName) throws IOException{
        List<Renter> renters = new ArrayList<>();

        try (InputStream is = RenterDataLoader.class.getClassLoader().getResourceAsStream(fileName);
             Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheetAt(0); // Use first sheet

            // Read header row and map column names to indexes
            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                throw new IllegalStateException("Sheet is missing header row");
            }

            Map<String, Integer> colMap = new HashMap<>();
            for (Cell cell : headerRow) {
                String colName = cell.getStringCellValue().trim();
                colMap.put(colName, cell.getColumnIndex());
            }

            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row == null || row.getCell(0) == null || row.getCell(0).getCellType() == CellType.BLANK) continue;

                String fullNames = getString(row, getRequiredColumnIndex(colMap, "Mieter"));
                String woman = getString(row, getRequiredColumnIndex(colMap, "Frau"));
                String man = getString(row, getRequiredColumnIndex(colMap, "Herr"));

                List<Tenant> tenants = new ArrayList<>();
                if (!woman.isBlank()) {
                    tenants.add(new Tenant(fullNames, woman));
                }
                if (!man.isBlank()) {
                    tenants.add(new Tenant(fullNames, man));
                }

                Apartment apartment = new Apartment(
                        getDouble(row, getRequiredColumnIndex(colMap, "Wfl.m2")),  // Wohnfläche
                        getString(row, getRequiredColumnIndex(colMap, "Balkon")),   // Balkon
                        getString(row, getRequiredColumnIndex(colMap, "Lage")),    // Lage
                        getString(row, getRequiredColumnIndex(colMap, "Straße")),  // Straße
                        getString(row, getRequiredColumnIndex(colMap, "Zustand")), // Zustand
                        getString(row, getRequiredColumnIndex(colMap, "Beheizung")) // Beheizg.
                );

                String suspended = getString(row, getRequiredColumnIndex(colMap, "Ausgesetzt"));

                PreviousRentAdjustment previous = new PreviousRentAdjustment(
                        (int) getDouble(row, getRequiredColumnIndex(colMap, "Jahr alt")),
                        getString(row, getRequiredColumnIndex(colMap, "Monat alt")),
                        getDouble(row, getRequiredColumnIndex(colMap, "Miete alt")),
                        getDouble(row, getRequiredColumnIndex(colMap, "€/m2 alt"))
                );

                double operatingCosts = getDouble(row, getRequiredColumnIndex(colMap, "BK Voraus"));
                double heatingCosts = getDouble(row, getRequiredColumnIndex(colMap, "HK Voraus"));

                Renter renter = new Renter(tenants, apartment, suspended, previous, operatingCosts, heatingCosts);
                renters.add(renter);
            }

        }

        return renters;
    }

    private static int getRequiredColumnIndex(Map<String, Integer> colMap, String colName) {
        Integer idx = colMap.get(colName);
        if (idx == null) {
            throw new IllegalStateException("Missing required column: " + colName);
        }
        return idx;
    }


    private static String getString(Row row, int col) {
        Cell cell = row.getCell(col);
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                return String.valueOf(cell.getNumericCellValue());
            case FORMULA:
                FormulaEvaluator evaluator = cell.getSheet().getWorkbook().getCreationHelper().createFormulaEvaluator();
                CellValue value = evaluator.evaluate(cell);
                return value.getStringValue().trim();

            case BOOLEAN:
            case BLANK:
            case _NONE:
            case ERROR:
            default:
                return "";
        }
    }


    private static double getDouble(Row row, int col) {
        Cell cell = row.getCell(col);
        if (cell == null) return 0.0;

        switch (cell.getCellType()) {
            case FORMULA:
                FormulaEvaluator evaluator = cell.getSheet().getWorkbook().getCreationHelper().createFormulaEvaluator();
                CellValue value = evaluator.evaluate(cell);
                return value.getNumberValue();

            case STRING:
                String val = cell.getStringCellValue().trim().replace(",", ".");
                return Double.parseDouble(val);

            case NUMERIC:
                return cell.getNumericCellValue();

            case BLANK:
            case BOOLEAN:
            case ERROR:
            default:
                return 0.0;
        }
    }
}
