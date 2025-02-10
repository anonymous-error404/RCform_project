import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.sql.*;

class Login {

   private final String url = "jdbc:mysql://localhost:3306/mydb"; // Database URL
   private final String user = "root"; // Database username
   private final String password = "Rajnikant@1"; // Database password
   private Connection con;

   public Login() {

      try {

         Class.forName("com.mysql.cj.jdbc.Driver");
         con = DriverManager.getConnection(url, user, password);

         if (con != null)
            System.out.println("Connection Successful from class Login");

      } catch (SQLException e) {

         System.out.println("Connection Unsuccessful");
         e.printStackTrace();
      } catch (ClassNotFoundException e) {

         System.out.println("Class not Found");
         e.printStackTrace();
      }
   }

   public String register(String uname, String pswd, ArrayList<Object> inputData) {

      System.out.println(inputData);

      try {

            PreparedStatement psmt1 = con
                  .prepareStatement("SELECT * FROM accounts WHERE student_id = ? AND username = ?");
            psmt1.setInt(1, (Integer) inputData.get(0));
            psmt1.setString(2, uname);
            ResultSet rs = psmt1.executeQuery();

            if (!rs.next()) {

               PreparedStatement psmt2 = con.prepareStatement("SELECT * FROM accounts WHERE username = ?");
               psmt2.setString(1, uname);
               ResultSet rs1 = psmt2.executeQuery();
      
               if (!rs1.next()){

               PreparedStatement psmt = con.prepareStatement(
                     "INSERT INTO accounts (student_id, student_name, student_birthdate, address, phone_number, department, year_of_study, username, password) VALUES (?,?,?,?,?,?,?,?,?)");

               psmt.setInt(1, (Integer) inputData.get(0));
               psmt.setString(2, (String) inputData.get(1));
               psmt.setDate(3, Date.valueOf((LocalDate) inputData.get(2)));
               psmt.setString(4, (String) inputData.get(3));
               psmt.setString(5, (String) inputData.get(4));
               psmt.setString(6, (String) inputData.get(5));
               psmt.setString(7, (String) inputData.get(6));
               psmt.setString(8, uname);
               psmt.setString(9, pswd);
               psmt.executeUpdate();

               return "registered";
               }
               else
               return "username exists";
            } else {
               return "account exists";
            }

      } catch (Exception e) {

         e.printStackTrace();
         return "Error";
      }
   }

   public ArrayList<Object> login(String username, String password) {

      try {
         String duration = null;
         LocalDate print_Date = null;
         PreparedStatement psmt = con.prepareStatement("SELECT student_id,student_name FROM accounts WHERE username = ? AND password = ?");
         psmt.setString(1, username);
         psmt.setString(2, password);
         ResultSet rs = psmt.executeQuery();

         if (!rs.next())
            return null;
         else {

            PreparedStatement psmt2 = con.prepareStatement("SELECT season_duration,print_date FROM history WHERE id = ?");
            psmt2.setInt(1, rs.getInt("student_id"));
            ResultSet rs2 = psmt2.executeQuery();
            boolean isnull = true;

            while (rs2.next()) {
               isnull = false;
               duration = rs2.getString("season_duration");
               print_Date = rs2.getDate("print_date").toLocalDate();
            }

            System.out.println(isnull+"\n"+duration+"\n"+print_Date);

            if (!isnull && duration.equals("Monthly")
                  && LocalDate.now().isBefore(print_Date.plusMonths(1).plusDays(3))) {

               ArrayList<Object> userData = new ArrayList<Object>();
               userData.add(username);
               userData.add(rs.getString("student_name"));
               userData.add(rs.getInt("student_id"));
               userData.add("Already has one issued");
               userData.add(password);
               return userData;

            } else if (!isnull && duration.equals("Quarterly")
                  && LocalDate.now().isBefore(print_Date.plusMonths(3).plusDays(3))) {

               ArrayList<Object> userData = new ArrayList<Object>();
               userData.add(username);
               userData.add(rs.getString("student_name"));
               userData.add(rs.getInt("student_id"));
               userData.add("Already has one issued");
               userData.add(password);
               return userData;

            } else if (!isnull && duration.equals("Half Yearly")
                  && LocalDate.now().isBefore(print_Date.plusMonths(6).plusDays(3))) {

               ArrayList<Object> userData = new ArrayList<Object>();
               userData.add(username);
               userData.add(rs.getString("student_name"));
               userData.add(rs.getInt("student_id"));
               userData.add("Already has one issued");
               userData.add(password);
               return userData;

            } else if (!isnull && duration.equals("Yearly")
                  && LocalDate.now().isBefore(print_Date.plusMonths(12).plusDays(3))) {

               ArrayList<Object> userData = new ArrayList<Object>();
               userData.add(username);
               userData.add(rs.getString("student_name"));
               userData.add(rs.getInt("student_id"));
               userData.add("Already has one issued");
               userData.add(password);
               return userData;

            } else {

               PreparedStatement psmt1 = con.prepareStatement("SELECT * FROM concession_info WHERE id = ?");
               psmt1.setInt(1, rs.getInt("student_id"));
               ResultSet rs1 = psmt1.executeQuery();

               if (!rs1.next()) {

                  ArrayList<Object> userData = new ArrayList<Object>();
                  userData.add(username);
                  userData.add(rs.getString("student_name"));
                  userData.add(rs.getInt("student_id"));
                  userData.add("no existing form");
                  userData.add(password);
                  return userData;

               } else {

                  ArrayList<Object> userData = new ArrayList<Object>();
                  userData.add(username);
                  userData.add(rs.getString("student_name"));
                  userData.add(rs.getInt("student_id"));
                  userData.add("form exists");
                  userData.add(rs1.getString("season_duration"));
                  userData.add(rs1.getString("boagie_class"));
                  userData.add(rs1.getString("departure_station"));
                  userData.add(rs1.getString("destination_station"));
                  userData.add(rs1.getString("concession_id"));
                  userData.add(password);
                  return userData;

               }

            }
         }

      } catch (SQLException e) {

         e.printStackTrace();
         return null;
      }

   }

