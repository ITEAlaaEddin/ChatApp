package sample;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;


public class Controller {
    @FXML
    public Button button_send;
    @FXML
    public TextArea text_send;
    @FXML
    public ListView list_show;
    @FXML ListView list_users;

    Client client;

    public void setUserList(String msg) {

        /*
        Platform.runLater(() -> {
            ObservableList<String> users = FXCollections.observableList();
            list_users.setItems(users);


        });*/

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
        yourMessages.setOnSucceeded(event -> list_show.getItems().add(yourMessages.getValue()));



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
                    ChatMessage chatMessage = new ChatMessage(ChatMessage.MESSAGE, msg ,client.getUsername(),"ismail");
                    client.sendMessage(chatMessage);
                    chatMessage.FromOther = false;
                    setText_show(chatMessage);

                }
            }
        });
    }
}
