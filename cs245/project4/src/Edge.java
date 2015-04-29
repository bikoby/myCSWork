import java.awt.Color;

/**
 * Edge class represents a single node in the linked list of edges for a vertex.
 * 
 */

class Edge {

	private Edge next;
	private int id;
	private int weight;
	private Color col;

	Edge(int id, int weight) {
		this.id = id;
		this.weight = weight;
		this.col = Color.black;
	}

	public void setNext(Edge next) {
		this.next = next;
	}

	public Edge next() {
		return this.next;
	}

	public int id() {
		return this.id;
	}

	public int weight() {
		return this.weight;
	}

}