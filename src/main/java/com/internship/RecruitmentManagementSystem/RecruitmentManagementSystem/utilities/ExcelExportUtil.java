package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.utilities;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.BulkUploadJob;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.BulkUploadRowResult;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.util.List;

public class ExcelExportUtil {

    public static String generateSuccessExcel(BulkUploadJob job, List<BulkUploadRowResult> results, String basePath) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Success Rows");
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Row Number");
            header.createCell(1).setCellValue("Status");

            int rowIndex = 1;
            for (BulkUploadRowResult r : results) {
                if (r.isSuccess()) {
                    Row row = sheet.createRow(rowIndex++);
                    row.createCell(0).setCellValue(r.getRowNum());
                    row.createCell(1).setCellValue("SUCCESS");
                }
            }

            String filePath = basePath + "/success_rows_" + job.getJobId() + ".xlsx";
            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                workbook.write(fos);
            }
            return filePath;
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate success Excel: " + e.getMessage());
        }
    }

    public static String generateErrorExcel(BulkUploadJob job, List<BulkUploadRowResult> results, String basePath) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Error Rows");
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Row Number");
            header.createCell(1).setCellValue("Error Message");

            int rowIndex = 1;
            for (BulkUploadRowResult r : results) {
                if (!r.isSuccess()) {
                    Row row = sheet.createRow(rowIndex++);
                    row.createCell(0).setCellValue(r.getRowNum());
                    row.createCell(1).setCellValue(r.getErrorMessage());
                }
            }

            String filePath = basePath + "/error_rows_" + job.getJobId() + ".xlsx";
            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                workbook.write(fos);
            }
            return filePath;
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate error Excel: " + e.getMessage());
        }
    }
}

