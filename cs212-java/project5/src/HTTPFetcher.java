import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;

/**
 * An abstract class designed to make fetching the results of different HTTP
 * operations easier.
 * 
 * @author Jinpeng Bi(Koby)
 * 
 */
public class HTTPFetcher {
	/** Port used by socket. For web servers, should be port 80. */
	private static final int PORT = 80;

	/** The URL to fetch from a web server. */
	private URL url;

	/**
	 * Initializes this fetcher. Must call {@link #fetch()} to actually start
	 * the process.
	 * 
	 * @param url
	 *            - the link to fetch from the webserver
	 * @throws MalformedURLException
	 *             if unable to parse URL
	 */
	public HTTPFetcher(String url) {

		try {
			this.url = new URL(url);
		} catch (MalformedURLException e) {
			System.out.println("Cannot open the URL:" + url.toString());
		}
	}

	/**
	 * Returns the port being used to fetch URLs.
	 * 
	 * @return port number
	 */
	public int getPort() {
		return PORT;
	}

	/**
	 * Returns the URL being used by this fetcher.
	 * 
	 * @return URL
	 */
	public URL getURL() {
		return url;
	}

	/**
	 * Crafts the HTTP request from the URL. Must be overridden.
	 * 
	 * @return HTTP request
	 */
	protected String craftRequest() {
		String host = this.getURL().getHost();
		String resource = this.getURL().getFile().isEmpty() ? "/" : this
				.getURL().getFile();

		StringBuffer output = new StringBuffer();
		output.append("GET " + resource + " HTTP/1.1\n");
		output.append("Host: " + host + "\n");
		output.append("Connection: close\n");
		output.append("\r\n");

		return output.toString();
	}

	/**
	 * Will connect to the web server and fetch the URL using the HTTP request
	 * from {@link #craftRequest()}, and then call {@link #processLine(String)}
	 * on each of the returned lines.
	 * 
	 * @param urls
	 * @param index
	 * @param lock
	 */
	public String fetch() {
		StringBuilder html = new StringBuilder();
		try (Socket socket = new Socket(url.getHost(), PORT);
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(socket.getInputStream()));
				PrintWriter writer = new PrintWriter(socket.getOutputStream());) {
			String request = craftRequest();

			writer.println(request);
			writer.flush();

			String line = reader.readLine();
			if (!line.contains("200 OK")) {
				return null;
			}

			do {
				line = reader.readLine();
			} while (!line.trim().isEmpty());

			while (line != null) {
				html.append(line + "\n");
				line = reader.readLine();

			}
		} catch (Exception ex) {
			System.out.println("Connect Error!");
		}
		return html.toString();
	}
}