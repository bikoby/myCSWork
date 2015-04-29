import java.io.BufferedWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.TreeSet;

public class InvertedIndex {

	private final TreeMap<String, TreeMap<String, TreeSet<Integer>>> map;
	private final MultiReaderLock lock;

	public InvertedIndex() {
		map = new TreeMap<String, TreeMap<String, TreeSet<Integer>>>();
		lock = new MultiReaderLock();

	}

	/**
	 * Get an other InvertedIndex and compare them. Update the map.
	 * 
	 * @param other
	 */
	public void addAll(InvertedIndex other) {
		lock.lockWrite();
		for (String word : other.map.keySet()) {
			// check whether there is the word in this map
			if (map.containsKey(word)) {
				for (String path : other.map.get(word).keySet()) {
					// check whether there is the path in this map
					if (map.get(word).containsKey(path)) {
						// TODO Instead of doing .contains() on the list, which
						// is very slow
						// do an addAll() to add all the positions from other to
						// map.
						map.get(word).get(path)
								.addAll(other.map.get(word).get(path));

					} else {
						map.get(word).put(path, other.map.get(word).get(path));
					}
				}
			} else {
				map.put(word, other.map.get(word));
			}
		}
		lock.unlockWrite();
	}

	/**
	 * Get word, word path, location and put it in map
	 * 
	 * @param word
	 * @param file
	 * @param location
	 */
	public void add(String word, Path file, int location) {
		lock.lockWrite();
		if (!hasKey(word)) {
			addNewWord(word, file, location);
		} else {
			if (!hasPath(word, file)) {
				addNewPath(word, file, location);
			} else {
				addNewInex(word, file, location);
			}
		}
		lock.unlockWrite();
	}

	/**
	 * For words which exist in map and have the same path, add location in map
	 * 
	 * @param word
	 * @param file
	 * @param location
	 */
	private void addNewInex(String word, Path file, int location) {
		map.get(word).get(file.toString()).add(location);

	}

	/**
	 * For words which exist in map and do not have the same path, add the path
	 * and location in map
	 * 
	 * @param word
	 * @param file
	 * @param location
	 */
	private void addNewPath(String word, Path file, int location) {
		TreeSet<Integer> indexSet = new TreeSet<Integer>();
		indexSet.add(location);
		map.get(word).put(file.toString(), indexSet);

	}

	/**
	 * For words which do not exist in map, get word, file, location and put
	 * them in map
	 * 
	 * @param word
	 * @param file
	 * @param location
	 */
	private void addNewWord(String word, Path file, int location) {
		TreeMap<String, TreeSet<Integer>> pathMap;
		pathMap = new TreeMap<String, TreeSet<Integer>>();
		TreeSet<Integer> indexSet = new TreeSet<Integer>();
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
		lock.lockRead();
		try (BufferedWriter writer = Files.newBufferedWriter(outputFile,
				charset)) {
			for (Entry<String, TreeMap<String, TreeSet<Integer>>> entry : map
					.entrySet()) {
				writer.write(entry.getKey());
				writer.newLine();
				for (Entry<String, TreeSet<Integer>> subEntry : entry
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
			System.out.println("Output file can not create:"
					+ outputFile.toString() + "\n");
		}
		lock.unlockRead();

	}

	/**
	 * 
	 * To search the map to get a list of words which start with one the query
	 * word. Sort them and return.
	 * 
	 * @param queryWords
	 * @return resultList
	 */
	public ArrayList<MatchWordLocation> partialSearch(
			ArrayList<String> queryWords) {
		lock.lockRead();
		HashMap<String, MatchWordLocation> resultMap = new HashMap<>();
		for (String queryWord : queryWords) {
			for (String word : map.tailMap(queryWord).keySet()) {
				if (word.startsWith(queryWord)) {
					for (String location : map.get(word).keySet()) {
						if (resultMap.containsKey(location))
							resultMap.get(location).update(word);
						else {
							resultMap.put(location, new MatchWordLocation(
									location));
							resultMap.get(location).update(word);
						}
					}
				} else
					break;
			}
		}
		lock.unlockRead();
		ArrayList<MatchWordLocation> resultList = new ArrayList<>();
		resultList.addAll(resultMap.values());
		Collections.sort(resultList);
		return resultList;
	}

	/**
	 * An object with frequency, initialPosition, location and implements
	 * Comparable.
	 * 
	 * Including functions: update(String), String getLocation(),
	 * getFrequency(), getInitialPosition(), compareTo(MatchWordLocation)
	 * 
	 * @param location
	 * 
	 */
	public class MatchWordLocation implements Comparable<MatchWordLocation> {

		private int frequency = 0;
		private int initialPosition = Integer.MAX_VALUE;
		private final String location;

		/**
		 * Constructor: create a MatchWordLocation object and initialize
		 * location with the given location.
		 * 
		 * @param location
		 * 
		 */
		public MatchWordLocation(String location) {
			this.location = location;
		}

		/**
		 * To add the frequency with the frequency of the given word and update
		 * the initialPosition with the position of the word if the posisition
		 * of the word is less than the initialPosition.
		 * 
		 * @param word
		 * 
		 * @return true if it contains the word
		 */
		public synchronized void update(String word) {
			frequency += map.get(word).get(location).size();
			int tempPosition = map.get(word).get(location).first();
			if (tempPosition < initialPosition)
				initialPosition = tempPosition;
		}

		/**
		 * Get the location.
		 * 
		 * @return location
		 */
		public String getLocation() {
			return location;
		}

		/**
		 * 
		 * Get the frequency.
		 * 
		 * @return frequency
		 */
		public int getFrequency() {
			return frequency;
		}

		/**
		 * Get the initialPosition.
		 * 
		 * @return initialPosition
		 */
		public int getInitialPosition() {
			return initialPosition;
		}

		/**
		 * Override the compareTo function of Comparable.
		 * 
		 * To compare the frequency first, put the greater frequency in the
		 * front. If the frequency are the same, then compare the
		 * initialPosition and put the smaller initialPosition in the front. If
		 * both the frequency and the initialPosition are the same, then compare
		 * the locations of them.
		 * 
		 * @param word
		 * 
		 * @return true if it contains the word
		 */
		@Override
		public int compareTo(MatchWordLocation other) {
			if (this.frequency != other.frequency) {
				return Integer.compare(other.frequency, this.frequency);
			} else {
				if (this.initialPosition != other.initialPosition) {
					return Integer.compare(this.initialPosition,
							other.initialPosition);
				} else {
					return String.CASE_INSENSITIVE_ORDER.compare(this.location,
							other.location);
				}
			}
		}

	}

}
