package QuequeApp;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

import Server.Server;

public class ClientSender {

	    
	    private Socket socket;
	    private ObjectOutputStream toServer;
	    
	    public Socket connectToServer() {
	        try {
	            this.socket = new Socket("localhost", Server.PORT);
	            this.toServer = new ObjectOutputStream(socket.getOutputStream());
	        
	        } catch (IOException ex) {
	        	ex.printStackTrace();
	        }
	        
	        return socket;
	    }
	    public void disconnectFromServer() throws IOException{
//	    	toServer.writeObject(m);
//	    	toServer.flush();
	    	socket.close();
	    	System.exit(0);
	    }
	    
	    public  void Send(Message message) throws IOException{
			toServer.writeObject(message);
			toServer.flush();
		}
	    
}
