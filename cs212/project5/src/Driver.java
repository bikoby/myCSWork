import java.nio.file.Path;
import java.nio.file.Paths;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

/**
 * 
 * Class Usage: to get input path and output filename, then get all words, word
 * locations, and paths, write it into that output file. Then search it with
 * query file, print out the match words' location, frequency and
 * initialPosition.
 * 
 */
public class Driver {

	public static void main(String[] args) {
		ArgumentParser ap = new ArgumentParser(args);
		Path inputFile;
		int PORT = 8080;
		WorkQueue workers;
		int numOfThread = 5;

		inputFile = ap.hasValue("-d") ? Paths.get(ap.getValue("-d")) : Paths
				.get("input", "search");
		if (ap.hasValue("-t")) {
			try {
				int temp = Integer.parseInt(ap.getValue("-t"));
				if (temp > 0)
					numOfThread = temp;
			} catch (Exception e) {
				System.out.println("Invalid number p. Using default number p.");
			}
		}
		if (ap.hasValue("-p")) {
			try {
				int temp = Integer.parseInt(ap.getValue("-p"));
				if (temp > 0)
					PORT = temp;
			} catch (Exception e) {
				System.out.println("Invalid PORT. Using default PORT.");
			}
		}
		workers = new WorkQueue(numOfThread);

		InvertedIndex index = new InvertedIndex();
		MultithreadedDirectoryTraverser traverser = new MultithreadedDirectoryTraverser(
				workers);
		InvertedIndexBuilder indexBuilder = new InvertedIndexBuilder(workers,
				index);

		if (ap.hasValue("-u")) {
			String url = ap.getValue("-u");
			UrlTraverser urlTraverser = new UrlTraverser(workers, index);
			urlTraverser.traverseDirectory(url);
			urlTraverser.finish();
		} else {
			try {
				traverser.traverseDirectory(inputFile, "txt");
				indexBuilder.parseFiles(traverser.getPaths(), index);
				indexBuilder.finish();
				traverser.reset();
			} catch (Exception e) {
				System.out.println("Program Erro!(69)");

			}
		}
		Server server = new Server(PORT);

		// Add static resource holders to web server
		// This indicates where web files are accessible on the file system
		ResourceHandler resourceHandler = new ResourceHandler();
		resourceHandler.setResourceBase("pic/pictures/pics1");
		resourceHandler.setDirectoriesListed(true);

		ContextHandler resourceContext = new ContextHandler("/pic1");
		resourceContext.setHandler(resourceHandler);

		ResourceHandler resourceHandler2 = new ResourceHandler();
		resourceHandler2.setResourceBase("pic/pictures/pics2");
		resourceHandler2.setDirectoriesListed(true);

		ContextHandler resourceContext2 = new ContextHandler("/pic2");
		resourceContext2.setHandler(resourceHandler2);

		ServletContextHandler servletContext = new ServletContextHandler();
		servletContext.addServlet(IndexServlet.class, "/index");

		servletContext.addServlet(new ServletHolder(new SearchServlet(index)),
				"/search");
		servletContext.addServlet(LoginUserServlet.class, "/login");
		servletContext.addServlet(History.class, "/history");
		servletContext.addServlet(LoginRegisterServlet.class, "/register");
		servletContext.addServlet(ChangePasswordServelet.class,
				"/changepassword");
		servletContext.addServlet(LoginRedirectServlet.class, "/*");
		HandlerList handlers = new HandlerList();
		handlers.setHandlers(new Handler[] { resourceContext, resourceContext2,
				servletContext });
		server.setHandler(handlers);
		try {
			server.start();
			server.join();

		} catch (Exception e) {
			System.out.println("Program Erro!(90)");
		}

		workers.shutdown();

	}
}
