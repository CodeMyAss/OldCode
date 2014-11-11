package memory.game;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class Stats {
	
	private int points = 0;
	private long time = 0;
	private String timeSTR = "<No Time>";
	private int highscore = 0;
	int level = 1;
	boolean createNewPlayer = false;
	String path = System.getProperty("user.dir");
	
	boolean pause = false;
	
	RandomAccessFile levelDAT;
	
	public Stats(){
		
	}
	
	public void start(){
		if(createNewPlayer){
			File f = new File(path+"\\PLAYER\\level.dat");
			if(!f.exists()){
				try {
					f.createNewFile();
				} catch (IOException e) {e.printStackTrace();}
			}
		}
		try {
			levelDAT = new RandomAccessFile(path+"\\PLAYER\\level.dat","rw");
		} catch (FileNotFoundException e) {e.printStackTrace();}
		if(createNewPlayer){
			try {
				levelDAT.seek(0);
				levelDAT.writeInt(0);
			} catch (IOException e) {e.printStackTrace();}
		}
		load();
		calcTime();
	}
	
	public void calcTime(){
		long minutes = time/60;
		long seconds = time-(minutes*60);
		timeSTR = minutes+"m "+seconds+"s";
	}
	
	public BufferedImage getStats(){
		BufferedImage img = new BufferedImage(800, 45, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = (Graphics2D) img.getGraphics();
		g.setFont(new Font("Lucida Console", Font.BOLD, 20));
		g.drawString("Points:    "+points, 20, 20);
		g.drawString("Highscore: "+highscore, 20, 40);
		g.drawString("Time:      "+timeSTR, 260, 20);
		g.drawString("Level:     "+level, 260, 40);
		return img;
	}
	
	public void save(){
		try {
			levelDAT.seek(0);
			levelDAT.writeInt(level);
			levelDAT.seek(1024);
			levelDAT.writeInt(highscore);
		} catch (IOException e) {e.printStackTrace();}
	}
	
	public void load(){
		try {
			levelDAT.seek(0);
			level = levelDAT.readInt();
			levelDAT.seek(1024);
			highscore = levelDAT.readInt();
		} catch (IOException e) {e.printStackTrace();}
	}

	public void updateTime(int s) {
		time += s;
	}

	public void updatePoints(int p) {
		points += p;
		if(points>highscore){
			highscore = points;
		}
	}
	
	public String getTimeStr(){
		return timeSTR;
	}
	
	public int getPoints(){
		return points;
	}
	
	public int getHighscore(){
		return highscore;
	}
}
