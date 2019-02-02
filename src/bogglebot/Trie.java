package bogglebot;

import java.util.HashMap;

// Trie data structure for storing words

public class Trie {

	private class Node {
		public boolean isWord;
		public HashMap<Character, Node> children;

		Node() {
			isWord = false;
			children = new HashMap<Character, Node>();
		}
	}

	private Node root;

	public Trie() {
		root = new Node();
	}

	public void insert(String s) {
		Node n = root;
		for (int i = 0; i < s.length(); i++) {
			if (!n.children.containsKey(s.charAt(i))) {
				n.children.put(s.charAt(i), new Node());
			}
			n = n.children.get(s.charAt(i));
		}
		n.isWord = true;
	}

	public boolean containsPrefix(String s) {
		Node n = root;
		for (int i = 0; i < s.length(); i++) {
			if (!n.children.containsKey(s.charAt(i))) {
				return false;
			}
			n = n.children.get(s.charAt(i));
		}
		return true;
	}

	public boolean contains(String s) {
		Node n = root;
		for (int i = 0; i < s.length(); i++) {
			if (!n.children.containsKey(s.charAt(i))) {
				return false;
			}
			n = n.children.get(s.charAt(i));
		}
		return n.isWord;
	}

}
