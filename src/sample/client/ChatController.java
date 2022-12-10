package sample.client;


import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import sample.ChatMessage;
import sample.client.bubble.BubbleSpec;
import sample.client.bubble.BubbledLabel;

import java.io.IOException;
import java.util.ArrayList;


public class ChatController {

    @FXML
    public TextField text_send;
    @FXML
    public ListView list_show;
    @FXML
    public ListView list_users;
    @FXML
    public Button button_logout;
    @FXML
    public Label label_user;

    Client client;

   // public User user;
    public static String ReciverUserName;

    ArrayList<User> Users = new ArrayList<>();

    public void setUsersList(ArrayList<String> usersName) {

        for (String userName : usersName) {
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


    private void autoScrollMessageList() {

        if (list_show.getItems().size() > 14) { /*where size equals possible items to display*/
            list_show.scrollTo(list_show.getItems().size() - 1);
        }
    }

    public void setText_show(ChatMessage chatMessage){

        Task<HBox> othersMessages = new Task<HBox>() {
            @Override
            public HBox call() {
                BubbledLabel bl6 = new BubbledLabel();
                bl6.setText(chatMessage.Date + " " + chatMessage.Message);

                bl6.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, null, null)));
                HBox x = new HBox();
                bl6.setBubbleSpec(BubbleSpec.FACE_LEFT_CENTER);
                x.getChildren().addAll(bl6);

                return x;
            }
        };

        othersMessages.setOnSucceeded(event -> {
            list_show.getItems().add(othersMessages.getValue());
            autoScrollMessageList();
        });


        Task<HBox> yourMessages = new Task<HBox>() {
            @Override
            public HBox call() {
                BubbledLabel bl6 = new BubbledLabel();
                bl6.setText(chatMessage.Message + " " + chatMessage.Date);

                bl6.setBackground(new Background(new BackgroundFill(Color.LIGHTBLUE,
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
            autoScrollMessageList();

                }
        );


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


        label_user.setTextFill(Color.WHITE);
        button_logout.setTextFill(Color.WHITE);
        text_send.clear();
        client = LoginController.MyClient;

        client.sendMessage(new ChatMessage(ChatMessage.WHOISIN,""));


        text_send.addEventFilter(KeyEvent.KEY_PRESSED, ke -> {
            if (ke.getCode().equals(KeyCode.ENTER)) {

                if (!text_send.getText().isEmpty()) {

                    String msg = text_send.getText();

                    // regular text message
                    User ouser = null;
                    for (User user : Users) {
                        if (user.UserName.equals(ReciverUserName)) {
                            ouser = user;
                            break;
                        }
                    }
                    if (ouser != null) {
                        ChatMessage chatMessage = new ChatMessage(ChatMessage.MESSAGE, msg, client.getUsername(), ReciverUserName);
                        client.sendMessage(chatMessage);
                        chatMessage.FromOther = false;
                        ouser.Messages.add(chatMessage);
                        setText_show(chatMessage);
                    } else
                        System.out.println("select user please");

                    text_send.clear();

                }
            }
        });


        button_logout.setOnMouseReleased(event -> {
            client.sendMessage(new ChatMessage(ChatMessage.LOGOUT));
            Main m = new Main();
            Main.ControllerName = "LoginController";
            try {
                m.changeScene("resources/Login.fxml");
            } catch (IOException e) {
                e.printStackTrace();
            }

        });
    }
}
