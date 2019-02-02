package bogglebot;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.TreeSet;

import org.sikuli.basics.Settings;
import org.sikuli.script.FindFailed;
import org.sikuli.script.Match;
import org.sikuli.script.Pattern;
import org.sikuli.script.Region;
import org.sikuli.script.Screen;

public class BoggleBot {

	private static final int BOARD_SIZE = 4;
	// stuff for calibrating region location
	private static final int TILE_SIZE = 147 / 2, DIST_TILES = 169 / 2;
	private static final int REF_X = 0 / 2, REF_Y = 384 / 2;
	private static final File DICT_FILE = new File("resources/english3.txt"); // Dictionary file
	private static final Pattern PAUSE_FILE = new Pattern("resources/pause"); // arrow in top left corner
	private static Pattern[] LETTER_FILES = new Pattern[26];
	private static final Screen SCREEN = new Screen(1);
	private static final Trie DICTIONARY = LoadDictionary();
	private static Region[][] TILES = new Region[BOARD_SIZE][BOARD_SIZE];
	private static final double DELAY_VALUE = 0;

	// Dictionary
	private static Trie LoadDictionary() {
		Scanner sc;
		try {
			sc = new Scanner(DICT_FILE);
			Trie t = new Trie();
			while (sc.hasNextLine()) {
				t.insert(sc.nextLine().toUpperCase());
			}
			sc.close();
			return t;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	public BoggleBot() {

		Settings.DelayBeforeDrag = DELAY_VALUE;
		Settings.DelayBeforeDrop = DELAY_VALUE;
		Settings.MoveMouseDelay = (float) DELAY_VALUE;
		Settings.DelayValue = DELAY_VALUE;
		// Settings.MinSimilarity = 0.9;

		// Initialize letter files
		for (int i = 0; i < 26; i++) {
			LETTER_FILES[i] = new Pattern("resources/letters/" + (char) (i + 'A'));
		}
		for (Pattern p : LETTER_FILES) {
			p.similar((float) 0.8);
		}
		PAUSE_FILE.similar((float) 0.9);

		Region pause;
		try {

			// define where the 16 tiles of the board are relative to the pause button
			pause = SCREEN.find(PAUSE_FILE);
			System.out.println("Reference point found.");
			TILES[0][0] = new Region(pause.x + REF_X, pause.y + REF_Y, TILE_SIZE, TILE_SIZE);
			for (int j = 1; j < BOARD_SIZE; j++) {
				TILES[0][j] = new Region(TILES[0][j - 1].x + DIST_TILES, TILES[0][0].y, TILE_SIZE, TILE_SIZE);
			}
			for (int i = 1; i < BOARD_SIZE; i++) {
				for (int j = 0; j < BOARD_SIZE; j++) {
					TILES[i][j] = new Region(TILES[0][j].x, TILES[i - 1][0].y + DIST_TILES, TILE_SIZE, TILE_SIZE);
				}
			}
			System.out.println("Regions defined.");

		} catch (FindFailed e) {
			e.printStackTrace();
		}
	}

	// read in board through image recognition
	private char[][] readBoard(Region[][] tiles) {

		char[][] board = new char[BOARD_SIZE][BOARD_SIZE];
		double highScore;
		double score;
		int best;

		System.out.println("Reading board...");
		for (int i = 0; i < BOARD_SIZE; i++) {
			for (int j = 0; j < BOARD_SIZE; j++) {
				highScore = 0;
				score = 0;
				best = 0;
				boolean notFound = true;
				for (int k = 0; k < LETTER_FILES.length; k++) {
					Match m = tiles[i][j].exists(LETTER_FILES[k], 0);
					if (m != null) {
						notFound = false;
						score = m.getScore();
					}
					if (score > highScore) {
						best = k;
						highScore = score;
					}
				}
				if (notFound) {
					return null;
				}
				board[i][j] = (char) ('A' + best);
			}
		}

		System.out.println();
		for (char[] cs : board) {
			for (char c : cs) {
				System.out.print(c + " ");
			}
			System.out.println();
		}

		return board;
	}

	private TreeSet<Word> findWord(char board[][], boolean visited[][], int row, int col, String s,
			ArrayList<Region> coords, Region[][] tiles) {

		TreeSet<Word> words = new TreeSet<Word>();

		// Mark letter as visited
		visited[row][col] = true;
		// Add letter in cell to s
		s += board[row][col];

		if (DICTIONARY.containsPrefix(s)) {

			Region xy = new Region(tiles[row][col]);
			// Store coordinates of letter
			coords.add(xy);

			Word w = new Word(s, coords);

			// Check if s is a word
			if (DICTIONARY.contains(w.word)) {
				words.add(w);
			}

			// recursively search the 8 letters around this letter
			for (int i = row - 1; i < BOARD_SIZE && i <= row + 1; i++) {
				for (int j = col - 1; j < BOARD_SIZE && j <= col + 1; j++) {
					if (i >= 0 && j >= 0 && !visited[i][j]) {
						words.addAll(findWord(board, visited, i, j, s, coords, tiles));
					}
				}
			}

			coords.remove(coords.size() - 1);
		}

		// remove current letter from s & coords
		s = s.length() == 1 ? "" : s.substring(0, s.length() - 2);
		visited[row][col] = false;

		return words;
	}

	private TreeSet<Word> findAllWords(char[][] board, Region[][] tiles) {

		// Can set this to whatever comparator you like. Since you get 1 fireball per
		// word perhaps it's better to prioritize shorter words
		TreeSet<Word> words = new TreeSet<Word>(new CompareScore());
		boolean[][] visited = new boolean[BOARD_SIZE][BOARD_SIZE];

		for (int i = 0; i < BOARD_SIZE; i++) {
			for (int j = 0; j < BOARD_SIZE; j++) {
				visited[i][j] = false;
			}
		}

		String s = "";
		ArrayList<Region> coords = new ArrayList<Region>();

		// Find all words for each letter in board
		for (int i = 0; i < BOARD_SIZE; i++) {
			for (int j = 0; j < BOARD_SIZE; j++) {
				words.addAll(findWord(board, visited, i, j, s, coords, tiles));
			}
		}
		return words;
	}

	private void playWords(Region[][] tiles) {
		// get an array of chars from the regions
		char[][] board = readBoard(tiles);
		if (board == null) {
			return;
		}
		TreeSet<Word> words = findAllWords(board, TILES);
		for (Word w : words) {
			try {
				for (int i = 0; i < w.coords.size() - 1; i++) {
					SCREEN.drag(w.coords.get(i));
				}
				SCREEN.dropAt(w.coords.get(w.coords.size() - 1));
			} catch (FindFailed e) {
				e.printStackTrace();
			}
		}
	}

	public void run() {
		playWords(TILES);
	}
}
