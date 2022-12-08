package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Scanner;

public class Main extends Application {

    public static Controller Controller;
    private static Stage stg;
    @Override
    public void start(Stage primaryStage) throws Exception{
        stg=primaryStage;
        //Parent root = FXMLLoader.load(getClass().getResource("Login.fxml"));
        FXMLLoader fmxlLoader = new FXMLLoader(getClass().getResource("Login.fxml"));
        Parent window = (Pane) fmxlLoader.load();
        //Controller = fmxlLoader.<Controller>getController();
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(window, 500, 500));
        primaryStage.show();
    }

    public void changeScene(String fxml) throws IOException {
        FXMLLoader fmxlLoader = new FXMLLoader(getClass().getResource(fxml));
        Parent window = (Pane) fmxlLoader.load();
        Controller = fmxlLoader.<Controller>getController();
        stg.getScene().setRoot(window);

    }


    public static void main(String[] args) {

        launch(args);

        // default values if not entered

        // create the Client object
        //Client client = new Client("localhost", 1500, "alaa");
        // try to connect to the server and return if not connected
        //if(!client.start())
         //   return;




    }
}
