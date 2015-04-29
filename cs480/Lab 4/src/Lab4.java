import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;

public class Lab4 {

	public static class pwdManager {
		private ArrayList<String> pwds;
		private HashMap<String, PwdPair> pairs;
		private String password = "password0";

		public pwdManager() {
			pwds = new ArrayList<String>();
			pairs = new HashMap<String, PwdPair>();
			try {
				Scanner scan = new Scanner(new File("pwd"));
				String pwd;
				while (scan.hasNextLine()) {
					pwd = scan.nextLine();
					pwds.add(pwd);
				}
				scan.close();
			} catch (FileNotFoundException e) {
				System.out.println("Fail to open pwd file");
			}
		}

		public pwdManager(String file) throws FileNotFoundException {
			pwds = new ArrayList<String>();
			pairs = new HashMap<String, PwdPair>();
			try {
				Scanner scan = new Scanner(new File("pwd"));
				String pwd;
				while (scan.hasNextLine()) {
					pwd = scan.nextLine();
					pwds.add(pwd);
				}
				scan.close();
			} catch (FileNotFoundException e) {
				System.out.println("Fail to open pwd file");
			}
			Scanner scan = new Scanner(new File(file));
			String[] pair = new String[3];
			password = scan.nextLine();
			while (scan.hasNextLine()) {
				pair = scan.nextLine().split(" ");
				pairs.put(pair[0], new PwdPair(pair[0], pair[1], pair[2]));
			}
			scan.close();
		}

		public String pwdGenearte() {
			String pwd = "";
			Random r = new Random();
			int f, s;
			do {
				f = r.nextInt(pwds.size());
				s = r.nextInt(pwds.size());
			} while (f == s);
			pwd = pwds.get(f) + pwds.get(s) + r.nextInt(10);
			return pwd;
		}

		public void store(String website, String pwd) {
			String web = hash(website);
			String key = website.replaceAll("\\W", "");
			Random r = new Random();
			int num = r.nextInt(100) * key.length();
			String number = hash(String.valueOf(num));
			String password = "" + (char) (num % 94 + (int) '!');
			int asc;
			int pre = num % 26;
			for (int i = 0; i < pwd.length(); i++) {
				asc = pre + (int) pwd.charAt(i)
						+ (int) key.charAt(i % key.length()) - 2 * (char) 'a'
						+ num % 16;
				pre = (int) pwd.charAt(i) - 97;
				password += (char) (asc + (int) '!');
			}
			pairs.put(web, new PwdPair(web, number, password));
			Charset charset = Charset.forName("UTF8");
			try (BufferedWriter writer = Files.newBufferedWriter(
					Paths.get("MyPassword.txt"), charset)) {
				writer.write(this.password);
				writer.newLine();
				for (PwdPair pp : pairs.values()) {
					writer.write(pp.web + " " + pp.key + " " + pp.pwd);
					writer.newLine();
				}
			} catch (Exception e) {
				System.out.println("Fail to wirte File");
			}

		}

		public String retrieval(String website) {
			String password = "";
			String web = hash(website);
			if (!pairs.containsKey(web)) {
				password = "(No password store for this website)";
			} else {
				PwdPair pp = pairs.get(web);
				String num = pp.key;
				website = website.replaceAll("\\W", "");
				int number = 0;
				for (int i = 0; i < 100; i++) {
					String hash = hash(String.valueOf(i * website.length()));
					if (num.equals(hash)) {
						number = i * website.length();
						break;
					}
				}
				int pre = number % 26;
				for (int i = 1; i < pp.pwd.length(); i++) {
					int asc = (int) pp.pwd.charAt(i) - (int) '!'
							- (int) website.charAt((i - 1) % website.length())
							+ 2 * (char) 'a' - number % 16 - pre;
					password += (char) asc;
					pre = asc - 97;
				}

			}

			return password;
		}

		public String hash(String pwd) {
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

		private class PwdPair {
			public String web, key, pwd;

			public PwdPair(String web, String key, String pwd) {
				this.web = web;
				this.key = key;
				this.pwd = pwd;
			}
		}

		public boolean checkPassword(int choice, String pwd) {
			if (pwd.length() != password.length() - 1) {
				return false;
			}
			boolean pass = true;
			int num = Integer
					.parseInt(password.substring(password.length() - 1));
			System.out.println("@@@" + num);
			for (int i = 0; i < pwd.length() - 1; i++) {
				if ((int) pwd.charAt(i) - (int) password.charAt(i) - num != choice) {
					pass = false;
					break;
				}
			}
			return pass;
		}
	}

	public static void main(String[] args) {
		Scanner scan = new Scanner(System.in);
		boolean c = true;
		int choice = 1;
		while (c) {
			try {
				System.out.println("Please choose to begin:");
				System.out.println("1. Begin with empty file");
				System.out.println("2. Begin with exist pwd file");
				choice = scan.nextInt();
				if (choice == 1 || choice == 2) {
					c = false;
				}
			} catch (Exception e) {
			}
		}
		c = true;
		pwdManager m = new pwdManager();
		if (choice == 2) {
			while (c) {
				System.out.println("File name:");
				String file = scan.next();
				try {
					m = new pwdManager(file);
					c = false;
					System.out.println("Read file successfully!");
				} catch (Exception e) {
					System.out.println("Fail to read file: " + file);
					System.out.println("Begin with empty file.");
					break;
				}
			}
		}
		c = true;
		choice = 0;
		boolean c1 = false;
		boolean c2 = false;
		boolean c3 = false;
		while (c) {
			try {
				System.out.println("Please choose to continue:");
				System.out.println("1. Generate safe password");
				System.out.println("2. Store website and password");
				System.out.println("3. Retrive a password from website");
				System.out.println("4. Exit");
				choice = scan.nextInt();
				if (choice > 0 && choice < 5) {
					switch (choice) {
					case 1:
						String p1 = "";
						if (!c1) {
							System.out
									.println("Please type in the password for this option:");
							p1 = scan.next();
						}
						if (m.checkPassword(1, p1) || c1) {
							c1 = true;
							String tempPwd = m.pwdGenearte();
							System.out.println("password:" + tempPwd);
							System.out
									.println("Do you want to use this password to store with a website?(Y/N)");
							String ch = scan.next();
							if (ch.toLowerCase().equals("y")) {
								System.out.println("Please type in website:");
								String website = scan.next();
								m.store(website, tempPwd);
							}
						}
						break;
					case 2:
						String p2 = "";
						if (!c2) {
							System.out
									.println("Please type in the password for this option:");
							p2 = scan.next();
						}
						if (m.checkPassword(2, p2) || c2) {
							c2 = true;
							System.out.println("Please type in website:");
							String website = scan.next();
							System.out.println("Please type in password:");
							String pwd = scan.next();
							m.store(website, pwd);
							System.out.println("Store successfully!");
						}
						break;
					case 3:
						String p3 = "";
						if (!c3) {
							System.out
									.println("Please type in the password for this option:");
							p3 = scan.next();
						}
						if (m.checkPassword(3, p3) || c3) {
							c3 = true;
							System.out.println("Please type in website:");
							String web = scan.next();
							System.out.println("The password for the website "
									+ web + " is: ");
							System.out.println(m.retrieval(web));
						}
						break;
					case 4:
						c = false;
						break;
					}
				}
			} catch (Exception e) {
			}

		}
	}

}