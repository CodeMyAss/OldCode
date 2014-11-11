package memory.game;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Random;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.event.MouseInputListener;

@SuppressWarnings("serial")
public class Content extends JPanel implements MouseInputListener, KeyListener, WindowListener{
	
	JFrame window;
	Vector<Card> cards = new Vector<Card>();
	Debug debug;
	Updater update;
	Level level;

	Stats stats;
	
	int cardID1 = -1;
	int cardID2 = -1;
	Card card1;
	
	Vector<Integer> usedID = new Vector<Integer>();
	ThreadedTimer th;
	Thread timer;
	
	BufferedImage overlay = new BufferedImage(800,800,BufferedImage.TYPE_INT_ARGB);
	Graphics2D over = (Graphics2D) overlay.getGraphics();
	
	private String path = System.getProperty("user.dir");
	
	public Content(JFrame window, Debug debug, Updater update, Stats st) throws IOException{
		stats = st;
		level = new Level(stats);
		window.addMouseListener(this);
		window.addKeyListener(this);
		this.window = window;
		this.debug = debug;
		debug.writeToLog("Generating Cards... \r\n");
		update.loadingNow.setText(update.loadingNow.getText()+"Generating Cards...\n");
		this.update = update;
		genCards(70); //ALWAYS GEN 70 CARDS ON BOARD
		scrambleCards();
		th = new ThreadedTimer(window, cards, this);
		update.loadingNow.setText(update.loadingNow.getText()+"Loading Player Data... \n");
		stats.start();
		update.loadingNow.setText(update.loadingNow.getText()+"\nDone Loading! Starting Game... \n");
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {}
		update.loadingScr.dispose();
		JOptionPane.showMessageDialog(null, "Press OK to start!");
		TimeCounter time = new TimeCounter(window,stats);
		Thread timer = new Thread(time);
		timer.start();
	}
	
	private void scrambleCards() throws IOException {
		debug.writeToLog("Mixing up cards... ");
		update.loadingNow.setText(update.loadingNow.getText()+"Shuffling Cards...\n");
		int shuffles = 0;
		for(int y=0;y<cards.size()*(stats.level*level.getShuffle(stats.level));y++){
			for(int i=0;i<cards.size();i++){
				Card card = cards.get(i);
				Random r = new Random(System.currentTimeMillis());
				int newPOS = -1;
				Point newPOI = new Point(0,0);
				boolean valid = false;
				while(!valid){
					int posIDN = r.nextInt((level.getCards(stats.level)/2)+1);
					if(
							posIDN>0
							&&posIDN<(level.getCards(stats.level)/2)+1
							&&posIDN!=card.POSID
							&&(Math.abs(posIDN-card.POSID)>(level.getCards(stats.level)/10))
					){
						newPOS = posIDN;
						valid = true;
					}
				}
				for(int x=0;x<cards.size();x++){
					Card tCard = cards.get(x);
					if(tCard.POSID!=newPOS){
						newPOI = tCard.pos;
						tCard.pos = card.pos;
						card.pos = newPOI;
						tCard.POSID = card.POSID;
						card.POSID = newPOS;
						card.updateRect();
						tCard.updateRect();
						shuffles++;
						break;
					}
				}
			}
		}
		//System.out.println(shuffles+" Shuffles");
		debug.writeToLog("Done!\r\n");
	}

	private void genCards(int amount) throws IOException {
		int x = 20;
		int y = 50;
		int h = 0;
		int v = 0;
		for(int i=0;i<amount;i++){
			Point p = new Point(x,y);
			int id = getID(i);
			cards.add(new Card(p,id,i));
			debug.writeToLog("\tCard "+(i+1)+"/"+amount+" Generated! (ID = "+id+")\r\n");
			h++;
			x += 75;
			if(h>9){
				x=20;
				y += 100;
				h=0;
				v++;
			}
			if(v>7){
				break;
			}
		}
	}
	
