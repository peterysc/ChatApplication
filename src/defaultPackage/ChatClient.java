package defaultPackage;

/*
 * REMINDER
 * 
 * Class BufferedReader: reads text from a char-input stream, buffering chars so as to provide for
 * the efficient reading of chars, arrays and lines 
 * BufferedReader reads a larger block at a time; typically much faster, especially for disk access and
 * larger data amounts
 * 
 * BE AWARE: BufferedReader is NOT SAME AS BufferedInputStream; BufferedReader reads chars (text) where
 * BufferedInputStream reads raw bytes
 * 
 * Class PrintWriter: Prints formatted representations of objects to a text-output stream.
 * This class enables you to write formatted data to an underlying Writer.
 * PrintWriter is useful if you are generating reports where you have to mix text and numbers.
 * The PrintWriter class has all the same methods as the PrintStream except for the methods to
 * write raw bytes. Being a Writer subclass the PrintWriter is intended to write text.
*/

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ChatClient {
	
	// flow 3 - setting up GUI for the chat application
	
	static JFrame chatWindow = new JFrame("Suh Dude");
	// input for JTextArea are # of rows and # of columns
	static JTextArea chatArea = new JTextArea(22,40);
	// input for JTextField is # of columns
	static JTextField textField = new JTextField(40);
	// will be used to have a blank line between chatArea and textField
	static JLabel blankLabel = new JLabel("             ");
	// button for sending messages
	static JButton sendButton = new JButton("Send");
	static BufferedReader in;
	static PrintWriter out;
	static JLabel nameLabel = new JLabel("             ");
	
	ChatClient(){
		//using FlowLayout for the chatWindow - arranges our components in some manner
		chatWindow.setLayout(new FlowLayout());
		
		chatWindow.add(nameLabel);
		// add a scroll pane in our chat area
		chatWindow.add(new JScrollPane(chatArea));
		chatWindow.add(blankLabel);
		chatWindow.add(textField);
		chatWindow.add(sendButton);
		// stop the application once a user click "x" button 
		chatWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// set the size of the window - width & height
		chatWindow.setSize(475, 500);
		// displays the application on the screen
		chatWindow.setVisible(true);
		
		// set false until the client is connected to the server
		textField.setEditable(false);
		// also set false for non-connected users
		chatArea.setEditable(false);
		
		// now when the user clicks the button, the message will be sent to the server
		sendButton.addActionListener(new Listner());
		// when the user presses enter, the message will be sent to the server
		textField.addActionListener(new Listner());

	}
	
	// flow 4 - main logic for client
	void startChat() throws Exception{
		// asking the user for the ip address through a dialog box
		//
		String ipAddress = JOptionPane.showInputDialog(
				// first input is the component which we need to display the dialog box
				chatWindow,
				// the message that is displayed in the dialog box
				"Enter IP Address:",
				// title of the dialog box
				"IP Address Required!",
				// type of the message
				JOptionPane.PLAIN_MESSAGE);
		
		Socket soc = new Socket(ipAddress, 9806);
		in = new BufferedReader(new InputStreamReader(soc.getInputStream()));
		out = new PrintWriter(soc.getOutputStream(),true);
		
		while(true){
			
			// flow 6 - read data received from the server
			String str = in.readLine();
			if(str.equals("NAMEREQUIRED")){
				String name = JOptionPane.showInputDialog(
						chatWindow,
						"Enter an unique name:",
						"Name Required!",
						JOptionPane.PLAIN_MESSAGE);
				
				out.println(name);
			}else if(str.equals("NAMEALREADYEXISTS")){
				String name = JOptionPane.showInputDialog(
						chatWindow,
						"Please enter a different name:",
						"Name Already Exists!",
						JOptionPane.WARNING_MESSAGE);
				
				out.println(name);
			}else if(str.startsWith("NAMEACCEPTED")){
				// user log in confirmed - now can send text
				textField.setEditable(true);
				// flow 7 - displays the name of the user on top of the chatbox
				nameLabel.setText("You are logged in as: " + str.substring(12));
				
			}else{
				// normal chat message recognition
				// adding the message to the chat area
				chatArea.append(str + "\n");
			}
		}
		
	}
	
	public static void main(String[] args) throws Exception{
		ChatClient client = new ChatClient();
		client.startChat();
		
	}	
}

class Listner implements ActionListener{

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		ChatClient.out.println(ChatClient.textField.getText());
		ChatClient.textField.setText("");
	}
	
}