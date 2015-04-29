package project2;

public class testD {
	public static void main(String[] args) {
		Dictionary d = new Dictionary();
		d.add("applae");
		d.add("appl");
		print(d.suggest("ppae", 5));
	}

	public static void print(String[] s) {
		for (String ss : s)
			System.out.print(ss + " ");
	}
}
