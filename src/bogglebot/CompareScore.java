package bogglebot;

import java.util.Comparator;

// Sorts words in decreasing order of score (doesn't count bonuses, yet)

public class CompareScore implements Comparator<Word> {
	public int compare(Word a, Word b) {
		// don't keep duplicate words
		if (a.word.equals(b.word)) {
			return 0;
		}
		// 2 words with same score are not the same word
		if (b.getScore() == a.getScore()) {
			return a.word.compareTo(b.word);
		}
		return b.getScore() - a.getScore();
	}
}
	