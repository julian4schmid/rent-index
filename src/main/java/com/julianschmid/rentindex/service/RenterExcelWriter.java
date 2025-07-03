package com.julianschmid.rentindex.service;


import com.julianschmid.rentindex.model.*;
import com.julianschmid.rentindex.util.*;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;


public class RenterExcelWriter {
    public static void createNewOverview(String filename, List<Renter> renters) throws IOException {
        String path = ResourceUtil.getDataPath();
        try (InputStream is = RenterDataLoader.class.getClassLoader().getResourceAsStream(path + filename);
             Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheetAt(0);

            // Get header row and find where to write new columns
            Row headerRow = sheet.getRow(0);
            Map<String, Integer> colMap = ExcelUtil.createColMap(headerRow);

            // Write new values for each row
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null || row.getCell(0) == null || row.getCell(0).getCellType() == CellType.BLANK) continue;

                Renter renter = renters.get(i - 1); // assuming same order
                if (!renter.getTenants().getFirst().fullName().equals(row.getCell(0).toString())) {
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
            filename = filename.substring(0, filename.length() - 5) + "_neu.xlsx";
            path = "target/" + path;
            try (OutputStream os = new FileOutputStream(path + filename)) {
                workbook.write(os);
            }
        }
    }
}
