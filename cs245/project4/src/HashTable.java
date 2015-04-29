public class HashTable {

	private Node[] nodeList;
	private int size;

	HashTable(int size) {
		this.size = size;
		this.nodeList = new Node[size];
		for (int i = 0; i < size; i++) {
			this.nodeList[i] = new Node("", i);
		}
	}

	public void add(String city, int id) {
		Node node = new Node(city, id);
		Node temp = this.nodeList[(int) hash(city, this.size)];
		while (temp.next != null) {
			temp = temp.next;
		}
		temp.setNext(node);

	}

	public int find(String city) {

		int id = -1;
		Node temp = this.nodeList[(int) hash(city, this.size)].next;
		while (temp != null) {
			if (temp.city().equals(city)) {
				id = temp.id();
				break;
			}
			temp = temp.next;
		}
		return id;
	}

	long hash(String key, int tablesize) {
		long h = 0;
		int i;
		for (i = 0; i < key.length(); i++)
			h = (h << 4) + (int) key.charAt(i);
		return h % tablesize;
	}

	private class Node {

		private String city;
		private int id;
		private Node next;

		Node(String city, int id) {
			this.city = city;
			this.id = id;
			this.next = null;
		}

		public void setNext(Node next) {
			this.next = next;
		}

		public Node next() {
			return this.next;
		}

		public int id() {
			return this.id;
		}

		public String city() {
			return this.city;
		}

	}
}
