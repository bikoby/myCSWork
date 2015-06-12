import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class tableCreate {
	public static void main(String[] args) {
		RainbowTable r = new RainbowTable(100);
		ArrayList<String> pwds = readHahFile("pwd");
		String temp;
		for (String pwd : pwds) {
			r.add(pwd);
			// add 1 number
			for (int i = 0; i < 10; i++) {
				temp = pwd + i;
				r.add(temp);
			}
			// replace 1 letter with number
			for (int i = 0; i < pwd.length(); i++) {
				for (int j = 0; j < 10; j++) {
					temp = pwd.substring(0, i) + j + pwd.substring(i + 1);
					r.add(temp);
				}
			}
			temp = "";
			// change 1 upper case letter
			for (int i = 0; i < pwd.length(); i++) {
				temp = pwd.substring(0, i)
						+ pwd.substring(i, i + 1).toUpperCase()
						+ pwd.substring(i + 1);
				r.add(temp);
			}
		}
		try {
			r.save("rainbowTable");
			System.out.println("Done saving!!");
		} catch (IOException e) {
			System.out.println("Fail to create rainbowTable");
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
