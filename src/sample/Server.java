package sample;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import java.util.Scanner;
import java.util.Set;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;


// the server that can be run as a console
public class Server {

	// a unique ID for each connection
	private static int uniqueId;
	// an ArrayList to keep the list of the Client
	public ArrayList<ClientThread> ListOfClients;
	// the port number to listen for connection
	private int port;
	// to check if server is running
	private boolean keepGoing;
	private SimpleDateFormat sdf;
	Set<UserOfServer> Users;
	
	//constructor that receive the port to listen to for connection as parameter
	public Server(int port) {
		// the port
		this.port = port;
		// an ArrayList to keep the list of the Client
		ListOfClients = new ArrayList<ClientThread>();
		sdf = new SimpleDateFormat("HH:mm:ss");

	}
	
	public void start() {
		try {
			String data="";
			File myObj = new File("C:\\Users\\Alaa Alaa Eddin\\IdeaProjects\\NetworksProjectt\\src\\sample\\Users.json");
			Scanner myReader = new Scanner(myObj);
			while (myReader.hasNextLine()) {
				data += myReader.nextLine();
			}
			myReader.close();
			ObjectMapper mapper = new ObjectMapper();

			Set<UserOfServer> properties = fromJSON(new TypeReference<Set<UserOfServer>>() {}, data);
			Users=properties;
		} catch (FileNotFoundException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}


		keepGoing = true;
		//create socket server and wait for connection requests 
		try 
		{
			// the socket used by the server
			ServerSocket serverSocket = new ServerSocket(port);

			// infinite loop to wait for connections ( till server is active )
			while(keepGoing) 
			{
				display("Server waiting for Clients on port " + port + ".");
				
				// accept connection if requested from client
				Socket socket = serverSocket.accept();
				// break if server stoped
				if(!keepGoing)
					break;
				// if client is connected, create its thread
				ClientThread t = new ClientThread(socket);
				//add this client to arraylist
				ListOfClients.add(t);
				
				t.start();
			}
			// try to stop the server
			try {
				serverSocket.close();
				for(int i = 0; i < ListOfClients.size(); ++i) {
					ClientThread tc = ListOfClients.get(i);
					try {
					// close all data streams and socket
					tc.sInput.close();
					tc.sOutput.close();
					tc.socket.close();
					}
					catch(IOException ioE) {
					}
				}
			}
			catch(Exception e) {
				display("Exception closing the server and clients: " + e);
			}
		}
		catch (IOException e) {
            String msg = sdf.format(new Date()) + " Exception on new ServerSocket: " + e + "\n";
			display(msg);
		}
	}
	
	// to stop the server
	protected void stop() {
		keepGoing = false;
		try {
			new Socket("localhost", port);
		}
		catch(Exception e) {
		}
	}
	
	// Display an event to the console
	private void display(String msg) {
		String time = sdf.format(new Date()) + " " + msg;
		System.out.println(time);
	}

	private boolean checkLogin(ChatMessage chatMessage){

	//	return chatMessage.Password.equals("alaa")&& chatMessage.SenderUserName.equals("alaa");

		boolean isExist = false;
		UserOfServer userr=null;
		for(UserOfServer user:Users){

			if(user.getUserName().equals(chatMessage.SenderUserName) && user.getPassword().equals(chatMessage.Password)){
				userr=user;
				isExist=true;
				break;
			}
		}


		if(userr!=null){
			int i=0;
			for(ClientThread client:ListOfClients){

				if(client.username.equals(userr.getUserName())){
					i++;

				}

			}
			if(i>1){
				isExist=false;

			}

		}

		System.out.println(isExist);
		return isExist;

	}

	public static <T> T fromJSON(final TypeReference<T> type,
								 final String jsonPacket) {
		T data = null;

		try {
			data = new ObjectMapper().readValue(jsonPacket, type);
		} catch (Exception e) {
			// Handle the problem
			System.out.println("oo "+e);
		}
		return data;
	}
	
	// to broadcast a message to all Clients
	private synchronized boolean broadcast(ChatMessage chatMessage) {

		boolean found=false;
		boolean isPrivate = true;

		if(chatMessage.ReceiverUserName.equalsIgnoreCase("brodcast"))
			isPrivate=false;

		if(isPrivate){

			// we loop in reverse order to find the mentioned username
			for(ClientThread ct1 : ListOfClients)
			{
				if(ct1.getUsername().equals(chatMessage.ReceiverUserName))
				{
					// try to write to the Client if it fails remove it from the list

					if(!ct1.writeMsg(chatMessage)) {
						ListOfClients.remove(ct1);
						display("Disconnected Client " + ct1.username + " removed from list.");
					}
					// username found and delivered the message
					found=true;
					break;
				}
			}
			// mentioned user not found, return false
			if(found!=true)
			{
				return false;
			}

		}


		// if message is a broadcast message
		else
		{
			// we loop in reverse order in case we would have to remove a Client
			// because it has disconnected
			for(ClientThread ct : ListOfClients) {
				// try to write to the Client if it fails remove it from the list
				//System.out.println("send from brodcast success "+chatMessage.JoinLeftUserName);
				if(!ct.writeMsg(chatMessage)) {
					ListOfClients.remove(ct);
					display("Disconnected Client " + ct.username + " removed from list.");
				}
			}
		}
		return true;

	}

