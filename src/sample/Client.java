package sample;

import javafx.scene.paint.Color;

import java.awt.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;


//The Client that can be run as a console
public class Client  {

	// notification
	private String notif = " *** ";

	// for I/O
	private ObjectInputStream sInput;		// to read from the socket
	private ObjectOutputStream sOutput;		// to write on the socket
	private Socket socket;					// socket object

	private String server, username;	// server and username
	private int port;					//port



	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	/*
	 *  Constructor to set below things
	 *  server: the server address
	 *  port: the port number
	 *  username: the username
	 */

	Client(String server, int port, String username) {
		this.server = server;
		this.port = port;
		this.username = username;
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
			display("Error connectiong to server:" + ec);
			return false;
		}

		String msg = "Connection accepted " + socket.getInetAddress() + ":" + socket.getPort();
		display(msg);

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

	/*public void getUsers(){
		sendMessage(new ChatMessage(ChatMessage.WHOISIN, ""));
		try {
			Thread thread = new ListenFromServer();
			@Override
			public void run() {}
			thread.start();
			String msg = (String) thread.sInput.readObject();
			System.out.println("hh "+msg);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}*/

	/*
	 * To send a message to the server
	 */
	void sendMessage(ChatMessage msg) {
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
			if(sInput != null) sInput.close();
		}
		catch(Exception e) {}
		try {
			if(sOutput != null) sOutput.close();
		}
		catch(Exception e) {}
        try{
			if(socket != null) socket.close();
		}
		catch(Exception e) {}

	}
	/*
	 * To start the Client in console mode use one of the following command
	 * > java Client
	 * > java Client username
	 * > java Client username portNumber
	 * > java Client username portNumber serverAddress
	 * at the console prompt
	 * If the portNumber is not specified 1500 is used
	 * If the serverAddress is not specified "localHost" is used
	 * If the username is not specified "Anonymous" is used
	 */




	/*
	 * a class that waits for the message from the server
	 */
	class ListenFromServer extends Thread  {

		public void run() {
			while(true) {
				try {
					// read the message form the input datastream
					ChatMessage chatMessage = (ChatMessage) sInput.readObject();
					Controller myController= Main.Controller;
					switch (chatMessage.getType()){
						case ChatMessage.MESSAGE:
							// print the message
							System.out.println(chatMessage.SenderUserName +": "+chatMessage.message);
							System.out.print("> ");
							if(chatMessage.SenderUserName.equals(Controller.ReciverUserName))
							    myController.setText_show(chatMessage);
							User ouser =null;
							for(User user:myController.Users){
								if(user.UserName.equals(chatMessage.SenderUserName)){
									ouser=user;
									break;
								}
							}
							if(ouser!=null) {
								ouser.Messages.add(chatMessage);
								ouser.Btn.setTextFill(Color.RED);
							}
							break;
						case ChatMessage.WHOISIN:
							myController.setUserList(chatMessage.WhoIsInUsers);
							break;
						case  ChatMessage.IsJoinedUserName:
							myController.AddUserToUserList(chatMessage.JoinLeftUserName);
							System.out.println(chatMessage.JoinLeftUserName);
							break;
						case ChatMessage.IsLeftUserName:
							myController.removeUserFromUserList(chatMessage.JoinLeftUserName);
							System.out.println("second removed "+chatMessage.JoinLeftUserName);
							break;
						default:
							throw new IOException();

					}

				}
				catch(IOException e) {
					display(notif + "Server has closed the connection: " + e + notif);
					break;
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}

			}
		}
	}
}

