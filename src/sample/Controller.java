package sample;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;


public class Controller {
    @FXML
    public Button button_send;
    @FXML
    public TextField text_send;
    @FXML
    public TextArea text_show;
    public void setText_show(String s){
        text_show.setText(s);
    }
    public void initialize(){
         Client client = LoginController.MyClient;
         if(!client.start())
             return;
        button_send.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

                System.out.print("> ");
                // read message from user
                String msg = text_send.getText();
                // logout if message is LOGOUT
                if(msg.equalsIgnoreCase("LOGOUT")) {
                    client.sendMessage(new ChatMessage(ChatMessage.LOGOUT, ""));


                    // client completed its job. disconnect client.
                    client.disconnect();

                }

                // message to check who are present in chatroom
                else if(msg.equalsIgnoreCase("WHOISIN")) {
                    client.sendMessage(new ChatMessage(ChatMessage.WHOISIN, ""));
                }
                // regular text message
                else {
                    client.sendMessage(new ChatMessage(ChatMessage.MESSAGE, msg));
                }
            }
        });
    }
}
