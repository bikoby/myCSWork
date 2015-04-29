/** A class representing a graph. Stores an array of nodes and adjacency list.
 * 
 */
import java.awt.Point;

public class MapGraph {

	private CityNode[] nodes;

	// for each vertex store a linked list of edges;
	private Edge[] adjacencyList;

	private int numNodes = 0;
	private int numEdges = 0;

	public final int EPS_DIST = 5;

	MapGraph(int numNodes) {
		this.nodes = new CityNode[numNodes];
		this.adjacencyList = new Edge[numNodes * numNodes];
		for (int i = 0; i < numNodes * numNodes; i++) {
			this.adjacencyList[i] = new Edge(i, 0);
		}
	}

	/**
	 * Returns a node with index i
	 */
	CityNode getNode(int i) {
		return this.nodes[i];
	}

	/**
	 * Returns the head of the linked list of edges for a vertex with id = i
	 */
	Edge getEdge(int i) {
		return this.adjacencyList[i];
	}

	/**
	 * Adds a node to the graph
	 * 
	 * @param node
	 */
	public void addNode(CityNode node) {
		this.nodes[this.numNodes++] = node;
	}

	public int numNodes() {
		return numNodes;
	}

	/**
	 * Adds the edge to the linked list for this vertexId
	 * 
	 * @param vertexId
	 * @param edge
	 */
	public void addEdge(int nodeId, Edge edge) {
		Edge temp = this.adjacencyList[nodeId];
		while (temp.next() != null) {
			temp = temp.next();
		}
		temp.setNext(edge);
		this.numEdges++;
	}

	/**
	 * Given the location of the click, return the node of the graph at this
	 * location.
	 */
	public CityNode getVertex(Point loc) {
		for (CityNode v : nodes) {
			Point p = v.getLocation();
			if ((Math.abs(loc.x - p.x) < EPS_DIST)
					&& (Math.abs(loc.y - p.y) < EPS_DIST))
				return v;
		}
		return null;
	}

	/**
	 * Returns the array of all edges for drawing: each element in the array
	 * corresponds to one edge and is the array of two Point objects
	 * (corresponding to the locations of the two nodes connected by this edge).
	 */
	public Point[][] getEdges() {
		Point[][] edges = new Point[numEdges][2];
		int p = 0;
		for (int i = 0; i < this.numNodes; i++) {
			Edge temp = this.adjacencyList[i].next();
			while (temp != null) {
				edges[p][0] = this.nodes[i].getLocation();
				edges[p][1] = this.nodes[temp.id()].getLocation();
				temp = temp.next();
				p++;
			}
		}
		return edges;

	}

	/**
	 * Returns the array of nodes as points. Used by MapGraph to draw little
	 * circles at the location of the nodes
	 */
	public Point[] getNodeLocations() {
		Point[] locations = new Point[numNodes];
		for (int i = 0; i < numNodes; i++) {
			locations[i] = this.nodes[i].getLocation();
		}
		return locations;
	}

	/**
	 * Returns the array of cities corresponding to the vertices of this graph
	 * in the array
	 * 
	 * @return
	 */
	public String[] getCities() {
		String[] labels = new String[numNodes];
		for (int i = 0; i < numNodes; i++) {
			labels[i] = this.nodes[i].getCity();
		}
		return labels;
	}

} // class MapGraph