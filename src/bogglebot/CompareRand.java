package bogglebot;

import java.util.Comparator;

// If you're trolling your friends, maybe you want to sort the words
// randomly to disguise the fact you're using a bot :P

public class CompareRand implements Comparator<Word> {
	public int compare(Word a, Word b) {
		// don't keep duplicate words
		if (a.word.equals(b.word)) {
			return 0;
		}
		return b.seed - a.seed;
	}
}
