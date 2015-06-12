import java.util.HashSet;
import java.util.LinkedHashSet;

public class UrlTraverser {

	private final WorkQueue workers;
	private final HashSet<String> urls;
	private int pending;
	private final MultiReaderLock lock;
	private InvertedIndex index;
	private int count = 0;

	public UrlTraverser(WorkQueue workers, InvertedIndex index) {
		this.workers = workers;
		urls = new HashSet<String>();
		lock = new MultiReaderLock();
		this.index = index;
	}

	/**
	 * Traverse the given dir and get all paths ending with ext.
	 * 
	 * @param url
	 * 
	 */
	public void traverseDirectory(String url) {
		// TODO Might want a "cleanURL" method that strips fragment, encodes URL
		urls.add(url);
		workers.execute(new URLWorker(url));
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
		count = 0;

	}

	/**
	 * Workers to traverse the links and get its sublinks and then create
	 * another workers to get the links from it sublinks until total 50 links.
	 */
	private class URLWorker implements Runnable {

		private final String url;
		private final LinkedHashSet<String> suburls;
		private final InvertedIndex localIndex;

		public URLWorker(String url) {
			this.url = url;
			suburls = new LinkedHashSet<>();
			localIndex = new InvertedIndex();
			incrementPending();
		}

		@Override
		public void run() {
			String html = new HTTPFetcher(url).fetch();
			html = html == null ? "" : html;

			for (String sublink : HTMLLinkParser.listLinks(html, url)) {
				if (count >= 50) {
					break;
				}
				if (!urls.contains(sublink)) {
					suburls.add(sublink);
					workers.execute(new URLWorker(sublink));
				}
			}
			lock.lockWrite();
			urls.addAll(suburls);
			lock.unlockWrite();
			html = HTMLCleaner.cleanHTML(html);
			int indexOfWord = 1;
			for (String word : HTMLCleaner.parseWords(html)) {
				localIndex.add(word, url, indexOfWord);
				indexOfWord++;
			}
			index.addAll(localIndex);

			decrementPending();

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
		count++;
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
