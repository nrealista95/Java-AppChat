package QuequeApp;

public class GroupMessage extends Message{

	private String group;

	public GroupMessage(String owner, String destinyUser, String content, String group) {
		super(owner, destinyUser, content);
		this.group=group;
	}

	public String getGroup() {
		return group;
	}
	
	

}
