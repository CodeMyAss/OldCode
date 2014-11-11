package memory.game;

import java.util.Vector;

import javax.swing.JFrame;

public class ThreadedTimer implements Runnable{
	
	JFrame window;
	Vector<Card> cards;
	int time = 1800;
	Content content;
	
	
	public ThreadedTimer(JFrame w, Vector<Card> cardVec, Content c){
		cards = cardVec;
		window = w;
		content = c;
	}

	public void run() {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		for(int i=0;i<cards.size();i++){
			Card card = cards.get(i);
			if(card.state!=2){
				card.state=1;
				card.updateState(); //Force ICO change
			}
			if(card.queForDeath){
				card.die();
			}
		}
		content.cardID1 = -1;
		content.cardID2 = -1;
		window.repaint();
	}
	
}
