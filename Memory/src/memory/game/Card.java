package memory.game;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;

public class Card {
	int id;
	int POSID;
	Rectangle card;
	Point pos;
	int state = 0;
	private Dimension dims = new Dimension(75,100);
	boolean queForDeath = false;
	
	ImageIcon ico = new ImageIcon(System.getProperty("user.dir")+"\\IMAGES\\card.png");
	Image cItem;
	
	public Card(Point p, int id, int POSID){
		pos = p;
		this.id = id;
		this.POSID = POSID;
		card = new Rectangle(pos,dims);
		cItem = new ImageIcon(System.getProperty("user.dir")+"\\IMAGES\\Card_"+id+".png").getImage();
	}
	
	public void updateState(){
		if(state == 0){
			ico = new ImageIcon(System.getProperty("user.dir")+"\\IMAGES\\card_back.png");
			state = 1;
		}else if(state == 1){
			ico = new ImageIcon(System.getProperty("user.dir")+"\\IMAGES\\card.png");
			state = 0;
		}
	}
	
	public void die(){
		ico = new ImageIcon(System.getProperty("user.dir")+"\\IMAGES\\card_dead.png");
		state = 2;
	}
	
	public BufferedImage getImage(){
		BufferedImage img = new BufferedImage(dims.width, dims.height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = (Graphics2D) img.getGraphics();
		//ImageIcon ico = new ImageIcon(System.getProperty("user.dir")+"\\IMAGES\\Card_"+id+".png");
		Image cImg = ico.getImage();
		g.drawImage(cImg, 0, 0, null);
		if(state == 1){
			g.drawImage(cItem, 0, 0, null);
		}
		g.dispose();
		return img;
	}
	
	
	
	public boolean isClicked(Point p){
		if(state == 2){
			return false;
		}
		if(card.contains(p)){
			return true;
		}
		return false;
	}
	
	public void updateRect(){
		card = new Rectangle(pos,dims);
	}
}
