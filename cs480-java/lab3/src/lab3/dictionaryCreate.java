package lab3;

import java.io.BufferedWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class dictionaryCreate {
	public static void main(String[] args) {

		Charset charset = Charset.forName("UTF8");
		ArrayList<String> pwds = lab3.readHahFile("pwd");
		try (BufferedWriter writer = Files.newBufferedWriter(
				Paths.get("dictionary"), charset)) {
			String temp;
			for (String pwd : pwds) {
				writer.write(pwd + " " + hash(pwd));
				writer.newLine();
				// add 1 number
				for (int i = 0; i < 10; i++) {
					temp = pwd + i;
					writer.write(temp + " " + hash(temp));
					writer.newLine();
				}
				// replace 1 letter with number
				for (int i = 0; i < pwd.length(); i++) {
					for (int j = 0; j < 10; j++) {
						temp = pwd.substring(0, i) + j + pwd.substring(i + 1);
						writer.write(temp + " " + hash(temp));
						writer.newLine();
					}
				}
				temp = "";
				// change 1 upper case letter
				for (int i = 0; i < pwd.length(); i++) {
					temp = pwd.substring(0, i)
							+ pwd.substring(i, i + 1).toUpperCase()
							+ pwd.substring(i + 1);
					writer.write(temp + " " + hash(temp));
					writer.newLine();
				}
			}
		} catch (Exception e) {
			System.out.println("Outputfile can not create \n");
		}
	}

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