	// if client sent LOGOUT message to exit
	synchronized void remove(int id) {
		
		String disconnectedClient = "";
		// scan the array list until we found the Id
		for(ClientThread ct:ListOfClients) {
			// if found remove it
			if(ct.id == id) {
				disconnectedClient = ct.getUsername();
				ListOfClients.remove(ct);
				break;
			}
		}
		ChatMessage left = new ChatMessage(ChatMessage.IsLeftUserName,disconnectedClient);
		left.ReceiverUserName= "brodcast";
		broadcast(left);
	}
	
	/*
	 *  To run as a console application
	 * > java Server
	 * > java Server portNumber
	 * If the port number is not specified 1500 is used
	 */ 
	public static void main(String[] args) {
		// start server on port 1500 unless a PortNumber is specified 
		int portNumber = 1500;
		switch(args.length) {
			case 1:
				try {
					portNumber = Integer.parseInt(args[0]);
				}
				catch(Exception e) {
					System.out.println("Invalid port number.");
					System.out.println("Usage is: > java Server [portNumber]");
					return;
				}
			case 0:
				break;
			default:
				System.out.println("Usage is: > java Server [portNumber]");
				return;
				
		}
		// create a server object and start it
		Server server = new Server(portNumber);
		server.start();
	}

	// One instance of this thread will run for each client
	class ClientThread extends Thread {
		// the socket to get messages from client
		Socket socket;
		ObjectInputStream sInput;
		ObjectOutputStream sOutput;
		// my unique id (easier for deconnection)
		int id;
		// the Username of the Client
		String username;
		// message object to recieve message and its type
		ChatMessage chatMessage;

		// Constructor
		ClientThread(Socket socket) {
			// a unique id
			id = ++uniqueId;
			this.socket = socket;
			//Creating both Data Stream
			System.out.println("Thread trying to create Object Input/Output Streams");
			try
			{
				sOutput = new ObjectOutputStream(socket.getOutputStream());
				sInput  = new ObjectInputStream(socket.getInputStream());
				// read the username
				ChatMessage chatMessage= (ChatMessage) sInput.readObject();
				username = chatMessage.SenderUserName;
				ChatMessage join = new ChatMessage(ChatMessage.IsJoinedUserName,username);
				join.ReceiverUserName = "brodcast";
				broadcast(join);
			}
			catch (IOException e) {
				display("Exception creating new Input/output Streams: " + e);
				return;
			}
			catch (ClassNotFoundException e) {
			}
		}
		
		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		// infinite loop to read and forward message
		public void run() {
			// to loop until LOGOUT
			boolean keepGoing = true;
			while(keepGoing) {
				// read a String (which is an object)
				try {
					chatMessage = (ChatMessage) sInput.readObject();
				}
				catch (IOException e) {
					display(username + " Exception reading Streams: " + e);
					break;				
				}
				catch(ClassNotFoundException e2) {
					break;
				}
				// get the message from the ChatMessage object received
				String message = chatMessage.getMessage();

				// different actions based on type message
				switch(chatMessage.getType()) {

				case ChatMessage.MESSAGE:
					boolean confirmation =  broadcast(chatMessage);
					if(confirmation==false){
						System.out.println( "***" + "User is trying to send message to offline user" + "***");
					}
					break;
				case ChatMessage.LOGOUT:
					display(username + " disconnected with a LOGOUT message.");
					keepGoing = false;
					break;
				case ChatMessage.WHOISIN:
					ChatMessage chatMessage2 = new ChatMessage(ChatMessage.WHOISIN,"");
					for(ClientThread client:ListOfClients){
						if(client.id==this.id)
							continue;
						chatMessage2.WhoIsInUsers.add(client.username);
					}
					writeMsg(chatMessage2);
					System.out.println(chatMessage2.WhoIsInUsers);
					break;
				case ChatMessage.CheckLogin:
						if(checkLogin(chatMessage)){
							ChatMessage chatMessage1 = new ChatMessage(ChatMessage.CheckLogin, chatMessage.SenderUserName, chatMessage.Password);
							chatMessage1.ReceiverUserName = chatMessage.SenderUserName;
							broadcast(chatMessage1);

						}
						else
							close();
						break;







				}
			}
			// if out of the loop then disconnected and remove from client list
			remove(id);
			close();
		}
		
		// close everything
		private void close() {
			try {
				if(sOutput != null) sOutput.close();
			}
			catch(Exception e) {}
			try {
				if(sInput != null) sInput.close();
			}
			catch(Exception e) {};
			try {
				if(socket != null) socket.close();
			}
			catch (Exception e) {}
		}

		// write a String to the Client output stream
		public boolean writeMsg(ChatMessage chatMessage) {
			// if Client is still connected send the message to it
			if(!socket.isConnected()) {
				close();
				return false;
			}
			// write the message to the stream
			try {
				sOutput.writeObject(chatMessage);
			}
			// if an error occurs, do not abort just inform the user
			catch(IOException e) {
				display("***" + "Error sending message to " + username + "***");
				display(e.toString());
			}
			return true;
		}
	}
}

