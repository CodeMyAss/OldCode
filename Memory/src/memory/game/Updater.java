package memory.game;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JFrame;
import javax.swing.JTextPane;


public class Updater {
	
	//FILE PATHS
	String path = System.getProperty("user.dir");
	File imgDir = new File(path+"\\IMAGES\\");
	File playerPath = new File(path+"\\PLAYER\\");
	File filesPath = new File(path+"\\FILES\\");
	
	File cFrnt = new File(path+"\\IMAGES\\card.png");
	File cBack = new File(path+"\\IMAGES\\card_back.png");
	File bg = new File(path+"\\IMAGES\\background.png");
	
	boolean needsUpdate = false;
	
	JFrame loadingScr;
	JTextPane loadingNow = new JTextPane();
	
	Debug debug;
	
	public Updater(Debug debug, Stats st) throws Exception{
		loadingNow.setText(loadingNow.getText()+"Starting Debuger...\n");
		loadingNow.setText(loadingNow.getText()+"     Creating Log...\n");
		loadingNow.setText(loadingNow.getText()+"     Debugger Loaded!\n");
		loadingNow.setText(loadingNow.getText()+"     "+debug.previousSessions+" Previous Sessions Found!\n");
		loadingNow.setEditable(false);
		loadingNow.setText(loadingNow.getText()+"Starting Updater...\n");
		loadingScr = new JFrame("LOADING...");
		loadingScr.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		loadingScr.setSize(400,350);
		loadingScr.setLocationRelativeTo(null);
		loadingScr.setResizable(false);
		loadingScr.add(loadingNow);
		loadingScr.setVisible(true);
		debug.writeToLog("Update Checking... \r\n");
		this.debug = debug;
		loadingNow.setText(loadingNow.getText()+"     Checking Files...\n");
		debug.writeToLog("Checking for player data... ");
		if(!playerPath.exists()){
			playerPath.mkdir();
			st.createNewPlayer = true;
			debug.writeToLog("Missing! (Creating New Player)\r\n");
		}else{
			debug.writeToLog("Found!\r\n");
		}
		loadingNow.setText(loadingNow.getText()+"Checking For JAR...\n");
		if(!filesPath.exists()){
			filesPath.mkdirs();
		}
		File f = new File(filesPath.toString()+"\\MemoryGame.jar");
		if(!f.exists()){
			debug.writeToLog("Missing!\n");
			needsUpdate = true;
		}
		debug.writeToLog("Checking for images... ");
		if(!imgDir.exists()){
			needsUpdate = true;
			imgDir.mkdir();
			debug.writeToLog("Image Directory Missing!\r\n");
			loadingNow.setText(loadingNow.getText()+"     Image Directory Missing!\n");
		}else if(!cFrnt.exists()){
			needsUpdate = true;
			debug.writeToLog("Card Front Image Missing!\r\n");
			loadingNow.setText(loadingNow.getText()+"     Card Image Missing!\n");
		}else if(!cBack.exists()){
			needsUpdate = true;
			debug.writeToLog("Card Back Image Missing!\r\n");
			loadingNow.setText(loadingNow.getText()+"     Card Image Missing!\n");
		}else if(!bg.exists()){
			needsUpdate = true;
			debug.writeToLog("Background Image Missing!\r\n");
			loadingNow.setText(loadingNow.getText()+"     Background Image Missing!\n");
		}else{
			debug.writeToLog("All Good!\r\n");
			loadingNow.setText(loadingNow.getText()+"     Images All Good!\n");
		}
		debug.writeToLog("Checking Card Images... \r\n");
		loadingNow.setText(loadingNow.getText()+"     Checking Cards...\n");
		if(!needsUpdate){
			String p = path+"\\IMAGES\\Card_";
			for(int i=1;i<36;i++){
				f = new File(p+i+".png");
				debug.writeToLog("\tCard "+i+": ");
				if(!f.exists()){
					needsUpdate = true;
					debug.writeToLog("Missing!\r\n");
				}else{
					debug.writeToLog("Found!\r\n");
				}
			}
		}
	}
	
	public void update() throws MalformedURLException, Exception{
		if(needsUpdate){
			debug.writeToLog("Update Needed! Getting Files... ");
			loadingNow.setText(loadingNow.getText()+"Update Needed! Fetching Files...\n");
			@SuppressWarnings("unused")
			ExternalFileHandler files = new ExternalFileHandler(new URL("http://turt2live.com/external/memory_game/pack.zip"), path+"\\pack.zip");
			debug.writeToLog("Files Downloaded!\r\nApplying Update... ");
			loadingNow.setText(loadingNow.getText()+"Applying Update...\n");
			@SuppressWarnings("unused")
			Unzip unzip = new Unzip(path+"\\pack.zip");
			debug.writeToLog("Update Applied!\r\n");
			File f = new File(path+"\\pack.zip");
			f.delete();
			loadingNow.setText(loadingNow.getText()+"     Update Applied!\n");
		}else{
			debug.writeToLog("No Update Needed!\r\n");
			loadingNow.setText(loadingNow.getText()+"     No Update Required!\n");
		}
		debug.writeToLog("Update Checking Done.\r\n----------------------------------\r\n");
	}
}
