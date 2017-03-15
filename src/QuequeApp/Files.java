package QuequeApp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Files {
	FileReader fileReader;
	FileReader readLastLine;
	

	public File createFolder(String folderName){
		File file = new File("src/"+folderName);
		if (!file.exists()) {
			if (file.mkdir()) {
				System.out.println("News:" + QuequeApp.user  +" is now using QuequeApp");
			} else {
				System.out.println("Failed to create directory!");
			}
		}
		return file;
	}


	public File createFile( File folder,String fileName){
		File ficheiro = new File(folder+"/"+fileName);
		try {
			if(!ficheiro.exists())
				if(ficheiro.createNewFile()){
				}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return ficheiro;
	}

	public void writeIntoFiles(File file,String text){
		try {

			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file, true)));
			out.println(text);
			System.out.println(text);
			out.close();

		}
		catch (IOException e) {
		}
	}

	public void readFromFile(File folder){
		String sCurrentLine;
		Chat chat;
		File[] listOfFiles = folder.listFiles();
		for (File file : listOfFiles) {
			if (file.isFile()) {
				QuequeApp.userListModel.addElement(file.getName());
				QuequeApp.userListModel1.addElement("sem mensagens novas");
				chat = new Chat(file.getName());
				QuequeApp.myChatList.add(chat);
				QuequeApp.file = createFile(folder, file.getName());
				try {
					fileReader= new FileReader(QuequeApp.file);
					BufferedReader bufferedReader = new BufferedReader(fileReader);
					while ((sCurrentLine = bufferedReader.readLine()) != null) {
						chat.getTextArea().append(sCurrentLine+"\n");
					}
				}
				catch (IOException e) {
					e.printStackTrace();
				}
				
			}
		}
	}




	public File chooseFile(File dir,String lostFile) {
		File[] listOfFiles = dir.listFiles();
		File busted= null;
		for (File file : listOfFiles) {
			if (file.getName().equals(lostFile)) {
				busted = file;
			} 
		}
		return busted;
	}

	public String readLastLine(File file){	
		
		String sCurrentLine;
		String lastLine = "";
		try {
			readLastLine= new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(readLastLine);
			while ((sCurrentLine = bufferedReader.readLine()) != null) {
				lastLine = sCurrentLine;
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return lastLine;
	}


}





