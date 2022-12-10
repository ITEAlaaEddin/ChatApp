package sample;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
/*
 * This class defines the different type of messages that will be exchanged between the
 * Clients and the Server. 
 * When talking from a Java Client to a Java Server a lot easier to pass Java objects, no 
 * need to count bytes or to wait for a line feed at the end of the frame
 */

public class ChatMessage implements Serializable {

	// The different types of message sent by the Client
	// WHOISIN to receive the list of the users connected
	// MESSAGE an ordinary text message
	// LOGOUT to disconnect from the Server
	public static final int WHOISIN = 0, MESSAGE = 1, LOGOUT = 2, IsLeftUserName = 3,IsJoinedUserName=4,CheckLogin =5;
	private int type;
	public String message;
	public String SenderUserName = "",ReceiverUserName = "",JoinLeftUserName ="";
	public String Password;
	public ArrayList<String>  WhoIsInUsers = new ArrayList<String>();
	public SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
	public String Date;
	public boolean FromOther= true;
	// constructor
	ChatMessage(int type, String message, String SenderUserName , String ReceiverUserName) {
		this.type = type;
		this.message = message;
		this.ReceiverUserName=ReceiverUserName;
		this.SenderUserName=SenderUserName;
		this.Date = this.sdf.format(new Date());
	}

	ChatMessage(int type, String JoinLeftUserName) {
		this.type = type;
		this.JoinLeftUserName=JoinLeftUserName;
	}

	ChatMessage(int type, String userName, String password) {
		this.type = type;
		this.SenderUserName=userName;
		this.Password=password;
	}
	
	int getType() {
		return type;
	}

	String getMessage() {
		return message;
	}
}
