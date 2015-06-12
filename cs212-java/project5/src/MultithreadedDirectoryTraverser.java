import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class MultithreadedDirectoryTraverser {

	private final WorkQueue workers;
	private final HashSet<Path> paths;
	private int pending;
	private final MultiReaderLock lock;

	public MultithreadedDirectoryTraverser(WorkQueue workers) {
		this.workers = workers;
		paths = new HashSet<Path>();
		lock = new MultiReaderLock();
	}

	/**
	 * Traverse the given dir and get all paths ending with ext.
	 * 
	 * @param dir
	 * 
	 * @param ext
	 */
	public void traverseDirectory(Path dir, String ext) {

		if (Files.isDirectory(dir)) {
			workers.execute(new DirectoryWorker(dir, ext));
		} else if (dir.toString().toLowerCase().endsWith(ext)) {
			addPath(dir);
		}
	}

	/**
	 * Return the unmodifiable version of the result paths set.
	 * 
	 * @return Collections.unmodifiableSet(results)
	 */
	public Set<Path> getPaths() {
		finish();
		lock.lockRead();
		HashSet<Path> results = paths;
		lock.unlockRead();
		return Collections.unmodifiableSet(results);
	}

	/**
	 * Resets the counters, allowing this object to be easily reused if desired.
	 * Note that we had to make this method synchronized in the multithreaded
	 * version.
	 */
	public void reset() {
		finish();
		lock.lockWrite();
		paths.clear();
		lock.unlockWrite();
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
	 * Will shutdown the work queue after all the current pending work is
	 * finished. Necessary to prevent our code from running forever in the
	 * background.
	 */
	public void shutdown() {
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
			this.directory = directory;
			this.ext = ext;
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
				if (!tempPaths.isEmpty()) {
					addAllPath(tempPaths);
				}

			} catch (IOException e) {
				System.out.println("Unable to parse files.");
			}
			decrementPending();
		}
	}

	/**
	 * Updates the number of files and bytes found. Note that we had to make
	 * this method synchronized to work in the multithreaded version.
	 * 
	 * @param files
	 * @param bytes
	 */
	private void addPath(Path p) {
		lock.lockWrite();
		paths.add(p.toAbsolutePath());
		lock.unlockWrite();
	}

	/**
	 * Updates the number of files and bytes found. Note that we had to make
	 * this method synchronized to work in the multithreaded version.
	 * 
	 * @param files
	 * @param bytes
	 */
	private void addAllPath(Set<Path> p) {
		lock.lockWrite();
		paths.addAll(p);
		lock.unlockWrite();
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
