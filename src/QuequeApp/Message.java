package QuequeApp;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Message implements Serializable{

	private String destinyUser;
	private String content;
	private String owner;
	
	public Message(String owner,String destinyUser,String content){
		this.owner=owner;
		this.destinyUser=destinyUser;
		this.content=content;
	}

	public String getDestinyUser(){
		return destinyUser;
	}
	
	public String getContent(){
		return content;
	}
	
	public String getOwner(){
		return owner;
	}
	
	@Override
	public String toString() {
		return "Message [destinyUser=" + destinyUser + ", content=" + content
				+ ", owner=" + owner + "]";
	}
}
