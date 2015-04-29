import java.io.BufferedReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class InvertedIndexBuilder {

	/*
	 * TODO Place the code for reading one or more files, and putting the
	 * results into an inverted index.
	 * 
	 * public static void parseFile(Path file, InvertedIndex index) { when you
	 * find a word, you can do index.add(...); }
	 */
	public InvertedIndexBuilder() { // TODO Can remove if all methods are static
	}

	/**
	 * get file path and then read all words, get words, paths, locations, add
	 * them into the index
	 * 
	 * @param path
	 *            , index
	 */
	public static void parseFile(Path file, InvertedIndex index) {
		Charset charset = Charset.forName("UTF8");
		try (BufferedReader reader = Files.newBufferedReader(file, charset)) {
			String line = null;
			int indexOfWord = 1;
			while ((line = reader.readLine()) != null) {
				for (String word : line.split(" ")) {
					word = clearString(word);
					if (!word.equals("")) {
						index.add(word, file.toAbsolutePath(), indexOfWord);
						indexOfWord++;
					}
				}
			}
			reader.close();
		} catch (Exception e) {
			System.out.println("Connot open the file:" + file.toString());
		}
	}

	/**
	 * get a list of files path, adding them into index using parseFile.
	 * 
	 * @param path
	 *            , index
	 */
	public static void parseFiles(ArrayList<Path> files, InvertedIndex index) {
		for (Path file : files) {
			parseFile(file, index);
		}
	}

	/**
	 * get a String then clear all non-letter, "_", and extra space, and then
	 * return a new String
	 * 
	 * @param string
	 * 
	 * @return string
	 */
	private static String clearString(String string) {
		string = string.replaceAll("[\\W_]", "");
		string = string.trim();
		string = string.toLowerCase();
		return string;
	}

}
