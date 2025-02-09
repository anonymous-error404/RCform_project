package com.example;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.util.*;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ExcelHandler {

    private String filePath;

    public ExcelHandler(String filePath) {
        this.filePath = filePath;
    }

    public void deleteRow() {

        Calendar calendar = Calendar.getInstance();
        
        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {
    
            Sheet sheet = workbook.getSheetAt(0); // Access the first sheet
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
            // Iterate backwards to avoid ConcurrentModificationException
            for (int i = sheet.getLastRowNum(); i >= 0; i--) {
    
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }
    
                Cell issueDateCell = row.getCell(1); // Gets the cell storing issue date
                if (issueDateCell == null || issueDateCell.getCellType() != CellType.STRING) {
                    continue; // Skip rows with invalid date format or empty cells
                }
    
                LocalDate date = LocalDate.parse(issueDateCell.getStringCellValue(), formatter); // Get date of issue
    
                // Compare date to current date + 3 days
                if (calendar.get(Calendar.DATE) > date.plusDays(3).getDayOfMonth()) {
    
                    // Remove the row
                    sheet.removeRow(row);
    
                    // If the row is not the last one, shift remaining rows up to fill the gap
                    if (i < sheet.getLastRowNum()) {
                        sheet.shiftRows(i + 1, sheet.getLastRowNum(), -1);
                    }
                }
            }
    
            // Write changes back to the file
            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                workbook.write(fos); // Save the workbook
            }
    
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    

    // Method to read from the Excel file
    public ArrayList<String> readFromExcel(String id) throws IOException {


        String Cid = id;
        boolean found = false;
         
        ArrayList<String> list = new ArrayList<String>();

        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {
                
            Sheet sheet = workbook.getSheetAt(0); // Read the first sheet

            for(Row row : sheet){

                Cell firstcell = row.getCell(0);

                if(firstcell.getStringCellValue().equals(Cid)){
                    
                    for (Cell cell : row) {
                        
                                list.add(cell.getStringCellValue());
                              
                        }

                        found = true; //flag that tells you that a match is found
                    }
                    System.out.println();
                }
              
              if(found){

                System.out.println("Processing your Railway Pass");
                return(list);
              }
              else{
                System.out.println("Concession ID not found");
                return(null);
              }
        }

    }
                
        
        


    // Method to write to the Excel file
    public void writeToExcel(ArrayList<String> value) throws IOException {

        int emptyrow = 0;
        
        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0); // Write to the first sheet

            emptyrow = sheet.getLastRowNum() + 1;

            Row row = sheet.getRow(emptyrow);
            if (row == null) {
                row = sheet.createRow(emptyrow);
            }

            for(int i = 0; i <= 5; i++){
            Cell cell = row.getCell(i);
            if (cell == null) {
                cell = row.createCell(i);
            }
            cell.setCellValue(value.get(i));
            }

            // Write the changes to the file
            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                workbook.write(fos);
            }
        }
    }

  }


