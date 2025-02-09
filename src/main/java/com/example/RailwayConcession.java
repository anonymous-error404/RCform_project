package com.example;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class RailwayConcession{

   private int student_id;
   private String student_name;
   private String student_birthdate;
   private String local_class;
   private String duration;
   private String date;
   private String departure;
   private String destination;

   public RailwayConcession(int student_id, String student_name, String student_birthdate){
      
      this.student_id = student_id;
      this.student_name = student_name;
      this.student_birthdate = student_birthdate;

      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

      LocalDate birthDate = LocalDate.parse(student_birthdate,formatter); 

      Calendar c = Calendar.getInstance();
 
      if(c.get(Calendar.YEAR)-birthDate.getYear() > 25 || c.get(Calendar.YEAR)-birthDate.getYear() < 11){
       
         System.out.println("You are not eligible for Railway Concession");
      }
      else{

         System.out.println("You are eligible, please proceed");

      }
    }

    public String idGenerator(){

      String ConcessionId = student_id+student_name+student_birthdate;
      return(ConcessionId);
   }

   public void getConcessionInfo() throws IOException{

      String excelFilePath = "src/main/resources/students.xlsx"; // Change this path as necessary
      ExcelHandler handler = new ExcelHandler(excelFilePath);

      Scanner d = new Scanner(System.in);
      ArrayList<String> list = new ArrayList<String>();
      
      System.out.println("Enter the class :");
      this.local_class = d.next();
      System.out.println("Enter Period  :");
      this.duration = d.next();
      System.out.println("Enter Departure Station :");
      this.departure = d.next();
      System.out.println("Enter Destination Station :");
      this.destination = d.next();
      
      Calendar c = Calendar.getInstance();
      
      this.date = String.format("%02d/%02d/%04d", c.get(Calendar.DATE), c.get(Calendar.MONTH)+1, c.get(Calendar.YEAR));

      String ConcessionId = idGenerator();

      list.add(ConcessionId);
      list.add(date);
      list.add(local_class);
      list.add(duration);
      list.add(departure);
      list.add(destination);

      handler.writeToExcel(list);

   }

    public static void main(String[] args) throws IOException {
      
      Scanner sc = new Scanner(System.in);

      System.out.println("Enter your Student ID:");
      int id = sc.nextInt();
      System.out.println("Enter your Name:");
      String name = sc.next();
      System.out.println("Enter your Birth Date: (DD/MM/YYYY)");
      String date = sc.next();

      RailwayConcession rc = new RailwayConcession(id,name,date);
      rc.getConcessionInfo();
      
      System.out.println("Your Concession ID is Successfully Generated!\n Concession ID : "+rc.idGenerator()+"\nPlease collect your Railway Concession Pass from the Pass Vending Machine!");

    }

}


