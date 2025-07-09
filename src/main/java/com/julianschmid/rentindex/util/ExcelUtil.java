package com.julianschmid.rentindex.util;

import org.apache.poi.ss.usermodel.*;

import java.util.HashMap;
import java.util.Map;

public final class ExcelUtil {

    private ExcelUtil() {
    }

    public static int getColumnIndex(Map<String, Integer> colMap, String colName) {
        Integer idx = colMap.get(colName);
        if (idx == null) {
            throw new IllegalStateException("Missing required column: " + colName);
        }
        return idx;
    }

    public static Map<String, Integer> createColMap(Row headerRow) {
        if (headerRow == null) {
            throw new IllegalStateException("Sheet is missing header row");
        }
        Map<String, Integer> colMap = new HashMap<>();
        for (Cell cell : headerRow) {
            String colName = cell.getStringCellValue().trim();
            colMap.put(colName, cell.getColumnIndex());
        }
        return colMap;
    }

    public static void setValue(Row row, String colName, Map<String, Integer> colMap, String val) {
        row.createCell(ExcelUtil.getColumnIndex(colMap, colName)).setCellValue(val);
    }

    public static void setValue(Row row, String colName, Map<String, Integer> colMap, double val) {
        row.createCell(ExcelUtil.getColumnIndex(colMap, colName)).setCellValue(val);
    }

    public static void setValueByCellRef(Sheet sheet, String cellRef, double val) {
        getOrCreateCell(sheet, cellRef).setCellValue(val);
    }

    public static void setValueByCellRef(Sheet sheet, String cellRef, String val) {
        getOrCreateCell(sheet, cellRef).setCellValue(val);
    }

    public static Cell getOrCreateCell(Sheet sheet, String cellRef) {
        int rowIndex = Integer.parseInt(cellRef.replaceAll("[A-Z]+", "")) - 1;
        String colLetters = cellRef.replaceAll("[0-9]+", "");
        int colIndex = columnLettersToIndex(colLetters);

        Row row = sheet.getRow(rowIndex);
        if (row == null) {
            row = sheet.createRow(rowIndex);
        }

        Cell cell = row.getCell(colIndex);
        if (cell == null) {
            cell = row.createCell(colIndex);
        }

        return cell;
    }

    private static int columnLettersToIndex(String letters) {
        int result = 0;
        for (char c : letters.toUpperCase().toCharArray()) {
            result = result * 26 + (c - 'A' + 1);
        }
        return result - 1; // zero-based index
    }


    public static String getString(Row row, int col) {
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


    public static double getDouble(Row row, int col) {
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