	private int getID(int on) {
		int idR = 0; 
		Random r = new Random(System.currentTimeMillis());
		if(on%2 == 1){ 
			idR = Integer.parseInt(usedID.get(on-1).toString()); 
		}else{
			boolean valid = false;
			while(!valid){
				int idRE = r.nextInt((level.getCards(stats.level)/2)+1);
				if(idRE>0&&idRE<(level.getCards(stats.level)/2)+1){ 
					boolean inVec = false;
					if(level.getCards(stats.level)==70){
						for(int i=0;i<usedID.size();i++){
							if(usedID.get(i) == idRE){
								inVec = true;
								break;
							}
						}
					}else{
						inVec = false;
					}
					if(inVec){
						valid = false;
					}else{
						idR = idRE;
						valid = true;
					}
				}else{
					valid = false;
				}
			}
		}
		usedID.add(idR); 
		return idR; 
	}
	
	public void paintComponent(Graphics g){
		Graphics2D g2 = (Graphics2D) g;
		g2.drawImage(new ImageIcon(path+"\\IMAGES\\background.png").getImage(), 0, 0, null);
		for(int i=0;i<cards.size();i++){
			g2.drawImage(cards.get(i).getImage(),cards.get(i).pos.x,cards.get(i).pos.y,null);
			//over.setColor(new Color(cards.get(i).id,cards.get(i).id,cards.get(i).id,200));
			//over.fill(cards.get(i).card);
		}
		g2.drawImage(stats.getStats(), 0, 0, null);
		g2.drawImage(overlay, 0, 0, null);
		g2.dispose();
	}
	
