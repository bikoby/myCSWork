package project3;

public class FlightNode {
	private FlightNode up;
	private FlightNode down;
	private FlightNode prev;
	private FlightNode next;
	private FlightKey key;
	private FlightData data;

	public FlightNode(FlightKey key, FlightData data) {
		this.key = key;
		this.data = data;
	}

	public FlightNode getUp() {
		return up;
	}

	public void setUp(FlightNode up) {
		this.up = up;
	}

	public FlightNode getDown() {
		return down;
	}

	public void setDown(FlightNode down) {
		this.down = down;
	}

	public FlightNode getPre() {
		return prev;
	}

	public void setPre(FlightNode prev) {
		this.prev = prev;
	}

	public FlightNode getNext() {
		return next;
	}

	public void setNext(FlightNode next) {
		this.next = next;
	}

	public FlightKey getKey() {
		return key;
	}

	public FlightData getData() {
		return data;
	}

}
