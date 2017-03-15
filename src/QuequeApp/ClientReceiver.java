package QuequeApp;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ClientReceiver implements Runnable{
	private  ObjectInputStream fromServer;
	private Chat chat;
	private  final String notification = " delivered";
	public static Socket sock;


	public ClientReceiver(Socket socket){
		sock=socket;
		try{
			fromServer = new ObjectInputStream( sock.getInputStream());	

		}
		catch(Exception e){
			e.printStackTrace();
		}


	}



	@Override
	public void run() {
		try{
			Message message = null;
			while(true){
				while((message = (Message) fromServer.readObject()) != null){
					receive(message);
					System.out.println("done");
				}
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}



	private void receive(Message message)throws IOException{
		System.out.println("isGroupMessage: " +isGroupMessage(message));
		if(!isGroupMessage(message)){
			if(QuequeApp.userListModel.contains(message.getOwner())){
				oldClient(message);
			}
			else
				newClient(message);
		}
		else
			oldGroup(message);
	}



	private void oldClient(Message message) throws IOException{	
		for(int i=0;i<QuequeApp.myChatList.size();i++){
			if(QuequeApp.myChatList.get(i).getJFrame().getTitle().equals(message.getOwner())){

				if(message.getContent().equals(notification) ){
					if(QuequeApp.confdir.readLastLine(QuequeApp.confdir.chooseFile(QuequeApp.folder, message.getOwner()))!= notification)
						QuequeApp.myChatList.get(i).Notify(message);
				}
				else{
					QuequeApp.myChatList.get(i).receiveMessage(message);
					QuequeApp.clientSender.Send(new Message(QuequeApp.user, message.getOwner(), notification));
					if(!QuequeApp.myChatList.get(i).getJFrame().isVisible()){
						QuequeApp.userListModel1.setElementAt("tens mensagem nova",i);	
					}
				}
			}

		}

	}

	private void newClient(Message message) throws IOException{
		QuequeApp.friendName = message.getOwner();
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		String text = "you and " + QuequeApp.friendName + " are friends since " + dateFormat.format(new Date());
		QuequeApp.confdir.createFile(QuequeApp.folder, QuequeApp.friendName);
		QuequeApp.file=QuequeApp.confdir.createFile(QuequeApp.folder, QuequeApp.friendName);;
		QuequeApp.confdir.writeIntoFiles(QuequeApp.file, text);
		QuequeApp.userListModel.addElement(message.getOwner());
		QuequeApp.userListModel1.addElement("tens mensagem nova");
		chat = new Chat(message.getOwner());
		chat.receiveMessage(message);

		QuequeApp.myChatList.add(chat);
		if(!message.getContent().equals(notification))
			QuequeApp.clientSender.Send(new Message(QuequeApp.user, message.getOwner(), notification));

	}



	

	private void oldGroup(Message message) throws IOException{
		for(int i=0;i<QuequeApp.myChatList.size();i++){
			if(belongsToGroup(QuequeApp.myChatList.get(i).getJFrame().getTitle(), message.getDestinyUser())){
				if(message.getContent().equals(notification) ){
					if(!QuequeApp.confdir.readLastLine(QuequeApp.confdir.chooseFile(QuequeApp.folder, message.getOwner())).equals(notification))
						QuequeApp.myChatList.get(i).Notify(message);
				}
				else{
					QuequeApp.myChatList.get(i).receiveMessage(message);
					QuequeApp.clientSender.Send(new Message(QuequeApp.user, message.getOwner(), notification));
					if(!QuequeApp.myChatList.get(i).getJFrame().isVisible()){
						QuequeApp.userListModel1.setElementAt("tens mensagem nova",i);	
					}
				}
			}	
		}
	}
	
	
	private boolean isGroupMessage(Message message){
		if(message.getDestinyUser().contains(","))
			return true;
		return false;
	}
	
	
	private boolean belongsToGroup(String groupName,String chatName){
		if(getAllGroupMember(groupName ).size()==getAllGroupMember(chatName ).size()){
			for(int i= 0;i< getAllGroupMember(groupName ).size();i++){
				for(int j=0;j<getAllGroupMember(chatName ).size();j++){
					if(!getAllGroupMember(groupName ).get(i).equals(getAllGroupMember(chatName ).get(j)) && j==getAllGroupMember(chatName ).size()-1)
						return false;
				}
			}
		}
		else
			return false;
		return true;
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

	
	
	
	
	
	
//	private boolean belongsToGroup(String groupName,String chatName){
//		String groupElement;
//		ArrayList<String> groupMember = new ArrayList<String>();
//		int plusIndex = groupName.indexOf(",");
//
//		while (plusIndex != -1) {
//			groupElement = groupName.substring(0, plusIndex);
//			groupMember.add(groupElement);
//		}
//
//		String groupElement1;
//		ArrayList<String> groupMember1 = new ArrayList<String>();
//		int plusIndex1 = chatName.indexOf(",");
//		while (plusIndex1 != -1) {
//			groupElement1 = chatName.substring(0, plusIndex1);
//			groupMember1.add(groupElement1);
//		}
//		if(groupMember.size()==groupMember1.size()){
//			for(int i= 0;i< groupMember.size();i++){
//				for(int j=0;j<groupMember1.size();j++){
//					if(!groupMember.get(i).equals(groupMember1.get(j)) && j==groupMember1.size()-1)
//						return false;
//				}
//			}
//		}
//		else
//			return false;
//		return true;
//	}





}






