package memory.game;

import java.util.Calendar;

import javax.swing.JFrame;

public class Window {
	
	static JFrame window;
	static Debug debug;
	static Updater updateG;
	static Stats stG;
	
	@SuppressWarnings("deprecation")
	public static void main(String[] args) throws Exception{
		debug = new Debug();
		Calendar now = Calendar.getInstance();
		String date = now.getTime().toGMTString();
		debug.writeToLog("Session Started "+date+"\r\nDIRECTORY: "+System.getProperty("user.dir")+"\r\n");
		Stats st = new Stats();
		Updater update = new Updater(debug, st);
		update.update();
		window = new JFrame("Game of Memory");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setSize(800,800);
		window.setResizable(false);
		window.setLocationRelativeTo(null);
		window.add(new Content(window, debug, update, st));
		window.setVisible(true);
		stG = st;
		updateG = update;
	}
	
	public static void updateLevel() throws Exception{
		stG.level+=1;
		stG.save();
		stG.levelDAT.close();
		Thread.sleep(200);
		Runtime.getRuntime().exec("cmd /c start FILES/MemoryGame.jar");
		Thread.sleep(200);
		System.exit(0);
	}
	
}
