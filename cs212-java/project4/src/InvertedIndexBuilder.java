import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

public class InvertedIndexBuilder {

	private final WorkQueue workers;
	private final InvertedIndex index;
	private int pending;

	public InvertedIndexBuilder(WorkQueue workers, InvertedIndex index) {
		this.workers = workers;
		this.index = index;
		pending = 0;
	}

	/**
	 * Workers that parse a file and store informations in the localIndex. And
	 * then add all in the index.
	 * 
	 * @param file
	 * 
	 */
	private class InvertedIndexWorkers implements Runnable {
		private final Path file;
		private final InvertedIndex localIndex;

		public InvertedIndexWorkers(Path file) {
			localIndex = new InvertedIndex();
			this.file = file;
			incrementPending();
		}

		@Override
		public void run() {

			Charset charset = Charset.forName("UTF-8");

			try (BufferedReader reader = Files.newBufferedReader(file, charset)) {
				String line = null;
				int indexOfWord = 1;
				while ((line = reader.readLine()) != null) {
					for (String word : line.split("\\s")) {
						word = clearString(word);
						if (!word.equals("")) {
							localIndex.add(word, file.toString(), indexOfWord);
							indexOfWord++;
						}
					}
				}
				index.addAll(localIndex);
				decrementPending();
			} catch (IOException e) {
				System.out.println("Connot open the file:" + file.toString());

			} catch (Exception e) {
				System.out.println("Programme Erro");
			}
		}

	}

	/**
	 * Get a list of files path, adding them into index using parseFile.
	 * 
	 * @param path
	 * @param index
	 */
	public void parseFiles(Set<Path> set, InvertedIndex index) {
		for (Path file : set) {
			workers.execute(new InvertedIndexWorkers(file));
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
	private static String clearString(String string) {
		string = string.replaceAll("[\\W_]", "");
		string = string.trim();
		string = string.toLowerCase();
		return string;
	}

	/**
	 * Helper method, that helps a thread wait until all of the current work is
	 * done. This is useful for resetting the counters or shutting down the work
	 * queue.
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

}
