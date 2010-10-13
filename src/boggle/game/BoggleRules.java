package boggle.game;

import java.util.HashMap;

public class BoggleRules {
	
	public final static long STANDARD_BOGGLE_TIMELIMIT = 180000;
	
	public final static int STANDARD_BOGGLE_WIDTH = 4;
	public final static int STANDARD_BOGGLE_HEIGHT = 4;
	public final static int STANDARD_MIN_LETTERS = 3;
	public final static int[] STANDARD_POINTS = {0,0,1,1,2,3,5,11};
	public final static HashMap<Character, Double> STANDARD_PROBS;
	
	static {
		STANDARD_PROBS = new HashMap<Character, Double>();
		STANDARD_PROBS.put('a', 9.0d / 96.0d);
		STANDARD_PROBS.put('b', 2.0d / 96.0d);
		STANDARD_PROBS.put('c', 2.0d / 96.0d);
		STANDARD_PROBS.put('d', 3.0d / 96.0d);
		STANDARD_PROBS.put('e', 13.0d / 96.0d);
		STANDARD_PROBS.put('f', 2.0d / 96.0d);
		STANDARD_PROBS.put('g', 2.0d / 96.0d);
		STANDARD_PROBS.put('h', 2.0d / 96.0d);
		STANDARD_PROBS.put('i', 8.0d / 96.0d);
		STANDARD_PROBS.put('j', 1.0d / 96.0d);
		STANDARD_PROBS.put('k', 1.0d / 96.0d);
		STANDARD_PROBS.put('l', 5.0d / 96.0d);
		STANDARD_PROBS.put('m', 3.0d / 96.0d);
		STANDARD_PROBS.put('n', 6.0d / 96.0d);
		STANDARD_PROBS.put('o', 6.0d / 96.0d);
		STANDARD_PROBS.put('p', 2.0d / 96.0d);
		STANDARD_PROBS.put('q', 1.0d / 96.0d);
		STANDARD_PROBS.put('r', 6.0d / 96.0d);
		STANDARD_PROBS.put('s', 6.0d / 96.0d);
		STANDARD_PROBS.put('t', 6.0d / 96.0d);
		STANDARD_PROBS.put('u', 4.0d / 96.0d);
		STANDARD_PROBS.put('v', 2.0d / 96.0d);
		STANDARD_PROBS.put('w', 1.0d / 96.0d);
		STANDARD_PROBS.put('x', 1.0d / 96.0d);
		STANDARD_PROBS.put('y', 1.0d / 96.0d);
		STANDARD_PROBS.put('z', 1.0d / 96.0d);
		
	}
	
	public char  getChar(double randomnumber){
		char c='z' + 1;
		for( c = 'a' -1 ; randomnumber >= 0.0;){
			 c++;
			if(c > 'z') break;
			randomnumber -= probabilities.get(c); 
		}
		if(c >= 'a' && c <= 'z') return c;
		throw new IllegalArgumentException("Random Number higher than 1.0!");
	}

	
	public long timeLimit = STANDARD_BOGGLE_TIMELIMIT;
	public int boggleWidth = STANDARD_BOGGLE_WIDTH;
	public int boggleHeight = STANDARD_BOGGLE_HEIGHT;
	public int minLetters = STANDARD_MIN_LETTERS;
	public int[] points = STANDARD_POINTS;
	public HashMap<Character, Double> probabilities = STANDARD_PROBS;
}
