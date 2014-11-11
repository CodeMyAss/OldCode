package memory.game;

import javax.swing.JFrame;

public class TimeCounter implements Runnable{

	JFrame window;
	Stats stats;
	
	public TimeCounter(JFrame w, Stats s){
		window = w;
		stats = s;
	}
	
	
	public void run() {
		while(true){
			if(!stats.pause){
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {}
				stats.updateTime(1);
				stats.calcTime();
				window.repaint();
			}
		}
	}

}
