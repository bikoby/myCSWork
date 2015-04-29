import java.io.BufferedReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Uses the new java.nio package and a {@link BufferedReader} to read a file,
 * parse each line into words, and count the total number of words found in the
 * file.
 * 
 * <p>
 * This is a homework assignment. Fill in the missing code (look for the TODO
 * blocks) without changing the method names or parameters or throwing any
 * checked exceptions. You may add your own members and methods, however.
 */
public class WordCounter {

	/**
	 * Normalizes a word by converting it to lowercase, removing all non-word
	 * characters using the {@code "\\W"} regular expression, removing all
	 * {@code "_"} underscore characters, and removing any unnecessary extra
	 * whitespace at the start of end of the word.
	 * 
	 * @param word
	 *            to normalize
	 * @return normalized version of the word
	 */
	public static String normalizeWord(String word) {
		if (word != null) {
			return word.toLowerCase().replaceAll("[\\W_]", "").trim();
		}
		return null;
	}

	/**
	 * Counts the number of non-null and non-empty words in a {@link String}.
	 * Specifically, it splits the text into words by whitespace using the
	 * {@code "\\s"} regular expression, and uses {@link #normalizeWord(String)}
	 * to normalize that word prior to counting.
	 * 
	 * @param text
	 *            to split into words and count
	 * @return number of words found in the text
	 */
	public static int countWords(String text) {
		int count = 0;
		if (text != null) {
			String[] words = text.split("\\s");
			for (String word : words) {
				word = normalizeWord(word);
				if (word != null && word.length() > 0) {
					count++;
				}
			}
		}
		return count;
	}

	/**
	 * Counts the number of words in a file using the java.nio package, a
	 * {@link BufferedReader} class, and a try-with-resources block. Assumes the
	 * file is encoded using the UTF-8 character set, which can be obtained with
	 * {@code Charset.forName("UTF-8")}. Uses the method
	 * {@link #countWords(String)} to count the words in each line read.
	 * 
	 * <p>
	 * You will not receive a 100% on this homework assignment until you use the
	 * required elements, even if you pass the unit tests.
	 * 
	 * @param file
	 *            to open
	 * @return number of words in the file
	 */
	public static int countWords(Path file) {
		int count = 0;
		if (file != null) {
			if (Files.isReadable(file)) {
				try (BufferedReader reader = Files.newBufferedReader(file,
						Charset.forName("UTF8"))) {
					String line;
					while ((line = reader.readLine()) != null) {
						count += countWords(line);
					}

				} catch (Exception e) {
					System.out.println("cannot open file: " + file.toString());
				}
			}
		}
		return count;
	}
}