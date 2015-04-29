package lab3;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class lab3 {

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

	private static HashMap<String, String> readDictionary() {
		HashMap<String, String> map = new HashMap<String, String>();
		String filename = "dictionary";
		try {
			Scanner scan = new Scanner(new File(filename));
			while (scan.hasNextLine()) {
				String[] line = scan.nextLine().split(" ");
				map.put(line[1], line[0]);
			}
			scan.close();
		} catch (FileNotFoundException e) {
			System.out
					.println("Cannot open file, please put the file into the project diretory");
		}
		return map;
	}

	private static void compareHash(ArrayList<String> hashes,
			HashMap<String, String> database) {
		int i = 0;
		for (String hash : hashes) {
			for (String temphash : database.keySet()) {
				if (hash.equals(temphash)) {
					System.out.println(i + " " + database.get(temphash) + " "
							+ temphash);
					i++;
					break;
				}
			}
		}
	}

	public static void main(String[] args) {
		HashMap<String, String> database = readDictionary();
		if (args.length == 0) {
			while (true) {
				ArrayList<String> hashes = readHahFile("");
				compareHash(hashes, database);
				System.out.println("Do you want to check another file?(y/n)");
				Scanner scan = new Scanner(System.in);
				String next = scan.nextLine();
				if (!next.toLowerCase().equals("y"))
					break;
			}
		} else {
			for (String filename : args) {
				ArrayList<String> hashes = readHahFile(filename);
				compareHash(hashes, database);
			}
		}

	}
}
