package com.alienCoders.moneymanger.util;

import com.alienCoders.moneymanger.dto.RecentTransactionDTO;
import jakarta.transaction.Transaction;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

@Component
public class ExcelGenerator {

    /**
     * This method receives a list of transactions and converts them into
     * an Excel (.xlsx) file using Apache POI.
     *
     * It returns the Excel file as ByteArrayInputStream so that
     * Spring Boot can send it as a downloadable file.
     */
    public  ByteArrayInputStream transactionToExcel(List<RecentTransactionDTO> transactions, String heading){

        // These are the column headings that will appear in Excel's first row
        String[] columns={"ID","Name","Amount","Category","Date"};

        /**
         * try-with-resources:
         * Workbook = Excel file in memory
         * ByteArrayOutputStream = used to convert Excel file to bytes
         *
         * Example:
         * - Workbook workbook = new XSSFWorkbook() → create new empty Excel file
         * - out = new ByteArrayOutputStream() → create a container to hold the Excel data
         */

       try(
               Workbook workbook=new XSSFWorkbook();
               ByteArrayOutputStream outputStream=new ByteArrayOutputStream()
               ){
          Sheet sheet=workbook.createSheet(heading);
          CellStyle headerStyle=workbook.createCellStyle();
          Font font=workbook.createFont();
          font.setBold(true);
          headerStyle.setFont(font);

           /**
            * Create Header Row
            * Row 0 → contains column titles
            */
           Row headerRow=sheet.createRow(0);

           // Loop to add all column headings
           for(int i=0;i< columns.length;i++){
               Cell cell=headerRow.createCell(i);
               cell.setCellValue(columns[i]);
               cell.setCellStyle(headerStyle);
           }

           int rowIdx=1;
               for(RecentTransactionDTO tx:transactions){
                 Row row=sheet.createRow(rowIdx++);
                 row.createCell(0).setCellValue(tx.getId());
                 row.createCell(1).setCellValue(tx.getName());
                 row.createCell(2).setCellValue(tx.getAmount().doubleValue());
                 row.createCell(3).setCellValue(tx.getCategoryName());
                 row.createCell(4).setCellValue(tx.getDate().toString());
               }
               workbook.write(outputStream);
               return new ByteArrayInputStream(outputStream.toByteArray());
       }
       catch (Exception e){
           e.printStackTrace();
           return null;
       }
    }

}
