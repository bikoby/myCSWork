package project3;

public class testList {
	public static void main(String[] args) {
		FlightList fl = new FlightList("flights");
		FlightKey key2 = new FlightKey("BOS", "PVD", "01/03/2014", "09:30");

		// System.out.println(fl.find(key2));
		// System.out.println();

		// fl.print();
		// System.out.println();
		// //
		// fl.remove(key2);
		// fl.print();

		for (FlightNode k : fl.suggestFlights(key2)) {
			System.out.println(k.getKey());
		}
	}
}
