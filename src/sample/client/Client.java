package sample.client;

import javafx.scene.paint.Color;
import sample.ChatMessage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;


//The Client that can be run as a console
public class Client {


	private final String server, username, Password;    // server and username
	private final int port;                    //port
	// for I/O
	private ObjectInputStream sInput;        // to read from the socket
	private ObjectOutputStream sOutput;        // to write on the socket
	private Socket socket;                    // socket object


	public String getUsername() {
		return username;
	}


	/*
	 *  Constructor to set below things
	 *  server: the server address
	 *  port: the port number
	 *  username: the username
	 */

	Client(String server, int port, String username, String password) {
		this.server = server;
		this.port = port;
		this.username = username;
		this.Password = password;
	}

	public void checkLogin(String password) {

		sendMessage(new ChatMessage(ChatMessage.CheckLogin, username, password));
	}

	/*
	 * To start the chat
	 */
	public boolean start() {
		// try to connect to the server
		try {
			socket = new Socket(server, port);
		}
		// exception handler if it failed
		catch(Exception ec) {
			display("Error connecting to server:" + ec);
			return false;
		}


		/* Creating both Data Stream */
		try
		{
			sInput  = new ObjectInputStream(socket.getInputStream());
			sOutput = new ObjectOutputStream(socket.getOutputStream());
		}
		catch (IOException eIO) {
			display("Exception creating new Input/output Streams: " + eIO);
			return false;
		}

		// creates the Thread to listen from the server
		new ListenFromServer().start();
		// Send our username to the server this is the only message that we
		// will send as a String. All other messages will be ChatMessage objects
		try
		{
			sOutput.writeObject(new ChatMessage(ChatMessage.MESSAGE,"",username,""));
		}
		catch (IOException eIO) {
			display("Exception doing login : " + eIO);
			disconnect();
			return false;
		}
		// success we inform the caller that it worked
		return true;
	}

	/*
	 * To send a message to the console
	 */
	private void display(String msg) {

		System.out.println(msg);

	}


	/*
	 * To send a message to the server
	 */
	public void sendMessage(ChatMessage msg) {
		try {
			sOutput.writeObject(msg);
		}
		catch(IOException e) {
			display("Exception writing to server: " + e);
		}
	}

	/*
	 * When something goes wrong
	 * Close the Input/Output streams and disconnect
	 */
	public void disconnect() {
		try {
			if (sInput != null) sInput.close();
			if (sOutput != null) sOutput.close();
			if (socket != null) socket.close();
		} catch (Exception e) {
			display(e.toString());
		}


	}


	/*
	 * a class that waits for the message from the server
	 */
	class ListenFromServer extends Thread  {

		public void run() {
			while(true) {
				try {
					// read the message form the input datastream
					ChatMessage chatMessage = (ChatMessage) sInput.readObject();
					ChatController chatController = Main.ChatController;
					switch (chatMessage.getType()) {
						case ChatMessage.MESSAGE:
							// print the message
							if (chatMessage.SenderUserName.equals(ChatController.ReciverUserName))
								chatController.setText_show(chatMessage);
							//storing message
							User userPointer = null;
							for (User user : chatController.Users) {
								if (user.UserName.equals(chatMessage.SenderUserName)) {
									userPointer = user;
									break;
								}
							}
							if (userPointer != null) {
								userPointer.Messages.add(chatMessage);
								//dont light the button when iam in the same chat with user
								if (!chatMessage.SenderUserName.equals(ChatController.ReciverUserName)) {
									userPointer.Btn.setTextFill(Color.RED);
								}
							}
							break;

						case ChatMessage.WHOISIN:
							chatController.setUsersList(chatMessage.OnlineUsers);
							break;

						case ChatMessage.IsJoinedUserName:
							chatController.AddUserToUserList(chatMessage.JoinLeftUserName);
							break;

						case ChatMessage.IsLeftUserName:
							chatController.removeUserFromUserList(chatMessage.JoinLeftUserName);
							break;

						case ChatMessage.CheckLogin:
							Main m = new Main();
							Main.ControllerName = "ChatController";
							try {
								m.changeScene("resources/Chat.fxml");
							} catch (IOException e) {
								e.printStackTrace();
							}


					}

				}
				catch(IOException e) {
					display("Server has closed the connection: " + e);
					break;
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}

			}
		}
	}
}

