package bogglebot;

import java.util.Comparator;

// Sorts words in increasing order of length

public class CompareLength implements Comparator<Word> {
	public int compare(Word a, Word b) {
		// don't keep duplicate words
		if (a.word.equals(b.word)) {
			return 0;
		}
		if (a.word.length() == b.word.length()) {
			return a.word.compareTo(b.word);
		}
		return a.word.length() - b.word.length();
	}
}
