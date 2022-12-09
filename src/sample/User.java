package sample;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.ListView;

import java.awt.*;
import java.util.ArrayList;

import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import sun.security.jca.GetInstance;

public class User {

    public Button Btn;
    public ArrayList<ChatMessage> Messages;
    public String UserName;
    public Controller MyController = Main.Controller;

    public User(String userName) {
        Messages = new ArrayList<ChatMessage>();
        Btn = new Button(userName);
        UserName = userName;

        Btn.setOnAction(new EventHandler() {

            @Override
            public void handle(Event event) {
                //MyController.list_show=Messages;
                Btn.setTextFill(Color.BLACK);
                MyController.list_show.getItems().clear();
                Controller.ReciverUserName=UserName;
                for(ChatMessage message:Messages){
                    MyController.setText_show(message);
                }

            }

        });


    }
}
