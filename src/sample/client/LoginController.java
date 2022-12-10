package sample.client;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;




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

    public void initialize() {


        button_login.setOnMouseReleased(event -> loginAction());

        button_login.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                loginAction();
            }
        });
    }

    public void loginAction(){
        MyClient = new Client(text_ip.getText(), Integer.parseInt(text_port.getText()), text_userName.getText(),text_password.getText());

        if(!MyClient.start())
            return;
        MyClient.checkLogin(text_password.getText());
    }

}
