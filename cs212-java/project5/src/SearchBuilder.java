import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Set;

public class SearchBuilder {

	/**
	 * 
	 * Create map to store queryWords and the MatchWordLocations.
	 * 
	 */
	private final LinkedHashMap<String, ArrayList<InvertedIndex.MatchWordLocation>> searchWords;
	private final WorkQueue workers;
	private final MultiReaderLock lock;
	private int pending;

	public SearchBuilder(WorkQueue wokers) {
		searchWords = new LinkedHashMap<String, ArrayList<InvertedIndex.MatchWordLocation>>();
		this.workers = wokers;
		this.lock = new MultiReaderLock();
		pending = 0;
	}

	/**
	 * Parse queryFiles and pass all files in addQueryLines(Path).
	 * 
	 * @param set
	 * 
	 */
	public void parseQueryFiles(Set<Path> set, InvertedIndex index) {
		for (Path queryFile : set) {
			Charset charset = Charset.forName("UTF8");
			try (BufferedReader reader = Files.newBufferedReader(queryFile,
					charset)) {
				String line = null;
				while ((line = reader.readLine()) != null) {
					for (String queryWord : line.split("\\s")) {
						queryWord = clearString(queryWord);
						if (!queryWord.equals("")) {
							lock.lockWrite();
							searchWords.put(line, null);
							lock.unlockWrite();
							workers.execute(new SearchWorker(line, index));
						}

					}

				}

			} catch (IOException e) {
				System.out.println("Connot open queryFile: "
						+ queryFile.toString());
			}
		}
	}

	/**
	 * Worker that get a line and do the searching on index
	 * 
	 * @param queryLine
	 * 
	 * @param index
	 */
	private class SearchWorker implements Runnable {

		private final String queryLine;
		private final InvertedIndex index;

		public SearchWorker(String queryLine, InvertedIndex index) {
			incrementPending();
			this.queryLine = queryLine;
			this.index = index;
		}

		@Override
		public void run() {
			ArrayList<String> queryWords = new ArrayList<String>();
			for (String queryWord : queryLine.split("\\s")) {
				queryWord = clearString(queryWord);
				if (!queryWord.equals(""))
					queryWords.add(queryWord);
			}
			ArrayList<InvertedIndex.MatchWordLocation> results = index
					.partialSearch(queryWords);
			lock.lockWrite();
			searchWords.put(queryLine, results);
			lock.unlockWrite();
			decrementPending();

		}

	}

	/**
	 * Helper method, that helps a thread wait until all of the current work is
	 * done. This is useful for resetting the counters or shutting down the work
	 * queue.
	 * 
	 * @param searchFile
	 */
	public synchronized void finish() {
		try {
			while (pending > 0) {
				this.wait();
			}
		} catch (InterruptedException e) {
			System.out.println("Programme Error");
		}

	}

	/**
	 * Indicates that we now have additional "pending" work to wait for. We need
	 * this since we can no longer call join() on the threads. (The threads keep
	 * running forever in the background.)
	 * 
	 * We made this a synchronized method in the outer class, since locking on
	 * the "this" object within an inner class does not work.
	 */
	private synchronized void incrementPending() {
		pending++;
	}

	/**
	 * Indicates that we now have one less "pending" work, and will notify any
	 * waiting threads if we no longer have any more pending work left.
	 */
	private synchronized void decrementPending() {
		pending--;

		if (pending <= 0) {
			this.notifyAll();
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
		finish();
		Charset charset = Charset.forName("UTF8");
		lock.lockRead();
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
		lock.unlockRead();

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
