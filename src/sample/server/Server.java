package sample.server;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import sample.ChatMessage;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import java.util.Set;


// the server that can be run as a console
public class Server {

	// a unique ID for each connection
	private static int uniqueId;
	// an ArrayList to keep the list of the Client
	public ArrayList<ClientThread> ListOfClients;
	// the port number to listen for connection
	private final int port;


	private final SimpleDateFormat sdf;
	Set<UserOfServer> Users;

	//constructor that receive the port to listen to for connection as parameter
	public Server(int port) {
		// the port
		this.port = port;
		// an ArrayList to keep the list of the Client
		ListOfClients = new ArrayList<>();
		sdf = new SimpleDateFormat("HH:mm:ss");

	}

	public static <T> T fromJSON(final TypeReference<T> type, final String jsonPacket) {
		T data = null;

		try {
			data = new ObjectMapper().readValue(jsonPacket, type);
		} catch (Exception e) {
			// Handle the problem
			System.out.println("fromJSON" + e);
		}
		return data;
	}

	// to stop the server

	/*
	 *  To run as a console application
	 * > java Server
	 * > java Server portNumber
	 * If the port number is not specified 1500 is used
	 */
	public static void main(String[] args) {
		// start server on port 1500 unless a PortNumber is specified
		int portNumber = 1500;

		// create a server object and start it
		Server server = new Server(portNumber);
		server.start();
	}

	// Display an event to the console
	private void display(String msg) {
		String time = sdf.format(new Date()) + " " + msg;
		System.out.println(time);
	}

	public void start() {


		readDefultUsers();


		//create socket server and wait for connection requests
		try {
			// the socket used by the server
			ServerSocket serverSocket = new ServerSocket(port);

			// infinite loop to wait for connections ( till server is active )
			while (true) {
				display("Server waiting for Clients on port " + port + ".");

				// accept connection if requested from client
				Socket socket = serverSocket.accept();

				// if client is connected, create its thread
				ClientThread t = new ClientThread(socket);
				//add this client to arraylist
				ListOfClients.add(t);

				t.start();
			}
			// try to stop the server
		}
		catch (IOException e) {
            String msg = sdf.format(new Date()) + " Exception on new ServerSocket: " + e + "\n";
			display(msg);
		}
	}

	private boolean checkLogin(ChatMessage chatMessage){


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
			if (i > 1) {
				isExist = false;

			}

		}

		return isExist;

	}

	private void readDefultUsers() {
		try {
			StringBuilder data = new StringBuilder();
			File myObj = new File("C:\\Users\\Alaa Alaa Eddin\\IdeaProjects\\NetworksProjectt\\src\\sample\\server\\Users.json");
			Scanner myReader = new Scanner(myObj);
			while (myReader.hasNextLine()) {
				data.append(myReader.nextLine());
			}
			myReader.close();

			Set<UserOfServer> properties = fromJSON(new TypeReference<Set<UserOfServer>>() {
			}, data.toString());
			Users = properties;
		} catch (FileNotFoundException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
	}

	// if client sent LOGOUT message to exit
	synchronized void remove(int id) {

		String disconnectedClient = "";
		// scan the array list until we found the Id
		for (ClientThread ct : ListOfClients) {
			// if found remove it
			if (ct.id == id) {
				disconnectedClient = ct.getUsername();
				ListOfClients.remove(ct);
				break;
			}
		}
		ChatMessage left = new ChatMessage(ChatMessage.IsLeftUserName, disconnectedClient);
		left.ReceiverUserName = "brodcast";
		broadcast(left);
	}

	// to broadcast a message to all Clients
	private synchronized boolean broadcast(ChatMessage chatMessage) {

		boolean found = false;

		if (!chatMessage.ReceiverUserName.equalsIgnoreCase("brodcast")) {

			// we loop in reverse order to find the mentioned username
			for (ClientThread ct1 : ListOfClients) {
				if (ct1.getUsername().equals(chatMessage.ReceiverUserName)) {
					// try to write to the Client if it fails remove it from the list

					if (!ct1.writeMsg(chatMessage)) {
						ListOfClients.remove(ct1);
						display("Disconnected Client " + ct1.username + " removed from list.");
					}
					// username found and delivered the message
					found = true;
					break;
				}
			}
			// mentioned user not found, return false
			return !found;


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
			try {
				sOutput = new ObjectOutputStream(socket.getOutputStream());
				sInput = new ObjectInputStream(socket.getInputStream());
				// read the username
				ChatMessage chatMessage = (ChatMessage) sInput.readObject();
				username = chatMessage.SenderUserName;
				ChatMessage join = new ChatMessage(ChatMessage.IsJoinedUserName, username);
				join.ReceiverUserName = "brodcast";
				broadcast(join);
				System.out.println(username + " is joined");
			}
			catch (IOException e) {
				display("Exception creating new Input/output Streams: " + e);
			}
			catch (ClassNotFoundException e) {
				System.out.println(e.toString());
			}
		}
		
		public String getUsername() {
			return username;
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


				// different actions based on type message
				switch(chatMessage.getType()) {

					case ChatMessage.MESSAGE:
						boolean confirmation = broadcast(chatMessage);
						if (!confirmation) {
							System.out.println("***" + "User is trying to send message to offline user" + "***");
						}
						break;
					case ChatMessage.LOGOUT:
						display(username + " disconnected with a LOGOUT message.");
						keepGoing = false;
						break;
					case ChatMessage.WHOISIN:
						ArrayList<String> onlineUsers = new ArrayList<>();
						for (ClientThread client : ListOfClients) {
							if (client.id == this.id)
								continue;
							onlineUsers.add(client.username);
						}
						writeMsg(new ChatMessage(ChatMessage.WHOISIN, onlineUsers));
						System.out.println(onlineUsers);
						break;
					case ChatMessage.CheckLogin:
						if (checkLogin(chatMessage)) {
							ChatMessage chatMessage1 = new ChatMessage(ChatMessage.CheckLogin);
							chatMessage1.ReceiverUserName = chatMessage.SenderUserName;
							broadcast(chatMessage1);

						} else
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
				if (sOutput != null) sOutput.close();
				if (sInput != null) sInput.close();
				if (socket != null) socket.close();
			} catch (Exception e) {
				System.out.println(e.toString());
			}

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