   public ArrayList getuseraccount(String username, String stu_name, Integer stu_id){

      try{
      PreparedStatement psmt = con.prepareStatement("SELECT * FROM accounts WHERE student_id = ? AND username = ?");
      psmt.setInt(1, stu_id);
      psmt.setString(2, username);
      ResultSet rs = psmt.executeQuery();
      rs.next();

      ArrayList<Object> user_account = new ArrayList<Object>();
      user_account.add(username);
      user_account.add(stu_id);
      user_account.add(stu_name);
      user_account.add(rs.getString("student_birthdate"));
      user_account.add(rs.getString("address"));
      user_account.add(rs.getString("phone_number"));
      user_account.add(rs.getString("department"));
      user_account.add(rs.getString("year_of_study"));
      return user_account;

      }
      catch(Exception e){
         e.printStackTrace();
         return null;
      }
   }

   public void changeUsername(String username, String newUname){

      try{
      PreparedStatement preparedStatement = con.prepareStatement("UPDATE accounts SET username = ? WHERE username = ?");
      preparedStatement.setString(1, newUname);
      preparedStatement.executeUpdate();
      }
      catch(SQLException e){
         e.printStackTrace();
      }

   }

   public void changeId(String username, int newId){

      try{
         PreparedStatement preparedStatement = con.prepareStatement("UPDATE accounts SET student_id = ? WHERE username = ?");
         preparedStatement.setInt(1, newId);
         preparedStatement.executeUpdate();
         }
         catch(SQLException e){
            e.printStackTrace();
         }

   }

   public void changeName(String username, String newName){

      try{
         PreparedStatement preparedStatement = con.prepareStatement("UPDATE accounts SET student_name = ? WHERE username = ?");
         preparedStatement.setString(1, newName);
         preparedStatement.executeUpdate();
         }
         catch(SQLException e){
            e.printStackTrace();
         }

   }

   public void changeBdate(String username, String newBdate){

      try{
         PreparedStatement preparedStatement = con.prepareStatement("UPDATE accounts SET student_birthdate = ? WHERE username = ?");
         preparedStatement.setDate(1, Date.valueOf(newBdate));
         preparedStatement.executeUpdate();
         }
         catch(SQLException e){
            e.printStackTrace();
         }

   }

   public void changeAdd(String username, String newAdd){

      try{
         PreparedStatement preparedStatement = con.prepareStatement("UPDATE accounts SET address = ? WHERE username = ?");
         preparedStatement.setString(1, newAdd);
         preparedStatement.executeUpdate();
         }
         catch(SQLException e){
            e.printStackTrace();
         }

   }

   public void changePhno(String username, String newPhno){

      try{
         PreparedStatement preparedStatement = con.prepareStatement("UPDATE accounts SET phone_number = ? WHERE username = ?");
         preparedStatement.setString(1, newPhno);
         preparedStatement.executeUpdate();
         }
         catch(SQLException e){
            e.printStackTrace();
         }

   }

   public void changeDept(String username, String newDept){

      try{
         PreparedStatement preparedStatement = con.prepareStatement("UPDATE accounts SET department = ? WHERE username = ?");
         preparedStatement.setString(1, newDept);
         preparedStatement.executeUpdate();
         }
         catch(SQLException e){
            e.printStackTrace();
         }

   }

   public void changeYear(String username, String newYear){

      try{
         PreparedStatement preparedStatement = con.prepareStatement("UPDATE accounts SET year_of_study = ? WHERE username = ?");
         preparedStatement.setString(1, newYear);
         preparedStatement.executeUpdate();
         }
         catch(SQLException e){
            e.printStackTrace();
         }

   }
}