	@SuppressWarnings("deprecation")
	public void dumpCards(){
		try{
			BufferedImage img = new BufferedImage(800, 800, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = (Graphics2D) img.getGraphics();
			for(int i=0;i<cards.size();i++){
				Card card = cards.get(i);
				g.drawImage(card.getImage(),card.pos.x,card.pos.y,null);
				g.drawImage(card.cItem,card.pos.x,card.pos.y,null);
				g.setFont(new Font("Arial",Font.BOLD,14));
				g.setColor(Color.WHITE);
				g.drawString("I: "+card.id, card.pos.x+5, card.pos.y+10);
			}
			File outDir = new File(path+"\\DEBUG\\");
			Calendar now = Calendar.getInstance();
			String date = now.getTime().toGMTString().replace(" ", "_").replace(":", "_");
			File outFile = new File(outDir.toString()+"\\"+date+".png");
			if(!outDir.exists()){
				outDir.mkdirs();
			}
			debug.writeToLog("DUMPING SCREEN TO "+outFile.toString()+"\r\n");
			ImageIO.write(img, "png", outFile);
		}catch(IOException e){e.printStackTrace();}
	}
	
	@SuppressWarnings("deprecation")
	public void dumpBoard(){
		try{
			BufferedImage img = new BufferedImage(800, 800, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = (Graphics2D) img.getGraphics();
			g.drawImage(new ImageIcon(path+"\\IMAGES\\background.png").getImage(), 0, 0, null);
			for(int i=0;i<cards.size();i++){
				g.drawImage(cards.get(i).getImage(),cards.get(i).pos.x,cards.get(i).pos.y,null);
			}
			g.drawImage(stats.getStats(), 0, 0, null);
			g.drawImage(overlay, 0, 0, null);
			File outDir = new File(path+"\\SCREENSHOTS\\");
			Calendar now = Calendar.getInstance();
			String date = now.getTime().toGMTString().replace(" ", "_").replace(":", "_");
			File outFile = new File(outDir.toString()+"\\"+date+".png");
			if(!outDir.exists()){
				outDir.mkdirs();
			}
			debug.writeToLog("DUMPING BOARD TO "+outFile.toString()+"\r\n");
			ImageIO.write(img, "png", outFile);
		}catch(IOException e){e.printStackTrace();}
	}

	public void mouseClicked(MouseEvent e) {
		if(!stats.pause){
			Point p = new Point(e.getX(),e.getY()-15);
			boolean match = false;
			for(int i=0;i<cards.size();i++){
				Card card = cards.get(i);
				//If no card selected
				if(card.state!=2){ //If alive
					if(cardID1 == -1 && card.isClicked(p) && cardID2 == -1){
						card.updateState();
						cardID1 = card.POSID;
						cardID2 = card.id;
						card1 = card;
					}else if(cardID1 >= 0 && card.isClicked(p)){ //If second card is clicked
						if(card.pos != card1.pos){
							if(card.id == cardID2){ //Check Match (T = Match)
								card.queForDeath = true;
								card1.queForDeath = true;
								cardID1 = -1;
								cardID2 = -1;
								card.updateState();
								card = null;
								card1 = null;
								match = true;
								stats.updatePoints(1);
							}else{
								match = true;
								card.updateState();
								//card1.updateState();
								cardID1 = -1;
								cardID2 = -1;
								card = null;
								card1 = null;
							}
						}
					}
				}
			}
			//over.setColor(Color.RED);
			//over.fill(new Rectangle(p, new Dimension(1,1)));
			e.consume();
			window.repaint();
			if(match){
				th = new ThreadedTimer(window, cards, this);
				timer = new Thread(th);
				timer.start();
			}
			try {
				debug.writeToLog("Clicked At Point ("+p.getX()+", "+p.getY()+")\r\n");
			} catch (IOException e1) {}
		}else{
			JOptionPane.showMessageDialog(null, "Please close the pause menu before starting the game!", "ERROR - Pause Menu Open", JOptionPane.ERROR_MESSAGE);
		}
		boolean stillOn = false;
		for(int i=0;i<cards.size();i++){
			Card card = cards.get(i);
			if(card.state!=2){
				stillOn = true;
				break;
			}
		}
		if(!stillOn && stats.level < 15){
			stats.pause = true;
			JOptionPane.showMessageDialog(null, "Your Time: "+stats.getTimeStr()+"\nPoints: "+stats.getPoints()+"\nHighscore: "+stats.getHighscore());
			try {
				Window.updateLevel();
			} catch (IOException e1) {} catch (Exception e2) {}
		}else if(!stillOn && stats.level == 15){
			JOptionPane.showMessageDialog(null, "You won!\nYour Time: "+stats.getTimeStr()+"\nPoints: "+stats.getPoints()+"\nHighscore: "+stats.getHighscore());
			stats.level = 1;
			stats.save();
		}
	}
	public void keyPressed(KeyEvent e) {
		int code = e.getKeyCode();
		if(code == KeyEvent.VK_F2){
			dumpCards();
		}else if(code == KeyEvent.VK_F3){
			dumpBoard();
		}else if(code == KeyEvent.VK_R){
			for(int i=0;i<cards.size();i++){
				Card card = cards.get(i);
				if(card.state!=2){
					card.state=1;
					card.updateState(); //Force ICO change
				}
			}
		}else if(code == KeyEvent.VK_F1 || code == KeyEvent.VK_ESCAPE){
			stats.pause = true;
			JDialog pauseMenu = new JDialog(window, "Pause Menu");
			pauseMenu.setSize(500, 500);
			pauseMenu.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			JTextArea text = new JTextArea();
			text.setEditable(false);
			text.setText("Objective: \nMatch all of the cards to eachother, clearing the board.\n\nControls:\nLeft Click - Flip / Select Card\n'R' Key - Reset Board (Hide Cards)\n'F1' - Show This Menu\n'ESC' Key - Show This Menu\n'F3' - Screenshot");
			pauseMenu.add(text);
			pauseMenu.setLocationRelativeTo(window);
			pauseMenu.setVisible(true);
			pauseMenu.addWindowListener(this);
			window.repaint();
		}else if(code == KeyEvent.VK_F10){
			for(int i=0;i<cards.size();i++){
				Card card = cards.get(i);
				card.die();
				stats.updatePoints(1);
			}
			window.repaint();
		}
		e.consume();
	}
	public void windowClosed(WindowEvent e) {
		stats.pause = false;
	}
	public void windowClosing(WindowEvent e) {
		stats.pause = false;
	}
	public void mouseEntered(MouseEvent e) {e.consume();}
	public void mouseExited(MouseEvent e) {e.consume();}
	public void mousePressed(MouseEvent e) {e.consume();}
	public void mouseReleased(MouseEvent e) {e.consume();}
	public void mouseDragged(MouseEvent e) {e.consume();}
	public void mouseMoved(MouseEvent e) {e.consume();}
	public void keyReleased(KeyEvent e) {e.consume();}
	public void keyTyped(KeyEvent e) {e.consume();}
	public void windowActivated(WindowEvent e) {}
	public void windowDeactivated(WindowEvent e) {}
	public void windowDeiconified(WindowEvent e) {}
	public void windowIconified(WindowEvent e) {}
	public void windowOpened(WindowEvent e) {}
	
}
