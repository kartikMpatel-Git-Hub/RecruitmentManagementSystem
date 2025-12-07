package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.utilities;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class ExcelUtils {

    public static String getString(Row row, int cellIndex) {
        Cell cell = row.getCell(cellIndex);
        if (cell == null) return "";
        if (cell.getCellType() == CellType.STRING) {
            return cell.getStringCellValue().trim();
        }
        if (cell.getCellType() == CellType.NUMERIC) {
            double val = cell.getNumericCellValue();
            long longVal = (long) val;
            if (val == longVal) {
                return String.valueOf(longVal);
            }
            return String.valueOf(val);
        }
        return "";
    }

    public static Integer getInteger(Row row, int cellIndex) {
        try {
            Cell cell = row.getCell(cellIndex);
            if (cell == null) return null;
            if (cell.getCellType() == CellType.NUMERIC) {
                return (int) cell.getNumericCellValue();
            }
            if (cell.getCellType() == CellType.STRING) {
                String s = cell.getStringCellValue().trim();
                if (s.isEmpty()) return null;
                return Integer.parseInt(s);
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public static LocalDate getDate(Row row, int cellIndex) {
        try {
            Cell cell = row.getCell(cellIndex);
            if (cell == null) return null;
            if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
                Date date = cell.getDateCellValue();
                return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            }
            if (cell.getCellType() == CellType.STRING) {
                String s = cell.getStringCellValue().trim();
                if (s.isEmpty()) return null;
                return LocalDate.parse(s);
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }
}
