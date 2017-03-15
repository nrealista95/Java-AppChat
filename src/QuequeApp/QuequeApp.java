package QuequeApp;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class QuequeApp{

	private JFrame loguinUser;
	private JFrame quequeApp;
	public static ArrayList<Chat> myChatList  = new ArrayList<Chat>();
	private JTextField userText;
	private JButton loguinButton;
	public static JTextArea textArea;
	public static String user = new String();
	private JTabbedPane userTabbedPane;
	public static DefaultListModel<String> userListModel = new DefaultListModel<String>(); 
	public static JList<String> userList = new JList<String>(userListModel);
	public static  DefaultListModel<String>userListModel1 = new DefaultListModel<String>();
	public static JList<String> userList1 = new JList<String>(userListModel1);
	public static String friendName;
	//public static File ficheiro; 
	//private File file;
	public static Files confdir;
	public static File folder;
	public static File file;

	public static Socket socket;
	public static ClientSender clientSender;
	private Chat chat;



	//------------------------------------------------PAINEL DO LOGUIN-------------------------------------//	


	public QuequeApp(){

		loguinUser = new JFrame("Loguin");
		loguinUser.setLayout(new BorderLayout());
		loguinUser.setResizable(false);
		loguinUser.setSize(300,90);
		loguinUser.setLocation(500,300);
		loguinUser.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		userText= new JTextField(10);
		loguinButton= new JButton("Loguin");
		JPanel painel = new JPanel(new GridLayout(1, 2));
		painel.add(userText);
		painel.add(loguinButton);

		JLabel label = new JLabel("Loguin as:");


		loguinUser.add(label,BorderLayout.NORTH);
		loguinUser.add(painel,BorderLayout.CENTER);



		loguinButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(userText.getText().matches(".*[a-zA-Z].*")){
					user = userText.getText();
					confdir = new Files();
					confdir.createFolder(user);
					folder = confdir.createFolder(user);
					clientSender = new ClientSender();
					socket = clientSender.connectToServer();

					try {
						Message message = new Message(user,"", "");
						clientSender.Send(message);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					loguinUser.dispose();
					userTab();
				}

			}
		});


	}





	public void run(){
		loguinUser.setVisible(true);
	}




	//---------------------------------------  PAINEL DOS CONTACTOS  -------------------------------//

	public void userTab(){
		new Files().readFromFile(folder);
		new Thread(new ClientReceiver(QuequeApp.socket)).start();
		userTabbedPane= new JTabbedPane();
		quequeApp = new JFrame("QuequeApp");
		quequeApp.setLayout(new BorderLayout());
		quequeApp.setSize(300,400);
		quequeApp.setResizable(true);
		quequeApp.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		

		JLabel quequeUser = new JLabel(user); 


		JPanel loguinPanel =new JPanel(new BorderLayout());
		loguinPanel.add(quequeUser,BorderLayout.NORTH);

		JPanel addUserPanel = new JPanel(new GridLayout(1, 2));
		addUserPanel.add(quequeUser,BorderLayout.NORTH);



		addUserPanel.setBorder(BorderFactory.createLineBorder(Color.black));
		JPanel userPanel = new JPanel(new BorderLayout());
		userPanel.add(quequeUser,BorderLayout.NORTH);

		JButton addUserButton = new JButton("+addUser");

		JTextField userNameTextField = new JTextField(10);
		addUserPanel.add(addUserButton);
		addUserPanel.add(userNameTextField);


		JPanel listPainel = new JPanel(new GridLayout(1,2));



		userList.setBorder(BorderFactory.createLineBorder(Color.black));
		userList1.setBorder(BorderFactory.createLineBorder(Color.black));

		listPainel.add(userList);
		listPainel.add(userList1);

		addUserButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				friendName = userNameTextField.getText();
				if(friendName.matches(".*[a-zA-Z].*") && !friendName.contains(" ") && !userListModel.contains(friendName) ) {
					userListModel.addElement(friendName );
					userListModel1.addElement("sem mensagens novas");
					chat = new Chat(friendName);
					myChatList.add(chat);
					DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
					//confdir.writeIntoFiles(friendName, folder,"you and " + friendName + " are friends since " + dateFormat.format(new Date()));
					confdir.createFile(folder, friendName);
					file = confdir.createFile(folder, friendName);
					confdir.writeIntoFiles(file, "you and " + friendName + " are friends since " + dateFormat.format(new Date()));
				}
				userNameTextField.setText(null);
			}

		});

		JPanel userPainel = new JPanel(new BorderLayout());
		userPainel.add(addUserPanel,BorderLayout.NORTH);
		userPainel.add(listPainel,BorderLayout.CENTER);

		userPanel.add(userPainel,BorderLayout.CENTER);

		userTabbedPane.add("Users", userPanel);
		userTabbedPane.setBorder(BorderFactory.createLineBorder(Color.black));
		quequeApp.add(userTabbedPane);
		quequeApp.setVisible(true);

		MouseListener mouseListener = new MouseAdapter(){

			public void mouseClicked(MouseEvent mouseEvent) {
				@SuppressWarnings("unchecked")
				JList<String> theList = (JList<String>) mouseEvent.getSource();
				if (mouseEvent.getClickCount() == 2 && (String) theList.getSelectedValue()!=null){ 
					String name = (String) theList.getSelectedValue();
					for(int i=0;i<myChatList.size();i++){
						if(!myChatList.get(i).getJFrame().getTitle().equals(name))
							myChatList.get(i).getJFrame().setVisible(false);
						if(myChatList.get(i).getJFrame().getTitle().equals(name) && !myChatList.get(i).getJFrame().isVisible()){
							myChatList.get(i).getJFrame().setVisible(true);
							userListModel1.setElementAt("sem mensagens novas", i);
						}

					}

				}  
			}
		};	

		userList.addMouseListener(mouseListener);

		quequeApp.addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosed(WindowEvent e){

				try{
					//Message m = new Message(null,null,user + " logg out.");
					ClientReceiver.sock.close();
					clientSender.disconnectFromServer();;
				}
				catch(Exception ex){
					ex.printStackTrace();
				}
			}
		});
		quequeApp.addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent e){
				try{
				//	Message m = new Message(null,null,user + " logg out.");
					ClientReceiver.sock.close();
					clientSender.disconnectFromServer();;
				}
				catch(Exception ex){
					ex.printStackTrace();
				}
			}
		});
	}




}