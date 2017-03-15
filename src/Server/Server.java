package Server;

import java.awt.BorderLayout;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

import javax.swing.JFrame;
import javax.swing.JTextArea;

import QuequeApp.GroupMessage;
import QuequeApp.Message;

public class Server {

	public static final int PORT= 8080; 
	private JFrame serverFrame;
	private JTextArea dataDisplay;
	private ArrayList<String> usersOnline = new ArrayList<String>();
	private Queue<Message> messageQueue;
	private HashMap<String,Queue<Message>> hashMap = new HashMap<String,Queue<Message>>();
	private Object usersListLock = new Object();
	private Object hashMapLock = new Object();
	private Socket clientSocket;
	public static String notification = "delivered";

	
	//Thread que trata das mensagens Recebidas
	public class messageReceiver extends Thread{
		private ObjectInputStream  reader;
		private Socket socket;
		private String user;

		public messageReceiver(Socket clientSocket) {


			try{
				socket = clientSocket;
				reader = new ObjectInputStream(socket.getInputStream());
				Message m = (Message) reader.readObject();
				user = m.getOwner();
				addUser(user);
				synchronized (hashMapLock) {
					if(!hashMap.containsKey(user)){
						System.out.println("Não existe vou criar uma Queue e põr no hashMap...");
						messageQueue = new LinkedList<Message>();
						hashMap.put(user, messageQueue);
					}
				}
				Thread out = new Thread(new messageSender(socket, user));
				out.start();


			}
			catch(Exception ex){
				ex.printStackTrace();
			}
		}

		@Override
		public void run() {
			Message message;
			try{
				while((message = (Message) reader.readObject())!=null){
					user = message.getOwner();
					putMsg(message);
					dataDisplay.append("read: " + message.getContent() + "\n");
				}	
			}catch(Exception ex){
				ex.printStackTrace();
				System.out.println("");
			}
		}
	}
	
	//Adiciona um cliente à lista assim que se Conecta//
	public void addUser(String owner) {
		synchronized (usersListLock) {
			usersOnline.add(owner);
			System.out.println("Online List: " + usersOnline.toString());
		}
	}
	
	

	//Põe Mensagens na Estrutura HashMap//
	public void putMsg(Message message){

		synchronized (hashMapLock){
			
			
			
		if(message.getContent().equals("END") && message.getDestinyUser().isEmpty()){
			for(int i = 0; i<usersOnline.size(); i++){
				if(usersOnline.get(i).equals(message.getOwner())){
					System.out.println(usersOnline.get(i) + " Desligou-se do Servidor...");
					usersOnline.remove(i);
					hashMap.remove(usersOnline.get(i));
				}
			}
			System.out.println(message.getOwner() + " perdeu a ligação!");
		}
		else if(isGroupMessage(message)){
			System.out.println("é um sms de grupo");
			ArrayList<String> destinyUserList = getAllGroupMember(message.getDestinyUser());
			for(int i=0;i<destinyUserList.size();i++){
				System.out.println("o cliente atual é: " +destinyUserList.get(i));
				String newDestinyUser = changeString(destinyUserList.get(i), message.getOwner(),message.getDestinyUser());
				System.out.println("o novo destinerUser é: " +newDestinyUser);
				String group = message.getDestinyUser() + "," + message.getOwner();
				System.out.println(group);
				Message changedMessage = new Message(group, newDestinyUser , message.getContent());
				System.out.println("A nova sms ´: " + group + changedMessage.getDestinyUser());
				if(!changedMessage .getContent().isEmpty() && hashMap.containsKey(destinyUserList.get(i))){
					hashMap.get(destinyUserList.get(i)).add(changedMessage);
					System.out.println("Messages to send: " + hashMap.toString());
					hashMapLock.notifyAll();
				}
				else {
					messageQueue = new LinkedList<Message>();
					messageQueue.add(changedMessage);
					hashMap.put(destinyUserList.get(i),messageQueue);
					hashMapLock.notifyAll();
					System.out.println("Messages to send: " + hashMap.toString());
				}
			}
			
			
		}
		else{
			if(!message.getContent().isEmpty() && hashMap.containsKey(message.getDestinyUser())){
				hashMap.get(message.getDestinyUser()).add(message);
				System.out.println("Messages to send: " + hashMap.toString());
				hashMapLock.notifyAll();
			}
			else{
				messageQueue = new LinkedList<Message>();
				messageQueue.add(message);
				hashMap.put(message.getDestinyUser(),messageQueue);
				hashMapLock.notifyAll();
				System.out.println("Messages to send: " + hashMap.toString());
			}
		}

		}
	}
	
	
	
