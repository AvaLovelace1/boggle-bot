package bogglebot;

import java.util.ArrayList;

import org.sikuli.script.*;

public class Word implements Comparable<Word> {

	private static final int[] points = { 1, 3, 3, 2, 1, 4, 2, 4, 1, 8, 5, 1, 3, 1, 1, 3, 10, 1, 1, 1, 1, 4, 4, 8, 4,
			10 };

	public String word;
	public ArrayList<Region> coords;
	public int seed;

	public Word(String word, ArrayList<Region> coords) {
		this.word = word;
		this.coords = new ArrayList<Region>();
		this.coords.addAll(coords);
		seed = (int) (Math.random() * 1e9);
	}

	public int getScore() {
		int score = 0;
		for (int i = 0; i < word.length(); i++) {
			score += points[word.charAt(i) - 'A'];
		}
		score += 5 * word.length();
		return score;
	}

	public int compareTo(Word w) {
		if (w.getScore() == this.getScore()) {
			return this.word.compareTo(w.word); 
		} else {
			return w.getScore() - this.getScore();
		}
	}
}
