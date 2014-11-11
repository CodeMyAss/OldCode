package memory.game;

public class Level {
	Stats stat;
	int cards_easy = 70/4;
	int cards_medi = 70/2;
	int cards_hard = 70;
	
	int shuffle_easy = 200;
	int shuffle_medi = 700;
	int shuffle_hard = 1100;

	int easy[] = {1,2,3,4,5};
	int medi[] = {6,7,8,9,10};
	int hard[] = {11,12,13,14,15};
	
	public Level(Stats s){
		stat = s;
	}
	
	public int getCards(int l){
		for(int i=0;i<easy.length;i++){
			if(easy[i] == l){
				return cards_easy;
			}
		}
		for(int i=0;i<medi.length;i++){
			if(medi[i] == l){
				return cards_medi;
			}
		}
		for(int i=0;i<hard.length;i++){
			if(hard[i] == l){
				return cards_hard;
			}
		}
		
		return 0;
	}
	
	public int getShuffle(int l){
		for(int i=0;i<easy.length;i++){
			if(easy[i] == l){
				return shuffle_easy;
			}
		}
		for(int i=0;i<medi.length;i++){
			if(medi[i] == l){
				return shuffle_medi;
			}
		}
		for(int i=0;i<hard.length;i++){
			if(hard[i] == l){
				return shuffle_hard;
			}
		}
		
		return 0;
	}
}
