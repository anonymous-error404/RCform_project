package com.example;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.time.format.DateTimeFormatter;
public class PassGenerator {

    public static void main(String[] args) throws IOException {
        
        String excelFilePath = "src/main/resources/students.xlsx"; // Change this path as necessary
        ExcelHandler handler = new ExcelHandler(excelFilePath);

        handler.deleteRow();

        Scanner sc = new Scanner(System.in);
        System.out.println("Enter your ConcessionID");
        String ID = sc.nextLine();
        
        ArrayList<String> student_info = handler.readFromExcel(ID);

        if(student_info == null){

            System.out.println("Oh No!\nLooks like either your Session date has Expired or You haven't applied.\nPlease Apply for Railway Concession Again");
            
        }
        
        else {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        LocalDate expiryDate = LocalDate.parse(student_info.get(1),formatter);
        expiryDate = expiryDate.plusMonths(3);
           
            System.out.println("Railway Pass(With applied Concession)\n");
            System.out.println("From : "+student_info.get(4)+"\t\t\t\t\tTo : "+student_info.get(5));
            System.out.println("Issue date : "+student_info.get(1)+"\t\t\t\tExpiry Date : "+expiryDate);
            System.out.println("Class : "+student_info.get(2)+"\t\t\t\t\tPeriod : "+student_info.get(3));
            System.out.println("Concession ID : "+student_info.get(0));
                
            }

    }
        
}


