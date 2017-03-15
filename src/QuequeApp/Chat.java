package QuequeApp;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class Chat {
	private  JButton sendBtn;
	private  JTextArea textArea;
	private  String name;
	private  JFrame chat;
	
	public Chat(String name){
		this.name=name;
		chat = new JFrame(name);
		chat.setLocation(300,0);
		chat.setSize(300,400);
		chat.setLayout(new BorderLayout());

		JLabel cabeçalho = new JLabel("chat with: " + name,SwingConstants.CENTER);
		cabeçalho.setOpaque(true);
		cabeçalho.setBackground(Color.green);
		chat.add(cabeçalho,BorderLayout.NORTH);

		JTextField msg = new JTextField();
		 textArea = new JTextArea();
		textArea.setEditable(false);
		 sendBtn = new JButton("ENVIAR");
		JPanel chatPanel = new JPanel();

		chatPanel.setLayout(new GridLayout(1, 2));
		sendBtn.setBackground(Color.darkGray);
		sendBtn.setForeground(Color.white);
		chatPanel.add(msg);
		chatPanel.add(sendBtn);

		sendBtn.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				String text = msg.getText();
				//File file = null ;
				try{
					if(!text.equals("") && text != null ) {
						Message m=new Message(QuequeApp.user,name,text);
						String text1 = "you sent: " + m.getContent();
						textArea.append(text1+ "\n");
						QuequeApp.confdir.writeIntoFiles(QuequeApp.confdir.chooseFile(QuequeApp.folder, name), text1);
						QuequeApp.clientSender.Send(m);
						
					}
				}
				catch(Exception e){
					e.printStackTrace();
				}
				
				msg.setText(null);
				
				

			}

		});

		chat.add(chatPanel, BorderLayout.SOUTH);
		chat.add(textArea, BorderLayout.CENTER);
		


	}
	public JFrame getJFrame(){
		return chat;
	}
	public String getName(){
		return name;
	}
	
	
	public void receiveMessage(Message message){
		String text = message.getOwner() +" said: "+ message.getContent();
		textArea.append(text+"\n");
		QuequeApp.confdir.writeIntoFiles(QuequeApp.file,text);
		}
	public void Notify(Message message){
		textArea.append(message.getContent() +"\n");
		QuequeApp.confdir.writeIntoFiles(QuequeApp.file,message.getContent());
		
	}
	public JTextArea getTextArea(){
		return textArea;
	}
	
	public void setVisible(){
		chat.setVisible(true);
	}
	
	public void retireVisible(){
		chat.setVisible(false);
	}
	
	public boolean isVisible(){
		return chat.isVisible();
	}

	
	
}
