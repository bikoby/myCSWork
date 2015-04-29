import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MultithreadedDirectoryTraverser {

	private static final Logger logger = LogManager.getLogger();

	private final WorkQueue workers;
	private final TreeSet<Path> paths;
	private final MultiReaderLock lock;
	private int pending;

	public MultithreadedDirectoryTraverser() {
		workers = new WorkQueue();
		paths = new TreeSet<Path>();
		lock = new MultiReaderLock();
	}

	public void traverseDirectory(Path dir, String ext) {
		if (Files.isDirectory(dir)) {
			workers.execute(new DirectoryWorker(dir, ext));
		} else if (dir.toString().toLowerCase().endsWith(ext)) {
			addPath(dir);
		}
	}

	public Set<Path> getPaths() {
		return Collections.unmodifiableSet(paths);
	}

	/**
	 * Resets the counters, allowing this object to be easily reused if desired.
	 * Note that we had to make this method synchronized in the multithreaded
	 * version.
	 */
	public synchronized void reset() {
		finish();
		paths.clear();
		logger.debug("paths reset");
	}

	/**
	 * Helper method, that helps a thread wait until all of the current work is
	 * done. This is useful for resetting the counters or shutting down the work
	 * queue.
	 */
	public synchronized void finish() {
		try {
			while (pending > 0) {
				logger.debug("Waiting until finished");
				this.wait();
			}
		} catch (InterruptedException e) {
			logger.debug("Finish interrupted", e);
		}
	}

	/**
	 * Will shutdown the work queue after all the current pending work is
	 * finished. Necessary to prevent our code from running forever in the
	 * background.
	 */
	public synchronized void shutdown() {
		logger.debug("Shutting down");
		finish();
		workers.shutdown();
	}

	/**
	 * Handles per-directory parsing. If a subdirectory is encountered, a new
	 * {@link DirectoryMinion} is created to handle that subdirectory.
	 */
	private class DirectoryWorker implements Runnable {

		private final Path directory;
		private final String ext;

		public DirectoryWorker(Path directory, String ext) {
			logger.debug("Worker created for {}", directory);
			this.directory = directory;
			this.ext = ext;
			// Indicate we now have "pending" work to do. This is necessary
			// so we know when our threads are "done", since we can no longer
			// call the join() method on them.
			incrementPending();
		}

		@Override
		public void run() {
			try {
				HashSet<Path> tempPaths = new HashSet<Path>();
				for (Path path : Files.newDirectoryStream(directory)) {
					if (Files.isDirectory(path)) {
						workers.execute(new DirectoryWorker(path, ext));
					} else {
						if (path.toString().toLowerCase().endsWith(ext)) {
							tempPaths.add(path);
						}
					}
				}
				if (!tempPaths.isEmpty())
					addAllPath(tempPaths);

				// Indicate that we no longer have "pending" work to do.
				decrementPending();
			} catch (IOException e) {
				logger.warn("Unable to parse {}", directory);
				logger.catching(Level.DEBUG, e);
			}

			logger.debug("Worker finished {}", directory);
		}
	}

	/**
	 * Updates the number of files and bytes found. Note that we had to make
	 * this method synchronized to work in the multithreaded version.
	 * 
	 * @param files
	 * @param bytes
	 */
	private synchronized void addPath(Path p) {
		lock.lockWrite();
		logger.debug("lock write");
		paths.add(p.toAbsolutePath());
		logger.debug("add file: {}, size: {}", paths.size(), p.toAbsolutePath()
				.toString());
		lock.unlockWrite();
		logger.debug("unlock write");
	}

	/**
	 * Updates the number of files and bytes found. Note that we had to make
	 * this method synchronized to work in the multithreaded version.
	 * 
	 * @param files
	 * @param bytes
	 */
	private synchronized void addAllPath(Set<Path> p) {
		lock.lockWrite();
		logger.debug("lock write");
		int n = paths.size();
		paths.addAll(p);
		logger.debug("add files: {}", paths.size() - n);
		lock.unlockWrite();
		logger.debug("unlock write");
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
		logger.debug("Pending is now {}", pending);
	}

	/**
	 * Indicates that we now have one less "pending" work, and will notify any
	 * waiting threads if we no longer have any more pending work left.
	 */
	private synchronized void decrementPending() {
		pending--;
		logger.debug("Pending is now {}", pending);

		if (pending <= 0) {
			this.notifyAll();
		}
	}
}
