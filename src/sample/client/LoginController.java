package sample.client;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

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
                loginAction();

            }
        });

        button_login.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode().equals(KeyCode.ENTER)) {
                    loginAction();
                }


            }
        });

    }

    public void loginAction(){
        MyClient = new Client(text_ip.getText(), Integer.parseInt(text_port.getText()), text_userName.getText(),text_password.getText());

        //MyClient = LoginController.MyClient;
        //client.getUsers();
        if(!MyClient.start())
            return;
        MyClient.confirmLogin(text_password.getText());
        //MyClient.sendMessage(new ChatMessage(ChatMessage.CheckLogin,MyClient.getUsername(),text_password.getText()));
    }

    public void changeScene(){
        Main m = new Main();
        Main.ControllerName="Controller";
        try {
            m.changeScene("Controller.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
