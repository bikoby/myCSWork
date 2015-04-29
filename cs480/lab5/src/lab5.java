import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class lab5 {

	public static void main(String[] args) {
		try {
			RainbowTable rainbowTable = new RainbowTable("rainbowTable");
			if (args.length == 0) {
				while (true) {
					ArrayList<String> hashes = readHahFile("");
					find(rainbowTable, hashes);
					System.out
							.println("Do you want to check another file?(y/n)");
					Scanner scan = new Scanner(System.in);
					String next = scan.nextLine();
					if (!next.toLowerCase().equals("y"))
						break;
				}
			} else {
				for (String filename : args) {
					ArrayList<String> hashes = readHahFile(filename);
					find(rainbowTable, hashes);
				}
			}
		} catch (FileNotFoundException e) {
			System.out.println("Fail to read rainbowTable");
		}
	}

	private static void find(RainbowTable r, ArrayList<String> hashes) {
		int i = 0;
		for (String hash : hashes) {
			String pwd = r.find(hash);
			if (pwd != null) {
				System.out.println(i + " " + pwd + " " + hash);
				i++;
			}
		}

	}

	public static ArrayList<String> readHahFile(String filename) {
		ArrayList<String> array = new ArrayList<>();
		if (filename.isEmpty()) {
			System.out.println("Input the hash file name:");
			Scanner scan = new Scanner(System.in);
			filename = scan.nextLine();
		}
		try {
			Scanner scan = new Scanner(new File(filename));
			while (scan.hasNext()) {
				array.add(scan.next());
			}
			scan.close();
		} catch (FileNotFoundException e) {
			System.out
					.println("Cannot open file, please put the file into the project diretory");
		}
		return array;
	}
}
