package project1;

public class Node implements ListElem {
	private Node[] Next;
	private Comparable[] Keys;
	private Node[] Prev;
	private Object Data;

	public Node(Comparable[] keys, int length, Object data) {
		this.Next = new Node[length];
		this.Keys = keys;
		this.Prev = new Node[length];
		this.Data = data;
	}

	public Node getNext(int index) {
		return Next[index];
	}

	public Comparable getKey(int index) {
		return Keys[index];
	}

	public Node getPrev(int index) {
		return Prev[index];
	}

	public void setNext(int index, Node n) {
		Next[index] = n;
	}

	public void setKey(int index, Comparable k) {
		Keys[index] = k;
	}

	public void setPrev(int index, Node n) {
		Prev[index] = n;
	}

	public Object getObject() {
		return Data;
	}

	@Override
	public int numKeys() {
		return Keys.length;
	}

	@Override
	public Comparable key(int index) {
		return Keys[index];
	}

	@Override
	public Object data() {
		return Data;
	}

	public String toString() {
		String s = "";
		for (int i = 0; i < Keys.length; i++) {
			s += Keys[i].toString();
		}
		return s;
	}
}
