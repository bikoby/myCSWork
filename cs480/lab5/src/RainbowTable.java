import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class RainbowTable {

	private final int length;
	// HashMap <hash, pwd>
	private ArrayList<HashMap<String, String>> tables;
	private final int numOfTable = 10;

	public RainbowTable(int length) {
		this.tables = new ArrayList<>();
		for (int i = 0; i < this.numOfTable; i++) {
			HashMap<String, String> temp = new HashMap<>();
			this.tables.add(temp);
		}
		this.length = length;
	}

	public RainbowTable(String fileName) throws FileNotFoundException {
		Scanner scan = new Scanner(new File(fileName));
		this.length = Integer.parseInt(scan.nextLine());
		this.tables = new ArrayList<>();
		for (int i = 0; i < this.numOfTable; i++) {
			HashMap<String, String> temp = new HashMap<>();
			this.tables.add(temp);
		}
		while (scan.hasNextLine()) {
			String[] line = scan.nextLine().split("\\s");
			add(line[1], line[0]);
		}
		scan.close();
	}

	public void add(String pwd, String hash) {
		HashMap<String, String> table = this.tables.get(pwd.length() - 6);
		table.put(hash, pwd);
	}

	public void add(String pwd) {
		if (pwd.length() >= 6 && pwd.length() < 16) {
			HashMap<String, String> table = this.tables.get(pwd.length() - 6);
			if (table.isEmpty()) {
				String temp = hash(pwd);
				for (int i = 0; i < this.length; i++) {
					temp = hash(temp.substring(0, pwd.length()));
				}
				table.put(temp, pwd);
			} else {
				// check the hash in the table
				boolean init = false;
				String temp = hash(pwd);
				for (int i = 0; i < this.length; i++) {
					if (table.containsKey(temp)
							&& find(table, pwd.length(), temp) != null) {
						init = true;
						break;
					}
					temp = hash(temp.substring(0, pwd.length()));
				}
				// if it does not contain this pwd, add pwd and the hash int to
				// the table
				if (!init) {
					table.put(temp, pwd);
				}
				// if it contains, do nothing
			}
		}

	}

	public String find(String hash) {
		for (int len = 6; len < this.numOfTable + 6; len++) {
			HashMap<String, String> table = this.tables.get(len - 6);
			String result = find(table, len, hash);
			if (result != null) {
				return result;
			}
		}
		return null;
	}

	private String find(HashMap<String, String> table, int len, String hash) {
		String temp = hash;
		if (!table.isEmpty()) {
			boolean init = false;
			for (int i = 0; i < this.length; i++) {
				if (table.containsKey(temp)) {
					init = true;
					break;
				}
				temp = hash(temp.substring(0, len));
			}

			// if it contains this pwd, then look it up
			if (init) {
				// get the start plain text
				String plaintext = table.get(temp);
				String temphash = hash(plaintext);
				if (temphash.equals(hash)) {
					return plaintext;
				}
				for (int i = 0; i < this.length; i++) {
					plaintext = temphash.substring(0, len);
					temphash = hash(plaintext);
					if (temphash.equals(hash)) {
						return plaintext;
					}
				}
			}
		}
		return null;
	}

	public void save(String fileName) throws IOException {
		Charset charset = Charset.forName("UTF8");
		try (BufferedWriter writer = Files.newBufferedWriter(
				Paths.get(fileName), charset)) {
			writer.write(String.valueOf(length));
			writer.newLine();
			for (HashMap<String, String> table : this.tables) {
				for (String hash : table.keySet()) {
					writer.write(hash + " " + table.get(hash));
					writer.newLine();
				}
			}

		}
	}

	// MD5 Hashing
	public static String hash(String pwd) {
		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("md5");
			byte[] bytes = digest.digest(pwd.getBytes());

			StringBuilder builder = new StringBuilder();
			for (byte b : bytes) {

				int num = (b & 0xff);

				String str = Integer.toHexString(num);

				if (str.length() == 1) {
					builder.append(0);
				}
				builder.append(str);

			}
			return builder.toString();
		} catch (NoSuchAlgorithmException e) {
			System.out.println("Program Error");
		}
		return null;
	}

}
