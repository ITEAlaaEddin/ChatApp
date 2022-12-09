package sample;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {
    @FXML
    public Button button_login;
    @FXML
    public TextField text_userName;
    @FXML
    public TextField text_ip;
    @FXML
    public TextField text_port;
    @FXML
    public TextField text_password;

    public static Client MyClient;

    public void initialize(){

        button_login.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                MyClient = new Client(text_ip.getText(), Integer.parseInt(text_port.getText()), text_userName.getText());
                Main m = new Main();
                Main.ControllerName="Controller";
                try {
                    m.changeScene("sample.fxml");
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        });
    }
}
