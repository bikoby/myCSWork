import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Dijkstra {

	// FILL IN CODE: you need to store Dijkstra's table
	private tableEntry[] dijkstra;
	private HashTable table;
	private int sourceVertex = -1; // store the id of the source vertex
	private MapGraph graph;

	Dijkstra(String filename) {
		loadGraph(filename);
	}

	public MapGraph getGraph() {
		return graph;
	}

	/**
	 * Compute all the shortest paths from the source vertex to all the other
	 * vertices in the graph; This function is called from GUIApp, when the user
	 * clicks on the city.
	 */
	public void computePaths(CityNode vSource) {
		PriorityQueue p = new PriorityQueue(this.dijkstra.length);

		this.sourceVertex = this.table.find(vSource.getCity());
		for (int i = 0; i < this.dijkstra.length; i++) {
			this.dijkstra[i] = new tableEntry();
		}
		this.dijkstra[this.sourceVertex].distance = 0;
		p.insert(this.table.find(vSource.getCity()), 0);
		int minID = p.remove_min();
		while (minID != -1) {
			this.dijkstra[minID].visited = true;
			for (Edge temp = this.graph.getEdge(minID).next(); temp != null; temp = temp
					.next()) {
				tableEntry tempE = this.dijkstra[temp.id()];
				if (!tempE.visited) {
					if (tempE.distance > this.dijkstra[minID].distance
							+ temp.weight()) {

						if (tempE.distance == Integer.MAX_VALUE) {

							tempE.distance = this.dijkstra[minID].distance
									+ temp.weight();
							tempE.id = minID;
							p.insert(temp.id(), tempE.distance);
						} else {
							tempE.distance = this.dijkstra[minID].distance
									+ temp.weight();
							tempE.id = minID;
							p.reduce_key(temp.id(), tempE.distance);
						}
					}
				}
			}
			minID = p.remove_min();

		}
	}

	/**
	 * Returns the shortest path between the source vertex and this vertex.
	 * Returns the array of node id-s on the path
	 */
	public ArrayList<Integer> shortestPath(CityNode vTarget) {
		ArrayList<Integer> ids = new ArrayList<>();
		int tID = this.table.find(vTarget.getCity());
		int tempID = this.dijkstra[tID].id;

		ids.add(this.table.find(vTarget.getCity()));
		while (tempID != -1) {
			ids.add(tempID);
			tempID = this.dijkstra[tempID].id;
		}

		ArrayList<Integer> result = new ArrayList<>();
		for (int i = ids.size() - 1; i >= 0; i--) {
			result.add(ids.get(i));
		}
		if (ids.size() > 1) {
			return result;
		}
		return null;
	}

	/**
	 * Loads graph info from the text file into MapGraph graph
	 * 
	 * @param filename
	 */
	public void loadGraph(String filename) {
		try {
			Scanner scan = new Scanner(new File(filename));
			String line = scan.nextLine();
			if (line.equals("NODES")) {
				line = scan.nextLine();
				int size = Integer.parseInt(line);
				this.graph = new MapGraph(size);
				this.table = new HashTable(size);
				this.dijkstra = new tableEntry[size];
				// read Nodes
				String[] temp;
				CityNode node;
				int id = 0;
				while (scan.hasNextLine()) {
					line = scan.nextLine();
					if (line.equals("ARCS")) {
						break;
					}
					temp = line.split("\\s");
					node = new CityNode(temp[0], Double.parseDouble(temp[1]),
							Double.parseDouble(temp[2]));
					this.table.add(temp[0], id++);
					this.graph.addNode(node);
				}
				// read Edges
				int id1, id2;
				String city1, city2;
				while (scan.hasNextLine()) {
					line = scan.nextLine();
					temp = line.split("\\s");
					city1 = temp[0];
					city2 = temp[1];
					id1 = this.table.find(city1);
					id2 = this.table.find(city2);
					Edge edge1 = new Edge(id1, Integer.parseInt(temp[2]));
					Edge edge2 = new Edge(id2, Integer.parseInt(temp[2]));
					this.graph.addEdge(id1, edge2);
					this.graph.addEdge(id2, edge1);
				}
			}
		} catch (FileNotFoundException e) {
			System.out.println("Cannot open USA.txt");
		}
	}

	public class tableEntry {

		public int distance;
		public int id;
		public boolean visited;

		tableEntry() {
			this.distance = Integer.MAX_VALUE;
			this.id = -1;
			this.visited = false;
		}

	}

	public static void main(String[] args) {

		// Create an instance of the Dijkstra class
		// The parameter is the name of the file
		Dijkstra dijkstra = new Dijkstra(args[0]);
		// create the GUI window with the panel
		GUIApp app = new GUIApp(dijkstra);
	}
}
