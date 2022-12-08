package sample;

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
    public void setText_show(String msg){
        Task<HBox> othersMessages = new Task<HBox>() {
            @Override
            public HBox call() throws Exception {
                BubbledLabel bl6 = new BubbledLabel();
                bl6.setText(msg);

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
                bl6.setText(msg);

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
        String[] name = msg.split(" ",2);

        System.out.println(" hh "+name[1].split(":",2)[0]+" hh "+LoginController.MyClient.getUsername());
        if (name[1].split(":",2)[0].equals(LoginController.MyClient.getUsername())) {
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
         Client client = LoginController.MyClient;
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
                    String[] tempMsg = msg.split(" ",2);
                    msg = "@"+client.getUsername()+" "+tempMsg[1];
                    client.sendMessage(new ChatMessage(ChatMessage.MESSAGE, msg));
                    System.out.println("from me"+msg);
                }
            }
        });
    }
}
