
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

public class RailwayConcession {

   private final String url = "jdbc:mysql://localhost:3306/mydb"; // Database URL
   private final String user = "root"; // Database username
   private final String password = "Rajnikant@1"; // Database password
   private Connection con;

   public RailwayConcession() {

      try {

         Class.forName("com.mysql.cj.jdbc.Driver");
         con = DriverManager.getConnection(url, user, password);

         if (con != null)
            System.out.println("Connection Successful from class RailwayConcession");

      } catch (SQLException e) {

         System.out.println("Connection Unsuccessful");
         e.printStackTrace();
      } catch (ClassNotFoundException e) {

         System.out.println("Class not Found");
         e.printStackTrace();
      }

   }

   public boolean Verification(int student_id, String student_name, LocalDate student_birthdate) {

      int sid;
      LocalDate sbdate;
      String query1 = "SELECT * FROM users WHERE student_id = ?", sname;

      try {
         PreparedStatement p = con.prepareStatement(query1);
         p.setInt(1, student_id);
         ResultSet rs = p.executeQuery();

         if (!rs.next()) {
            System.out.println("record not found");
            return false;
         } else {
            sid = rs.getInt("student_id");
            System.out.println(sid);
            sname = rs.getString("student_name");
            System.out.println(sname);
            sbdate = rs.getDate("student_birthdate").toLocalDate();
            System.out.println(sbdate);

            if (sid == student_id && sname.equals(student_name) && sbdate.isEqual(student_birthdate)) {
               System.out.println("verified");
               return true;
            } else {
               System.out.println("not verified");
               return false;
            }
         }

      } catch (SQLException e) {

         System.out.println("Error Finding Student" + e.getMessage());
         e.printStackTrace();
         return false;

      }

   }

   public String idGenerator(int id) {

      String query = "SELECT * FROM users WHERE student_id = ?";

      try {

         PreparedStatement p = con.prepareStatement(query);
         p.setInt(1, id);
         ResultSet rs = p.executeQuery();
         rs.next();

         String student_name = rs.getString("student_name");
         int student_id = rs.getInt("student_id");
         String student_birthdate = rs.getDate("student_birthdate").toString();

         String ConcessionId = student_id + student_name + student_birthdate;
         return (ConcessionId);

      } catch (SQLException e) {

         System.out.println("Some error occured while generating Concession ID");
         e.printStackTrace();
         return null;

      }
   }

   public String getConcessionInfo(int id, String cls, String dur, String dep, String des) throws IOException {

      LocalDate today = LocalDate.now();

      String ConcessionId = idGenerator(id);

      String query = "INSERT INTO concession_info (id, concession_id, issue_date, season_duration, boagie_class, departure_station, destination_station) VALUES (?,?,?,?,?,?,?)";

      try {

         PreparedStatement p = con.prepareStatement(query);
         p.setInt(1, id);
         p.setString(2, ConcessionId);
         p.setDate(3, Date.valueOf(today));
         p.setString(4, dur);
         p.setString(5, cls);
         p.setString(6, dep);
         p.setString(7, des);

         p.executeUpdate();
         System.out.println("Concession info of student number" + id + "has been updated");
         return ConcessionId;

      } catch (SQLException e) {

         e.printStackTrace();
         System.out.println("error occured while getting info");
         return null;

      }

   }

   public boolean editForm(int ID, String cls, String duration, String depart, String dest) {

      try {
         PreparedStatement psmt = con.prepareStatement(
               "UPDATE concession_info SET boagie_class = ?, season_duration = ?, departure_station = ?, destination_station = ? WHERE id = ?");
         psmt.setString(1, cls);
         psmt.setString(2, duration);
         psmt.setString(3, depart);
         psmt.setString(4, dest);
         psmt.setInt(5, ID);
         psmt.executeUpdate();
         return true;
      } catch (SQLException e) {
         e.printStackTrace();
         return false;
      }

   }

   public ArrayList<Object> historyChecker(int student_id) {

      try {
         PreparedStatement pstm = con.prepareStatement("SELECT * FROM history WHERE id = ?");
         pstm.setInt(1, student_id);
         ResultSet rs = pstm.executeQuery();

         ArrayList<Object> pastForms = new ArrayList<Object>();

         if (rs.next()) {
            pastForms.add(rs.getString("season_duration"));
            pastForms.add(rs.getString("boagie_class"));
            pastForms.add(rs.getString("departure_station"));
            pastForms.add(rs.getString("destination_station"));
            pastForms.add(rs.getString("issue_date"));
            pastForms.add(rs.getString("print_date"));
            
            while (rs.next()) {
               pastForms.add(rs.getString("season_duration"));
               pastForms.add(rs.getString("boagie_class"));
               pastForms.add(rs.getString("departure_station"));
               pastForms.add(rs.getString("destination_station"));
               pastForms.add(rs.getString("issue_date"));
               pastForms.add(rs.getString("print_date"));
               
            }
            return pastForms;
         } else {
            pastForms.add("no history found");
            return pastForms;
         }
      } catch (SQLException e) {
         e.printStackTrace();
         return null;
      }

   }

}
