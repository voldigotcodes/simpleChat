// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

package client;

import ocsf.client.*;
import common.*;
import java.io.*;

/**
 * This class overrides some of the methods defined in the abstract
 * superclass in order to give more functionality to the client.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;
 * @author Fran&ccedil;ois B&eacute;langer
 * @version July 2000
 */
public class ChatClient extends AbstractClient
{
  //Instance variables **********************************************
  
  /**
   * The interface type variable.  It allows the implementation of 
   * the display method in the client.
   */
  ChatIF clientUI; 
  String loginID;

  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the chat client.
   *
   * @param host The server to connect to.
   * @param port The port number to connect on.
   * @param clientUI The interface type variable.
   */
  
  public ChatClient(String loginID, String host, int port, ChatIF clientUI) 
    throws IOException 
  {
    super(host, port); //Call the superclass constructor
    this.clientUI = clientUI;
    this.loginID = loginID;
    if(loginID.isEmpty() || loginID == null) {
    	clientUI.display("ERROR - No login ID specified.  Connection aborted");
    	quit();
    }else {
    	openConnection();
    }
    
  }

  
  //Instance methods ************************************************
    
  /**
   * This method handles all data that comes in from the server.
   *
   * @param msg The message from the server.
   */
  public void handleMessageFromServer(Object msg) 
  {
    clientUI.display(msg.toString());
  }

  /**
   * This method handles all data coming from the UI            
   *
   * @param message The message from the UI.    
   */
  public void handleMessageFromClientUI(String message)
  {
    try
    {
    	if(message.startsWith("#")) {
    		handleCommands(message);
    	}else {
    		sendToServer(message);
    	}
    }
    catch(IOException e)
    {
      clientUI.display
        ("Could not send message to server.  Terminating client.");
      quit();
    }
  }
  
  private void handleCommands(String cmd) {
	  if(cmd.startsWith("#sethost")) {
		  if(!isConnected()) {
			  setHost(cmd.substring(9));
		  }else {
			  clientUI.display("You need to log off before attempting to change your host");
		  }
		  
	  }else if(cmd.startsWith("#setport")){
		  if(!isConnected()) {
			  setPort(Integer.parseInt(cmd.substring(9)));
		  }else {
			  clientUI.display("You need to log off before attempting to change your port");
		  }
	  }
	  else {
		  switch(cmd) {
		  case "#quit":
			  quit();
			  break;
		  case "#logoff":
			  try {
				  if(isConnected()) {
					  clientUI.display("You are about to log off");
					  closeConnection();
				  }else {
					  clientUI.display("The client has already been disconnected");
				  }
				
			} catch (IOException e) {
				clientUI.display("Couldn't close the connection");
			}
			  break;
		  case "#gethost":
			  clientUI.display(getHost());
			  break;
		  case "#getport":
			  clientUI.display(Integer.toString(getPort()));
			  break;
		  default:
			  clientUI.display("The command you typed does not exist");
			  break;
				  
		  }
	  }
	  
  }
  
  /**
   * This method terminates the client.
   */
  public void quit()
  {
    try
    {
      closeConnection();
    }
    catch(IOException e) {}
    System.exit(0);
  }
  
  /**
	 * This method is called after the connection has been closed. The method
	 * terminate the client after the connection to the server has been closed
	 */
	@Override
	public void connectionClosed() {
		  clientUI.display("Connection to server closed");
		  
	}
	
	/**
	 * implementation of the hook method called each time an exception is thrown by the client's
	 * thread that is waiting for messages from the server. The method may be
	 * overridden by subclasses.
	 * 
	 * @param exception
	 *            the exception raised.
	 */
	protected void connectionException(Exception exception) {
		clientUI.display("Connection to server lost");
		System.exit(0);
	}
	
	/**
	 * Hook method called after a connection has been established. The default
	 * implementation does nothing. It may be overridden by subclasses to do
	 * anything they wish.
	 */
	protected void connectionEstablished() {
		try {
			sendToServer("#login " + loginID);
		} catch (IOException e) {
			clientUI.display("somthing went wrong with your loginID");
			quit();
		}
	}
  
}
//End of ChatClient class
