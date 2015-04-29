import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class SearchBuilder {

	/**
	 * 
	 * Create map to store queryWords and the MatchWordLocations.
	 * 
	 */
	private final LinkedHashMap<String, ArrayList<InvertedIndex.MatchWordLocation>> searchWords;

	public SearchBuilder() {
		searchWords = new LinkedHashMap<String, ArrayList<InvertedIndex.MatchWordLocation>>();
	}

	/**
	 * Parse queryFiles and pass all files in addQueryLines(Path).
	 * 
	 * @param queryFiles
	 * 
	 */
	public void parseQueryFiles(ArrayList<Path> queryFiles, InvertedIndex index) {
		for (Path queryFile : queryFiles) {
			querySearch(queryFile, index);
		}
	}

	/**
	 * Go through the file and get each line of query words. Check each word in
	 * index and get a list of MatchWordLocation and put them in the
	 * searchWords.
	 * 
	 * @param file
	 * 
	 */
	private void querySearch(Path file, InvertedIndex index) {
		Charset charset = Charset.forName("UTF8");
		try (BufferedReader reader = Files.newBufferedReader(file, charset)) {
			String line = null;
			while ((line = reader.readLine()) != null) {
				ArrayList<String> tempQueryWords = new ArrayList<String>();
				for (String queryWord : line.split("\\s")) {
					queryWord = clearString(queryWord);
					if (!queryWord.equals(""))
						tempQueryWords.add(queryWord);
				}
				searchWords.put(line, index.partialSearch(tempQueryWords));
			}
		} catch (Exception e) {
			System.out.println("Connot open the file:" + file.toString());
		}
	}

	/**
	 * Get each queriesWord in the map and write them with their
	 * MatchWordLocation in the resultFile.
	 * 
	 * @param resultFile
	 * 
	 */
	public void writeResultFile(Path resultFile) {
		Charset charset = Charset.forName("UTF8");
		try (BufferedWriter writer = Files.newBufferedWriter(resultFile,
				charset)) {
			for (String queryLine : searchWords.keySet()) {
				writer.write(queryLine);
				writer.newLine();
				for (InvertedIndex.MatchWordLocation sW : searchWords
						.get(queryLine)) {
					writer.write("\"" + sW.getLocation() + "\"" + ", "
							+ sW.getFrequency() + ", "
							+ sW.getInitialPosition());
					writer.newLine();
				}
				writer.newLine();
			}
		} catch (IOException e) {
			System.out.println("Outputfile can not create:"
					+ resultFile.toString() + "\n");
		}

	}

	/**
	 * Get a String then clear all non-letter, "_", and extra space, and then
	 * return a new String
	 * 
	 * @param string
	 * 
	 * @return string
	 */
	private String clearString(String string) {
		string = string.replaceAll("[\\W_]", "");
		string = string.trim();
		string = string.toLowerCase();
		return string;
	}

}
