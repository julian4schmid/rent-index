package com.julianschmid.rentindex.util;

import com.julianschmid.rentindex.model.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class RenterDataLoader {

    public static List<Renter> loadRenters(String fileName) {
        List<Renter> renters = new ArrayList<>();

        try (InputStream is = RenterDataLoader.class.getClassLoader().getResourceAsStream(fileName);
             Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheetAt(0); // Use first sheet
            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row == null) continue;

                // Extract and process tenant names
                String fullNames = getString(row, 0);
                String frauLastName = getString(row, 1).toLowerCase();
                String herrLastName = getString(row, 2).toLowerCase();

                List<Tenant> tenants = new ArrayList<>();
                for (String name : fullNames.split(" und ")) {
                    name = name.trim();
                    String lastName = name.substring(name.lastIndexOf(' ') + 1).toLowerCase();

                    String gender = "unknown";
                    if (!frauLastName.isBlank() && lastName.equals(frauLastName)) gender = "woman";
                    else if (!herrLastName.isBlank() && lastName.equals(herrLastName)) gender = "man";

                    tenants.add(new Tenant(name, gender));
                }

                Apartment apartment = new Apartment(
                        getDouble(row, 3),  // Wfl.m2
                        getString(row, 4),  // Balkon
                        getString(row, 5),  // Lage
                        getString(row, 6),  // Straße
                        getString(row, 7),  // Zustand
                        getString(row, 8)   // Beheizg.
                );

                String suspended = getString(row, 9);

                PreviousRentIncrease increase = new PreviousRentIncrease(
                        (int) getDouble(row, 10), // Jahr alt
                        (int) getDouble(row, 11), // Monat alt
                        getDouble(row, 12),       // Miete alt
                        getDouble(row, 13)        // €/m2 alt
                );

                double operatingCosts = getDouble(row, 14);
                double heatingCosts = getDouble(row, 15);

                Renter renter = new Renter(tenants, apartment, suspended, increase, operatingCosts, heatingCosts);
                renters.add(renter);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return renters;
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
