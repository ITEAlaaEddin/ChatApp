package sample.client;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;

public class Main extends Application {

    public static ChatController Controller;
    public static LoginController LoginController;
    public static String ControllerName;
    public static Stage stg;
    public static Scene scene;
    @Override
    public void start(Stage primaryStage) throws Exception{
        stg=primaryStage;
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent e) {

                System.exit(0);
            }
        });
        //Parent root = FXMLLoader.load(getClass().getResource("Login.fxml"));
        FXMLLoader fmxlLoader = new FXMLLoader(getClass().getResource("resources/Login.fxml"));
        Parent window = (Pane) fmxlLoader.load();
        //Controller = fmxlLoader.<Controller>getController();
        primaryStage.setTitle("AIH Chat App");
        primaryStage.setScene(new Scene(window, 820, 503));
        primaryStage.show();
    }

    public void changeScene(String fxml) throws IOException {
        FXMLLoader fmxlLoader = new FXMLLoader(getClass().getResource(fxml));
        Parent window = (Pane) fmxlLoader.load();
        if(ControllerName.equalsIgnoreCase("Controller"))
            Controller = fmxlLoader.<ChatController>getController();
        else
            LoginController = fmxlLoader.<LoginController>getController();
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
