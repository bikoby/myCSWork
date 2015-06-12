package project2;

import java.io.BufferedReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Dictionary {

	private DNode root;

	public Dictionary() {
		this.root = null;
	}

	public Dictionary(String filename) {
		Charset charset = Charset.forName("UTF8");
		Path file = Paths.get("words_ospd.txt");
		try (BufferedReader reader = Files.newBufferedReader(file, charset)) {
			String line = null;
			while ((line = reader.readLine()) != null) {
				for (String word : line.split(" ")) {
					if (!word.equals("")) {
						add(word);
					}
				}
			}
			reader.close();
		} catch (Exception e) {
			System.out.println("Connot open the file:" + file.toString());
		}
	}

	public void add(String word) {
		root = add(word, root);
	}

	private DNode add(String word, DNode root) {
		if (root == null) {
			return new DNode(word, true);
		} else if (root.getLetter().equals(word)) {
			if (root.Value() == false) {
				root.setValue(true);
			}
			return root;
		} else if (word.startsWith(root.getLetter())) {
			int len = root.getLetter().length();
			root.setChild(word.charAt(len),
					add(word.substring(len), root.getChild(word.charAt(len))));
			return root;
		} else if (root.getLetter().startsWith(word)) {
			int len = word.length();
			root.setChild(
					root.getLetter().charAt(len),
					add(root.getLetter().substring(len),
							root.getChild(root.getLetter().charAt(len))));
			root.setLetter(word);
			return root;
		} else {
			System.out.println(root.getLetter() + " " + word);
			DNode pre = new DNode(getLongestPre(root.getLetter(), word), false);
			int len = pre.getLetter().length();
			root.setLetter(root.getLetter().substring(len));
			DNode suf2 = new DNode(word.substring(len), true);
			pre.setChild(root.getLetter().charAt(0), root);
			pre.setChild(suf2.getLetter().charAt(0), suf2);
			return pre;
		}
	}

	private String getLongestPre(String letter, String word) {
		String pre = "";
		int num = Math.min(letter.length(), word.length());
		for (int i = 0; i < num; i++) {
			if (letter.charAt(i) == word.charAt(i))
				pre += letter.charAt(i);
			else
				break;
		}
		return pre;
	}

	public void print() {
		printWordsR(root, "");
		System.out.println();
	}

	private void printWordsR(DNode root, String pre) {
		String word = pre + root.getLetter();
		if (root.Value()) {
			System.out.print(word + " ");
		}
		for (int i = 0; i < 26; i++) {
			DNode child = root.getChild(i);
			if (child != null)
				printWordsR(child, word);

		}
	}

	public void printTree() {
		printStructureR(root, 0);
	}

	private void printStructureR(DNode root, int num) {
		for (int i = 0; i < num; i++)
			System.out.print("  ");
		System.out.print(root.getLetter());
		if (root.Value())
			System.out.print("<T>");
		if (!root.getLetter().isEmpty())
			System.out.println();
		for (int i = 0; i < 26; i++) {
			DNode child = root.getChild(i);
			if (child != null) {
				if (root.getLetter().isEmpty())
					printStructureR(child, num);
				else
					printStructureR(child, num + 1);
			}
		}
	}

	public boolean check(String word) {
		return check(root, word);
	}

	private boolean check(DNode root, String word) {
		if (root == null || word.isEmpty())
			return false;
		if (word.equals(root.getLetter()))
			return root.Value();
		if (word.startsWith(root.getLetter())) {
			int len = root.getLetter().length();
			return check(root.getChild(word.charAt(len)), word.substring(len));
		}
		return false;
	}

	public boolean checkPrefix(String prefix) {
		return checkPrefix(root, prefix);
	}

	private boolean checkPrefix(DNode root, String prefix) {
		if (root == null)
			return false;
		if (root.getLetter().startsWith(prefix))
			return true;
		if (prefix.startsWith(root.getLetter())) {
			int len = root.getLetter().length();
			return checkPrefix(root.getChild(prefix.charAt(len)),
					prefix.substring(len));
		}
		return false;
	}

	private int num;
	private String[] s;

	public String[] suggest(String word, int numSuggestions) {
		num = 0;
		if (check(word)) {
			s = new String[1];
			s[0] = word;
		} else {
			s = new String[numSuggestions];
			// give suggestions with same letters
			wordSuggest(word, 0);
			if (!isFull(s))
				// replace one letter
				repSuggest(word, 1);
		}
		return s;
	}

	private void getWordByPre(DNode root, String word, String pre) {
		if (root == null)
			return;
		if (root.getLetter().startsWith(word))
			getChild(root, pre);
		else if (word.startsWith(root.getLetter())) {
			int len = root.getLetter().length();
			getWordByPre(root.getChild(word.charAt(len)), word.substring(len),
					pre + root.getLetter());
			if (!root.getLetter().isEmpty())
				getChild(root, pre);
		}
	}

	private void getChild(DNode root, String pre) {
		pre = pre + root.getLetter();
		if (isFull(s))
			return;
		if (root.Value())
			if (!exist(pre))
				s[num++] = pre;
		for (int i = 0; i < 26; i++) {
			DNode child = root.getChild(i);
			if (child != null)
				getChild(child, pre);
		}

	}

	// change the letters' sequence to get suggestions
	private void wordSuggest(String word, int index) {
		// get "abcd" and "abdc"
		if (index == word.length() - 2) {
			String newWord = word.substring(0, word.length() - 2);
			String w1 = newWord + word.charAt(word.length() - 2)
					+ word.charAt(word.length() - 1);
			String w2 = newWord + word.charAt(word.length() - 1)
					+ word.charAt(word.length() - 2);
			if (isFull(s))
				return;
			if (check(w1))
				if (!exist(w1)) {
					s[num++] = w1;
				}
			getWordByPre(root, w1, "");
			if (isFull(s))
				return;
			if (check(w2))
				if (!exist(w2)) {
					s[num++] = w2;
				}
			getWordByPre(root, w2, "");
		} else {
			// shit the letters
			for (int i = 0; i < word.length() - index; i++) {
				wordSuggest(word, index + 1);
				word = shift(word, index);
			}
		}
	}

	private boolean exist(String string) {
		if (num == 0)
			return false;
		for (String word : s)
			if (string.equals(word))
				return true;
		return false;
	}

	// shift "abcd" -> "bcda" if index is 0
	private String shift(String word, int index) {
		String newWord = word.substring(0, index);
		for (int i = index; i < word.length() - 1; i++) {
			newWord += word.charAt(i + 1);
		}
		newWord += word.charAt(index);
		return newWord;
	}

	// replace letter to get suggestions
	private void repSuggest(String word, int numReplace) {
		char[] tempArray;
		String newWord;
		for (int i = word.length() - 1; i >= 0; i--) {
			for (int j = 0; j < 26; j++) {
				tempArray = word.toCharArray();
				tempArray[i] = (char) ((int) 'a' + j);
				newWord = "";
				for (int k = 0; k < tempArray.length; k++)
					newWord += tempArray[k];
				wordSuggest(newWord, 0);
			}
		}
	}

	private boolean isFull(String[] s) {
		return num == s.length;
	}
}