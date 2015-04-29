package project3;

public class FlightData {
	private String fliNum;
	private int price;

	public FlightData(String fliNum, int price) {
		this.fliNum = fliNum;
		this.price = price;
	}

	public String toString() {
		return "(" + fliNum + " " + price + ")";
	}
}
