package com.julianschmid.rentindex.writer;


import com.julianschmid.rentindex.model.*;
import com.julianschmid.rentindex.util.*;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;


public final class OverviewWriter {
    public static void createAdjustmentOverview(String filename, List<Renter> renters) throws IOException {
        System.out.println("create adjustment overview");

        String path = ResourceUtil.getDataPath();
        try (InputStream is = OverviewWriter.class.getClassLoader().getResourceAsStream(path + filename);
             Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheetAt(0);

            // Get header row and find where to write new columns
            Row headerRow = sheet.getRow(0);
            Map<String, Integer> colMap = ExcelUtil.createColMap(headerRow);

            // Write new values for each row
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null || row.getCell(0) == null || row.getCell(0).getCellType() == CellType.BLANK) {
                    continue;
                }

                Renter renter = renters.get(i - 1); // assuming same order
                if (!renter.getTenants().getFirst().fullNames().equals(row.getCell(0).toString())) {
                    System.out.println(renter);
                    System.out.println(row);
                    throw new IllegalStateException("Mismatch between renter data and Excel row at index "
                            + row.getRowNum());
                }

                RentAdjustment adjustment = renter.getRentAdjustment();

                ExcelUtil.setValue(row, "VPI alt", colMap, adjustment.getOldVpi().value());
                ExcelUtil.setValue(row, "VPI neu", colMap, adjustment.getNewVpi().value());
                ExcelUtil.setValue(row, "% mgl.", colMap, adjustment.getPercentPossible());

                // if rent will be adjusted
                if (adjustment.isWillAdjustRent()) {
                    ExcelUtil.setValue(row, "Jahr neu", colMap, adjustment.getNewVpi().year());
                    ExcelUtil.setValue(row, "Monat neu", colMap, adjustment.getNewVpi().month());
                    ExcelUtil.setValue(row, "€", colMap, adjustment.getRentDifference());
                    ExcelUtil.setValue(row, "%", colMap, adjustment.getPercentIncrease());
                    ExcelUtil.setValue(row, "Miete neu", colMap, adjustment.getNewRent());
                    ExcelUtil.setValue(row, "€/m2 neu", colMap, adjustment.getNewRentPerSqm());
                } else {

                    ExcelUtil.setValue(row, "Grund", colMap, adjustment.getReason());
                }
            }

            // Write updated workbook to a new file inside target/path
            VpiRecord currentVpi = renters.getFirst().getRentAdjustment().getNewVpi();
            String outputFilename = String.format(
                    "Index_Mieterhöhungen_%d_%s.xlsx",
                    currentVpi.year(),
                    currentVpi.month());
            String outputPath = "target/" + path;
            try (OutputStream os = new FileOutputStream(outputPath + outputFilename)) {
                workbook.write(os);
            }

            createNewOverview(path, filename, renters, colMap);
        }
    }

    public static void createNewOverview(String path, String filename, List<Renter> renters,
                                         Map<String, Integer> colMap) throws IOException {
        System.out.println("create new overview after rent adjustments");

        try (InputStream is = OverviewWriter.class.getClassLoader().getResourceAsStream(path + filename);
             Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheetAt(0);

            // Write new values for each row
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null || row.getCell(0) == null || row.getCell(0).getCellType() == CellType.BLANK) {
                    continue;
                }

                // assuming same order
                RentAdjustment adjustment = renters.get(i - 1).getRentAdjustment();
                if (adjustment.isWillAdjustRent()) {
                    ExcelUtil.setValue(row, "Jahr alt", colMap, adjustment.getNewVpi().year());
                    ExcelUtil.setValue(row, "Monat alt", colMap, adjustment.getNewVpi().month());
                    ExcelUtil.setValue(row, "Miete alt", colMap, adjustment.getNewRent());
                }
            }

            String outputFilename = "Indexmieten_Übersicht_neu.xlsx";
            String outputPath = "target/" + path;
            try (OutputStream os = new FileOutputStream(outputPath + outputFilename)) {
                workbook.write(os);
            }
        }
    }
}
