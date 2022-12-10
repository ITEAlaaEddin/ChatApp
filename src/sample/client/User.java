package sample.client;

import javafx.event.Event;
import javafx.event.EventHandler;

import java.util.ArrayList;

import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import sample.ChatMessage;


public class User {

    public Button Btn;
    public ArrayList<ChatMessage> Messages;
    public String UserName;
    public ChatController MyController = Main.Controller;

    public User(String userName) {
        Messages = new ArrayList<ChatMessage>();
        Btn = new Button(userName);
        UserName = userName;
        Btn.setStyle("-fx-border-color: #ffffff; -fx-border-width: 2px;");
        Btn.setStyle("-fx-background-color: #000000; -fx-background-radius: 40");

        Btn.setMinWidth(80);
        Btn.setMaxWidth(80);
        Btn.setTextFill(Color.WHITE);
        DropShadow shadow = new DropShadow();
        Btn.setOnMousePressed(new EventHandler() {
            @Override
            public void handle(Event event) {
                shadow.setRadius(10);
                shadow.setColor(Color.BLUE);
                Btn.setEffect(shadow);
                //MyController.list_show=Messages;
                Btn.setTextFill(Color.WHITE);
                MyController.list_show.getItems().clear();
                ChatController.ReciverUserName=UserName;
                for(ChatMessage message:Messages){
                    MyController.setText_show(message);
                }

            }

        });
        Btn.setOnMouseReleased(new EventHandler(){
            @Override
            public void handle(Event event) {
                Btn.setEffect(null);
                Main.Controller.label_user.setText(userName);

            }
        });


    }
}