	private boolean isGroupMessage(Message message){
		if(message.getDestinyUser().contains(","))
			return true;
		return false;
	}
	
	private ArrayList<String> getAllGroupMember(String groupName ){
		String groupElement;
		ArrayList<String> groupMember = new ArrayList<String>();
		String segments[] = groupName.split(",");

		for(int i=0;i<segments.length;i++){
			groupElement = segments[i];
			groupMember.add(groupElement);
		}
		return groupMember;
	}
	private String changeString(String toBeChanged,String outString,String inString){
		String changedString=new String();
		String segments[] = toBeChanged.split(",");
		for(int i=0;i<segments.length;i++){
//			System.out.println("change string:"+segments[i]);
//			System.out.println("inString "+outString);
			if(segments[i].equals(outString)){
				segments[i]=inString;
//				System.out.println(inString);
			}
			changedString+=segments[i]+",";
			
		}
		System.out.println("o novo receptor é:"+changedString);
		return   changedString.substring(0,changedString.length()-1);
	}


	// Thread que trata de enviar Mensagens //
	public class messageSender extends Thread{
		private String myUser;
		private Socket socket;
		private ObjectOutputStream  writer;

		public messageSender(Socket clientSocket, String myUser){
			try {
				socket = clientSocket;
				writer = new ObjectOutputStream(socket.getOutputStream());
				this.myUser=myUser;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void run(){
			Message message = null;
			try{
				while(true){
				Queue<Message> msgs = getMsgs(myUser);
				while(!msgs.isEmpty()){
					//Message message = null;
					message = msgs.poll();
					writer.writeObject(message);
					writer.flush();
				}
				//writer.writeObject(new Message(message.getOwner(), message.getDestinyUser(), notification));
				//msgs.clear();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}catch(InterruptedException e1){
				e1.printStackTrace();}
			}
	}

	//Vai Retirar as Mensagens Da Estrutura//
	public Queue<Message> getMsgs(String myUser) throws InterruptedException {
		System.out.println("Busca de mensagens iniciada...");
		synchronized (hashMapLock) {
			Queue<Message> fila = null;
			while(hashMap.get(myUser).isEmpty()){
				System.out.println("Thread " + myUser + " está em espera...");
				hashMapLock.wait();
			}
			fila =  hashMap.get(myUser);
			System.out.println("Thread do " + myUser + " Acordou!! Vou Eviar -->" + fila.toString());
			//hashMapLock.notifyAll();
			return fila;
		}
	}

	// Inicia o Servidor //
	public void runServer() throws IOException{
		serverFrame = new JFrame("Server");
		dataDisplay = new JTextArea();
		dataDisplay.setEditable(false);
		serverFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		serverFrame.setLayout(new BorderLayout());
		serverFrame.add(dataDisplay,BorderLayout.CENTER);
		serverFrame.setSize(500, 500);
		serverFrame.setLocation(850,0);
		serverFrame.setVisible(true);
		@SuppressWarnings("resource")
		ServerSocket serverSock = new ServerSocket(PORT);

		try{
			while(true){
				System.out.println("Waiting for connection...");
				clientSocket = serverSock.accept();
				//writer = new ObjectOutputStream(clientSocket.getOutputStream());
				Thread in = new Thread(new messageReceiver(clientSocket));
				in.start();
				dataDisplay.append("got a connection" + "\n");
			}

		}
		finally{
			clientSocket.close();
		}
	}

	//	public void tellEveryone(Message message){
	//		Iterator<ObjectOutputStream> i = clientOutputStreams.iterator();
	//		while(i.hasNext()){
	//			try{
	//				ObjectOutputStream writer = i.next();
	//				writer.writeObject(message);
	//				writer.flush();	
	//			}catch(Exception ex){ex.printStackTrace();}
	//		}
	//	}


}	


