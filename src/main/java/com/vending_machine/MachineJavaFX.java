package com.vending_machine;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class MachineJavaFX extends Application {

    private int age_years, age_months;

    public void showTranscriptDialog(Scene scene1, ArrayList<Object> printDataList, Button pButton, Button eButton, MachineBackend mb_parameter) {

        Canvas canvas = new Canvas(mb_parameter.cmtoPoints(30),mb_parameter.cmtoPoints(20));
        Image image = new Image(getClass().getResource("/Railway concession form.jpg").toExternalForm());
        GraphicsContext gc = canvas.getGraphicsContext2D();

        gc.drawImage(image,0,0,mb_parameter.cmtoPoints(30),mb_parameter.cmtoPoints(20));
        gc.setFill(Color.BLACK);
        gc.setFont(new Font("Arial",11));
        gc.strokeText((String)printDataList.get(3), mb_parameter.cmtoPoints(9), mb_parameter.cmtoPoints(6.172)); //name =
        gc.strokeText(printDataList.get(4).toString(),  mb_parameter.cmtoPoints(12),  mb_parameter.cmtoPoints(7.4)); //bdate =
        gc.strokeText(String.valueOf(printDataList.get(13)),  mb_parameter.cmtoPoints(12.421),  mb_parameter.cmtoPoints(6.883)); //age_years =
        gc.strokeText(String.valueOf(printDataList.get(14)), mb_parameter.cmtoPoints(17.780), mb_parameter.cmtoPoints(6.883));//age_moths =
        gc.setFont(new Font("Arial",15));
        gc.strokeText((String)printDataList.get(6),  mb_parameter.cmtoPoints(1.762),  mb_parameter.cmtoPoints(10.033)); //class =
        gc.strokeText((String)printDataList.get(5),  mb_parameter.cmtoPoints(5.833),  mb_parameter.cmtoPoints(10.160)); //duration =
        gc.strokeText((String)printDataList.get(7),  mb_parameter.cmtoPoints(11.573),  mb_parameter.cmtoPoints(10.135)); //depart =
        gc.strokeText((String)printDataList.get(7),  mb_parameter.cmtoPoints(2.489),  mb_parameter.cmtoPoints(11.557)); //depart =
        gc.strokeText((String)printDataList.get(8),  mb_parameter.cmtoPoints(17.452),  mb_parameter.cmtoPoints(10.135)); //dest =
        gc.strokeText((String)printDataList.get(8),  mb_parameter.cmtoPoints(15.189),  mb_parameter.cmtoPoints(11.557)); //dest =
        gc.setFont(new Font("Arial",12));
        gc.strokeText(LocalDate.now().toString(),  mb_parameter.cmtoPoints(4.731),  mb_parameter.cmtoPoints(13.2)); //print date =

        HBox hbox = new HBox(eButton,pButton);
        hbox.setSpacing(10);
        VBox vbox = new VBox(canvas,hbox);
        vbox.setSpacing(10);
        StackPane sp = new StackPane();
        hbox.setAlignment(Pos.CENTER);
        vbox.setAlignment(Pos.CENTER);
        sp.setAlignment(Pos.CENTER);
        sp.getChildren().addAll(vbox);
        scene1.setRoot(sp);

    }

    public void start(Stage primaryStage) throws IOException {

        MachineBackend mb = new MachineBackend();
        MachineJavaFX m = new MachineJavaFX();

        // Create a grid layout for the form
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setAlignment(Pos.CENTER);

        // Add form labels and fields
        Label IDLabel = new Label("Concession ID:");
        TextField IDField = new TextField();
        grid.add(IDLabel, 0, 0);
        grid.add(IDField, 1, 0);

        Button submitButton = new Button("Submit");
        grid.add(submitButton, 0, 1);

        // Create a print button
        Button printButton = new Button("Print Form");
        // Create an edit button
        Button editButton = new Button("Edit Form");
        // Create an confirm edit button
        Button confirmEdit = new Button("Confirm Edit");

        // Create editing fields
        TextField ClassValue = new TextField();
        TextField DurationValue = new TextField();
        TextField DepartureValue = new TextField();
        TextField DestinationValue = new TextField();

        Scene scene = new Scene(grid);
        primaryStage.setTitle("Form Vending Machine");
        primaryStage.setScene(scene);
        primaryStage.setFullScreen(true);
        primaryStage.show();

        submitButton.setOnAction(event -> {

            try {

                ArrayList<Object> printData = (ArrayList) mb.findForm(IDField.getText());

                if (printData != null) {

                    LocalDate today = LocalDate.now();
                    LocalDate bdate = LocalDate.parse(printData.get(4).toString());
                    age_years = LocalDate.now().getYear()-bdate.getYear();
                    
                    if(today.getMonth().getValue() >= bdate.getMonth().getValue()){
                        age_months =  today.getMonth().getValue() - bdate.getMonth().getValue();
                    }
                    else{
                        age_months =  12 + today.getMonth().getValue() - bdate.getMonth().getValue();
                        age_years -= 1;
                    }
    
                    printData.add(4, bdate);
                    printData.remove(5);
                    printData.add(age_years);
                    printData.add(age_months);

                    primaryStage.setUserData(printData);
                    m.showTranscriptDialog(scene, printData, printButton, editButton, mb);

                } else {
                    Label NotFound = new Label("Form Not Found");
                    grid.add(NotFound, 0, 2);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        editButton.setOnAction(event -> {

            try {

                ArrayList printData = (ArrayList) primaryStage.getUserData();
                GridPane editSection = new GridPane();
                editSection.setHgap(10);
                editSection.setVgap(10);
                editSection.setAlignment(Pos.CENTER);

                Label Class = new Label("Boagie Class");
                Label Duration = new Label("Season Duration");
                Label Departure = new Label("Departure Station");
                Label Destination = new Label("Destination Station");

                ClassValue.setText((String) printData.get(6));
                DurationValue.setText((String) printData.get(5));
                DepartureValue.setText((String) printData.get(7));
                DestinationValue.setText((String) printData.get(8));

                editSection.add(Class, 0, 0);
                editSection.add(ClassValue, 0, 1);
                editSection.add(Duration, 1, 0);
                editSection.add(DurationValue, 1, 1);
                editSection.add(Departure, 0, 2);
                editSection.add(DepartureValue, 0, 3);
                editSection.add(Destination, 1, 2);
                editSection.add(DestinationValue, 1, 3);
                editSection.add(confirmEdit, 0, 4);

                scene.setRoot(editSection);

            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        confirmEdit.setOnAction(event -> {

            try {

                ArrayList printData = (ArrayList) primaryStage.getUserData();

                mb.editForm((Integer) printData.get(2), ClassValue.getText(), DurationValue.getText(),
                        DepartureValue.getText(), DestinationValue.getText());

                printData = mb.findForm(printData.get(12).toString());
                printData.add(age_years);
                printData.add(age_months);
                primaryStage.setUserData(printData);

                m.showTranscriptDialog(scene, printData, printButton, editButton, mb);

            } catch (Exception e) {
                e.printStackTrace();
            }

        });

        printButton.setOnAction(event -> {

            ArrayList printData = (ArrayList) primaryStage.getUserData();
            System.out.println(printData);

            for (int i = 0; i < 4; i++) {
                printData.remove(5);
            }

            GridPane gp = new GridPane();
            Label thankyou = new Label("Thank You for giving us a chance to serve you!");
            Label error = new Label(
                    "Some Unexpected Error Occured \n Sorry For Your Inconvenience \n Please Try Again Later.");
            gp.setVgap(10);
            gp.setHgap(10);
            gp.setAlignment(Pos.CENTER);

            boolean print = mb.printform(printData);

            if (print)
                gp.add(thankyou, 0, 0);
            else
                gp.add(error, 0, 0);

            scene.setRoot(gp);
        });

    }

    public static void main(String[] args) {
        launch(args);
    }

}