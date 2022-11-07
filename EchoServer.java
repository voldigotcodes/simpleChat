// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

import java.io.IOException;

import common.ChatIF;
import ocsf.server.*;

/**
 * This class overrides some of the methods in the abstract 
 * superclass in order to give more functionality to the server.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;re
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Paul Holden
 * @version July 2000
 */
public class EchoServer extends AbstractServer 
{
  //Class variables *************************************************
 
  /**
   * The interface type variable.  It allows the implementation of 
   * the display method in the server.
   */
  ChatIF serverUI;
  boolean loggedIn;
  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the echo server.
   *
   * @param port The port number to connect on.
   */
  public EchoServer(int port, ChatIF serverUI) 
  {
    super(port);
    this.serverUI = serverUI;
    loggedIn = false;
  }

  
  //Instance methods ************************************************
  
  /**
   * This method handles any messages received from the client.
   *
   * @param msg The message received from the client.
   * @param client The connection from which the message originated.
   */
  public void handleMessageFromClient
    (Object msg, ConnectionToClient client)
  {
	  if(msg.toString().startsWith("#login")) {
			  client.setInfo("loginID", msg.toString().substring(7));
			  serverUI.display("Message received: \"" + msg + "\" from null");
			  this.sendToAllClients(client.getInfo("loginID")+" Has logged on");
 
	  }else {
		  serverUI.display("Message received: \"" + msg + "\" from " + client.getInfo("loginID"));
		  this.sendToAllClients(msg);
	  }
    
  }
  
  /**
   * This method handles all data coming from the UI            
   *
   * @param message The message from the UI.    
   */
  public void handleMessageFromServerUI(String message)
  {
    if(message.startsWith("#")) {
		handleCommands(message);
	}else {
		this.sendToAllClients("SERVER MSG> " + message);
	}
  }
  
  /**
   * This method handles all commands coming from the UI            
   *
   * @param message The message from the UI.    
   */
  private void handleCommands(String cmd) {
	  if(cmd.startsWith("#setport")){
		  if(!this.isListening()) {
			  setPort(Integer.parseInt(cmd.substring(9)));
		  }else {
			  serverUI.display("Please close the server before attempting to change the port");
		  }
		
		  
	  }
	  else {
		  switch(cmd) {
		  case "#quit":
			  System.exit(0);
			  break;
		  case "#stop":
			  if(isListening()) {
				  stopListening();
				  
			  }else {
				  serverUI.display("The server already stopped");
			  }
			  break;
		  case "#close":
			  try {
				this.close();
			} catch (IOException e) {
				serverUI.display("Couldn't close the server");
			}
			  break;
		  case "#start":
			  if(isListening()) {
				  serverUI.display("The server has already been started");
			  }else {
				  try {
					listen();
				} catch (IOException e) {
					serverUI.display("Couldn't start the server");
				}
			  }
			  break;
		  case "#getport":
			  serverUI.display(Integer.toString(getPort()));
			  break;
		  default:
			  serverUI.display("The command you typed does not exist");
			  break;
				  
		  }
	  }
	  
  }
  
    
  /**
   * This method overrides the one in the superclass.  Called
   * when the server starts listening for connections.
   */
  protected void serverStarted()
  {
    System.out.println
      ("Server listening for connections on port " + getPort());
  }
  
  /**
   * This method overrides the one in the superclass.  Called
   * when the server stops listening for connections.
   */
  protected void serverStopped()
  {
    System.out.println
      ("Server has stopped listening for connections.");
  }
  
  /**
   * Implementation of the hook method called each time a new client connection is
   * accepted. The default implementation does nothing.
   * @param client the connection connected to the client.
   */
  protected void clientConnected(ConnectionToClient client) {
		  System.out.println("Client : <<" + client.getInfo("loginID") + ">> established connection");
	  
  }

  /**
   * Implementation of the hook method called each time a client disconnects.
   * The default implementation does nothing. The method
   * may be overridden by subclasses but should remains synchronized.
   *
   * @param client the connection with the client.
   */
  synchronized protected void clientDisconnected(
    ConnectionToClient client) {
	  	System.out.println("Client <<" + client.getInfo("loginID") + ">> disconnected");
	  }

  /**
   * Implementation of the hook method called each time an exception is thrown in a
   * ConnectionToClient thread.
   * The method may be overridden by subclasses but should remains
   * synchronized.
   *
   * @param client the client that raised the exception.
   * @param Throwable the exception thrown.
   */
  synchronized protected void clientException(
    ConnectionToClient client, Throwable exception) {
	  System.out.println("Connection to client : "+client.getInfo("loginID")+" terminated");
  }
  
}
//End of EchoServer class
