package sample;


import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.util.ArrayList;


public class Controller {
    @FXML
    public Button button_send;
    @FXML
    public TextArea text_send;
    @FXML
    public ListView list_show;
    @FXML
    public ListView list_users;
    @FXML
    public Button button_logout;

    Client client;

   // public User user;
    public static String ReciverUserName;

    ArrayList<User> Users = new ArrayList<User>();

    public void setUserList(ArrayList<String> usersName) {

        for (String userName : usersName )
        {
            User user = new User(userName);
            Users.add(user);
            Platform.runLater(() -> {
                list_users.getItems().add(user.Btn);
            });

        }

    }

    public  void AddUserToUserList(String userName){
        User user = new User(userName);
        Users.add(user);
        Platform.runLater(() -> {
            list_users.getItems().add(user.Btn);
        });

    }

    public void removeUserFromUserList(String username){
        for(User user : Users){
            if(user.UserName.equals(username)){
                Platform.runLater(() -> {
                    list_users.getItems().remove(user.Btn);
                    System.out.println("removed !");
                });
                Users.remove(user);
                break;
            }
        }

    }
    public void setText_show(ChatMessage chatMessage){

        Task<HBox> othersMessages = new Task<HBox>() {
            @Override
            public HBox call() throws Exception {
                BubbledLabel bl6 = new BubbledLabel();
                bl6.setText(chatMessage.Date+" "+chatMessage.message);

                bl6.setBackground(new Background(new BackgroundFill(Color.WHITE,null, null)));
                HBox x = new HBox();
                bl6.setBubbleSpec(BubbleSpec.FACE_LEFT_CENTER);
                x.getChildren().addAll(bl6);

                return x;
            }
        };

        othersMessages.setOnSucceeded(event -> {
            list_show.getItems().add(othersMessages.getValue());
        });
       // ouser.Messages.getItems().add(othersMessages.getValue());

        Task<HBox> yourMessages = new Task<HBox>() {
            @Override
            public HBox call() throws Exception {
                BubbledLabel bl6 = new BubbledLabel();
                bl6.setText(chatMessage.message+" "+chatMessage.Date);

                bl6.setBackground(new Background(new BackgroundFill(Color.LIGHTGREEN,
                        null, null)));
                HBox x = new HBox();
                x.setMaxWidth(list_show.getWidth() - 20);
                x.setAlignment(Pos.TOP_RIGHT);
                bl6.setBubbleSpec(BubbleSpec.FACE_RIGHT_CENTER);
                x.getChildren().addAll(bl6);

                return x;
            }
        };
        yourMessages.setOnSucceeded(event ->{
            list_show.getItems().add(yourMessages.getValue());

                }
        );
        //ouser.Messages.getItems().add(othersMessages.getValue());



        if (!chatMessage.FromOther) {
            Thread t2 = new Thread(yourMessages);
            t2.setDaemon(true);
            t2.start();
        } else {
            Thread t = new Thread(othersMessages);
            t.setDaemon(true);
            t.start();
        }


    }
    public void initialize(){
        client = LoginController.MyClient;
        //client.getUsers();
         if(!client.start())
             return;
        client.sendMessage(new ChatMessage(ChatMessage.WHOISIN,""));
        HBox x = new HBox();

        button_send.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

                System.out.print("> ");
                // read message from user
                String msg = text_send.getText();
                // logout if message is LOGOUT
                if(msg.equalsIgnoreCase("LOGOUT")) {
                    client.sendMessage(new ChatMessage(ChatMessage.LOGOUT, client.getUsername()));
                    // client completed its job. disconnect client.
                    client.disconnect();

                }

                // message to check who are present in chatroom
                /*else if(msg.equalsIgnoreCase("WHOISIN")) {
                    client.sendMessage(new ChatMessage(ChatMessage.WHOISIN, ""));
                }*/
                // regular text message
                else {
                    User ouser =null;
                    for(User user:Users){
                        if(user.UserName.equals(ReciverUserName)){
                            ouser=user;
                            break;
                        }
                    }
                    if(ouser!=null) {
                        ChatMessage chatMessage = new ChatMessage(ChatMessage.MESSAGE, msg, client.getUsername(), ReciverUserName);
                        client.sendMessage(chatMessage);
                        chatMessage.FromOther = false;
                        ouser.Messages.add(chatMessage);
                        setText_show(chatMessage);
                    }
                    else
                        System.out.println("select user please");




                }
            }
        });

        button_logout.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                client.sendMessage(new ChatMessage(ChatMessage.LOGOUT,""));
                Main m = new Main();
                Main.ControllerName="LoginController";
                try {
                    m.changeScene("Login.fxml");
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
    }
}
