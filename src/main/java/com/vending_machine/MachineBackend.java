package com.vending_machine;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.print.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.printing.PDFPrintable;
import org.apache.poi.util.Units;

class MachineBackend {

    private final String url = "jdbc:mysql://localhost:3306/mydb"; // Database URL
    private final String user = "root"; // Database username
    private final String password = "Rajnikant@1"; // Database password
    private Connection con;

    public double cmtoPoints(double x) {
        return (x * 28.3465);
    }

    public MachineBackend() {

        try {

            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(url, user, password);

            if (con != null)
                System.out.println("Connection Successful from class MachineBackend");

        } catch (SQLException e) {

            System.out.println("Connection Unsuccessful");
            e.printStackTrace();
        } catch (ClassNotFoundException e) {

            System.out.println("Class not Found");
            e.printStackTrace();
        }
    }

    public ArrayList findForm(String ConcessionID) throws IOException {

        try {

            PreparedStatement psmt = con.prepareStatement("SELECT * FROM concession_info WHERE concession_id =?");
            psmt.setString(1, ConcessionID);
            ResultSet rs = psmt.executeQuery();

            if (!rs.next())
                return null;
            else {

                PreparedStatement psmt1 = con.prepareStatement("SELECT * FROM accounts WHERE student_id = ?");
                psmt1.setInt(1, (Integer) rs.getInt("id"));
                ResultSet rs1 = psmt1.executeQuery();

                rs1.next();
                ArrayList<Object> printForm = new ArrayList<Object>();
                printForm.add(rs1.getString("department")); // 0
                printForm.add(rs1.getString("year_of_study")); // 1
                printForm.add(rs1.getInt("student_id")); // 2
                printForm.add(rs1.getString("student_name")); // 3
                printForm.add(rs1.getDate("student_birthdate")); // 4
                printForm.add(rs.getString("season_duration"));
                printForm.add(rs.getString("boagie_class"));
                printForm.add(rs.getString("departure_station"));
                printForm.add(rs.getString("destination_station"));
                printForm.add(rs.getDate("issue_date").toString()); //5
                printForm.add(rs1.getString("address")); // 6
                printForm.add(rs1.getString("phone_number")); // 7
                printForm.add(rs.getString("concession_id")); // 8

                return printForm;

            }

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void editForm(int ID, String cls, String duration, String depart, String dest) {

        try {
            PreparedStatement psmt = con.prepareStatement(
                    "UPDATE concession_info SET boagie_class = ?, season_duration = ?, departure_station = ?, destination_station = ? WHERE id = ?");
            psmt.setString(1, cls);
            psmt.setString(2, duration);
            psmt.setString(3, depart);
            psmt.setString(4, dest);
            psmt.setInt(5, ID);
            psmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();

        }

    }

    public boolean printform(ArrayList printData) {

        try {

            PreparedStatement ps = con.prepareStatement("SELECT * FROM concession_info WHERE concession_id = ?");
            PreparedStatement pst = con.prepareStatement(
                    "INSERT INTO history (id, concession_id, issue_date, print_date, season_duration, boagie_class, departure_station, destination_station) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
            PreparedStatement ps_delete = con.prepareStatement("DELETE FROM concession_info WHERE concession_id = ?");

            ps.setString(1, (String) printData.get(8));
            ResultSet rs = ps.executeQuery();
            rs.next();

            pst.setInt(1, (Integer) printData.get(2));
            pst.setString(2, (String) printData.get(8));
            pst.setDate(3, Date.valueOf((String)printData.get(5)));
            pst.setDate(4, Date.valueOf(LocalDate.now().toString()));
            pst.setString(5, rs.getString("season_duration"));
            pst.setString(6, rs.getString("boagie_class"));
            pst.setString(7, rs.getString("departure_station"));
            pst.setString(8, rs.getString("destination_station"));
            pst.executeUpdate();

            Canvas canvas = new Canvas(cmtoPoints(30), cmtoPoints(20));
            GraphicsContext gc = canvas.getGraphicsContext2D();

            Image image = new Image(getClass().getResource("/Railway concession form.jpg").toExternalForm());

            gc.drawImage(image, 0, 0, cmtoPoints(30), cmtoPoints(20));
            gc.setFill(Color.BLACK);
            gc.setFont(new Font("Arial", 11));
            gc.strokeText((String) printData.get(3), cmtoPoints(9), cmtoPoints(6.172)); // name =
            gc.strokeText(printData.get(4).toString(), cmtoPoints(12), cmtoPoints(7.4)); // bdate =
            gc.strokeText(String.valueOf(printData.get(9)), cmtoPoints(12.421), cmtoPoints(6.883)); // age_years =
            gc.strokeText(String.valueOf(printData.get(10)), cmtoPoints(17.780), cmtoPoints(6.883));// age_moths =
            gc.setFont(new Font("Arial", 15));
            gc.strokeText(rs.getString("boagie_class"), cmtoPoints(1.762), cmtoPoints(10.033)); // class =
            gc.strokeText(rs.getString("season_duration"), cmtoPoints(5.833), cmtoPoints(10.160)); // duration =
            gc.strokeText(rs.getString("departure_station"), cmtoPoints(11.573), cmtoPoints(10.135)); // depart =
            gc.strokeText(rs.getString("destination_station") ,cmtoPoints(17.452), cmtoPoints(10.135)); // dest =
            gc.setFont(new Font("Arial", 12));
            gc.strokeText(printData.get(5).toString(), cmtoPoints(4.731), cmtoPoints(13.2)); // issue date =

            Printer printer = Printer.getDefaultPrinter();
            PrinterJob job = PrinterJob.createPrinterJob(printer);

            if (job != null) {

                if (job.printPage(canvas)) {
                    job.endJob();
                    System.out.println("Printing Successful");
                    ps_delete.setString(1, (String) printData.get(8));
                    ps_delete.executeUpdate();
                    return true;
                } else {
                    System.out.println("Printing Unsuccessful");
                    return false;
                }
            } else
                return false;

        } catch (SQLException e) {

            e.printStackTrace();
            return false;
        }

    }
}
