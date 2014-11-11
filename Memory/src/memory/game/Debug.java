package memory.game;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;

public class Debug {

	private File debugFile;
	int previousSessions = 0;
	
	@SuppressWarnings("deprecation")
	public Debug() throws IOException{
		Calendar now = Calendar.getInstance();
		String date = now.getTime().toGMTString().replace(" ", "_").replace(":", "_");
		File p = new File(System.getProperty("user.dir")+"\\LOGS\\");
		if(!p.exists()){
			p.mkdirs();
		}
		previousSessions = p.listFiles().length;
		debugFile = new File(System.getProperty("user.dir")+"\\LOGS\\Log_"+date+".txt");
		if(!debugFile.exists()){
			debugFile.createNewFile();
		}
	}
	
	public void writeToLog(String s) throws IOException{
		BufferedWriter w = new BufferedWriter(new FileWriter(debugFile, true));
		w.write(s);
		w.close();
	}
	
}
