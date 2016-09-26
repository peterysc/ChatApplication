package defaultPackage;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ChatServer {

	static ArrayList<String> userNames = new ArrayList<String>();
	// printWriter is required to send the message to all the clients
	static ArrayList<PrintWriter> printWriters = new ArrayList<PrintWriter>();
	
	public static void main(String[] args) throws Exception{
		
		// flow 1 - basic setup
		System.out.println("Waiting for clients...");
		ServerSocket ss = new ServerSocket(9806);
		
		// for incoming client connections
		while(true){
			// accept() returns a socket object
			Socket soc = ss.accept();
			System.out.println("Connection established");
			
			// flow 2 - create conversation handler once the connect is complete and start the thread
			ConversationHandler handler = new ConversationHandler(soc);
			handler.start();
			
		}
		
	}
}

class ConversationHandler extends Thread{
	
	// this socket is for conversation handler
	Socket socket;
	BufferedReader in;
	// useful for writing it to the socket's outputstream
	PrintWriter out;
	String name;
	// will be used for writing data to our file
	PrintWriter pw;
	static FileWriter fw;
	static BufferedWriter bw;
	
	public ConversationHandler(Socket socket) throws IOException{
		this.socket = socket;
		// true is for not clearing the current content of the file every time the file is accessed
		fw = new FileWriter("chatLogs/chatLog.txt",true);
		// in order to write an entire string at time, we utilize BufferedWriter
		bw = new BufferedWriter(fw);
		// writes the data to our file
		// true is for auto-flush
		pw = new PrintWriter(bw,true);
	}
	
	public void run(){
		try{
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream(),true);
			// flow 5 - waits for the user until the user enters a unique name
			int count = 0;
			
			while(true){
				if(count>0){
					out.println("NAMEALREADYEXISTS");
				}else{
					out.println("NAMEREQUIRED");
				}
				
				// captures the name input from a client in a name variable
				// readLine : reads a line of text; a line is considered to be terminated by
				// any one of a line feed ('\n'), a carriage return ('/r') or a carriage return
				// followed immediately by a linefeed
				// returns a string containing the contents of the line, not including any
				// line-termination chars or null if the end of the stream has been reached
				name = in.readLine();
				
				if(name == null){
					return;
				}
				
				// add the name to the list of username if it is not there already
				if(!ChatServer.userNames.contains(name)){
					ChatServer.userNames.add(name);
					break;
				}
				
				count++;
			}
			
			out.println("NAMEACCEPTED"+ name);
			ChatServer.printWriters.add(out);
			
			// read message from a client and send it to ALL the clients
			while(true){
				String message = in.readLine();
				
				if(message == null){
					return;
				}
				
				// sending the message to the chat-log file along with the user's name
				pw.println(name + ":" + message);
				for(PrintWriter writer : ChatServer.printWriters){
					writer.println(name + ": " + message);
				}
			}
			
		}catch(Exception e){
			
		}
	}
	
}
