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
    public static void createNewOverview(String filename, String outputPath, List<Renter> renters) throws IOException {
        try (InputStream is = RenterDataLoader.class.getClassLoader().getResourceAsStream(filename);
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
                    throw new IllegalStateException("Mismatch between renter data and Excel row at index "
                            + row.getRowNum());
                }

                RentAdjustment adjustment = renter.getRentAdjustment();

                ExcelUtil.setValue(row, "VPIalt", colMap, adjustment.getOldVpi().value());
                ExcelUtil.setValue(row, "VPIneu", colMap, adjustment.getNewVpi().value());
                ExcelUtil.setValue(row, "% mgl.", colMap, adjustment.getPercentPossible());

                // if rent will be adjusted
                if (adjustment.isWillAdjustRent()) {
                    ExcelUtil.setValue(row, "Jahr neu", colMap, adjustment.getNewVpi().year());
                    ExcelUtil.setValue(row, "Monat neu", colMap, adjustment.getNewVpi().month());
                    ExcelUtil.setValue(row, "€", colMap, adjustment.getRentDifference());
                    ExcelUtil.setValue(row, "Miete neu", colMap, adjustment.getNewRent());
                    ExcelUtil.setValue(row, "€", colMap, adjustment.getNewRentPerSqm());

                }


            }

            // Write updated workbook to a new file
            try (OutputStream os = new FileOutputStream(outputPath + filename + "_neu")) {
                workbook.write(os);
            }
        }
    }
}
