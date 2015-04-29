package project2;

public class DNode {
	private DNode[] child;
	private String letter;
	private boolean value;

	public DNode(String letter, boolean value) {
		this.letter = letter;
		this.child = new DNode[26];
		this.value = value;
	}

	public void setChild(char c, DNode child) {
		this.child[(int) c - (int) 'a'] = child;
	}

	public DNode getChild(char c) {
		return child[(int) c - (int) 'a'];
	}

	public DNode getChild(int index) {
		return child[index];
	}

	public String getLetter() {
		return letter;
	}

	public void setLetter(String letter) {
		this.letter = letter;
	}

	public boolean Value() {
		return value;
	}

	public void setValue(boolean value) {
		this.value = value;
	}

	public String toString() {
		return this.letter + "(" + this.value + ")";
	}

}
