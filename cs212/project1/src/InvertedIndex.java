import java.io.BufferedWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.TreeMap;

public class InvertedIndex {

	private TreeMap<String, TreeMap<String, ArrayList<Integer>>> map;

	/**
	 * 
	 * Create map to store words, words locations, paths write things in map
	 * into output file
	 * 
	 */
	public InvertedIndex() {
		map = new TreeMap<String, TreeMap<String, ArrayList<Integer>>>();
	}

	/**
	 * Get word, word path, location and put it in map
	 * 
	 * @param word
	 *            , file, location
	 */

	public void add(String word, Path file, int location) {
		if (!hasKey(word)) {
			addNewWord(word, file, location);
		} else {
			if (!hasPath(word, file)) {
				addNewPath(word, file, location);
			} else {
				addNewInex(word, file, location);
			}
		}
	}

	/**
	 * For words which exist in map and have the same path, add location in map
	 * 
	 * @param word
	 *            , file, location
	 */
	private void addNewInex(String word, Path file, int location) {
		map.get(word).get(file.toString()).add(location);

	}

	/**
	 * For words which exist in map and do not have the same path, add the path
	 * and location in map
	 * 
	 * @param word
	 *            , file, location
	 */
	private void addNewPath(String word, Path file, int location) {
		ArrayList<Integer> indexSet = new ArrayList<Integer>();
		indexSet.add(location);
		map.get(word).put(file.toString(), indexSet);

	}

	/**
	 * For words which do not exist in map, get word, file, location and put
	 * them in map
	 * 
	 * @param word
	 *            , file, location
	 */
	private void addNewWord(String word, Path file, int location) {
		TreeMap<String, ArrayList<Integer>> pathMap;
		pathMap = new TreeMap<String, ArrayList<Integer>>();
		ArrayList<Integer> indexSet = new ArrayList<Integer>();
		indexSet.add(location);
		pathMap.put(file.toString(), indexSet);
		map.put(word, pathMap);

	}

	/**
	 * Check whether the map have the key word
	 * 
	 * @param word
	 * 
	 * @return true if it contains the word
	 */
	private boolean hasKey(String word) {
		return map.containsKey(word);
	}

	/**
	 * Check whether the map have the path of the key
	 * 
	 * @param word
	 * 
	 * @return true if it contains the path
	 */
	private boolean hasPath(String word, Path file) {
		return map.get(word).containsKey(file.toString());
	}

	/**
	 * Get an output file path, then write all things in map into that file
	 * 
	 * @param outputFile
	 */
	public void writeFile(Path outputFile) {
		Charset charset = Charset.forName("UTF8");
		try (BufferedWriter writer = Files.newBufferedWriter(outputFile,
				charset)) {
			for (Entry<String, TreeMap<String, ArrayList<Integer>>> entry : map
					.entrySet()) {
				writer.write(entry.getKey());
				writer.newLine();
				for (Entry<String, ArrayList<Integer>> subEntry : entry
						.getValue().entrySet()) {
					writer.write("\"" + subEntry.getKey() + "\"");
					for (Integer index : subEntry.getValue()) {
						writer.write(", " + index);
					}
					writer.newLine();
				}
				writer.newLine();
			}
		} catch (Exception e) {
			System.out.println("Outputfile can not create:"
					+ outputFile.toString() + "\n");
		}

	}

}
