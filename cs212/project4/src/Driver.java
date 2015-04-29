import java.nio.file.Path;
import java.nio.file.Paths;

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
		Path inputFile, outputFile, queryFile, searchFile;
		WorkQueue workers;
		int numOfThread = 5;

		inputFile = ap.hasValue("-d") ? Paths.get(ap.getValue("-d")) : Paths
				.get("input", "search");
		queryFile = ap.hasValue("-q") ? Paths.get(ap.getValue("-q")) : null;
		outputFile = Paths.get(ap.hasValue("-i") ? ap.getValue("-i")
				: "invertedindex.txt");
		searchFile = Paths.get(ap.hasValue("-r") ? ap.getValue("-r")
				: "searchresults.txt");
		if (ap.hasValue("-t")) {
			try {
				int temp = (Integer) Integer.parseInt(ap.getValue("-t"));
				if (temp > 0)
					numOfThread = temp;
			} catch (Exception e) {
			}
		}
		workers = new WorkQueue(numOfThread);

		InvertedIndex index = new InvertedIndex();
		MultithreadedDirectoryTraverser traverser = new MultithreadedDirectoryTraverser(
				workers);
		InvertedIndexBuilder indexBuilder = new InvertedIndexBuilder(workers,
				index);

		if (ap.hasValue("-u") && (ap.hasValue("-i") || ap.hasValue("-q"))) {
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
				System.out.println("Program Erro!");

			}
		}

		if (ap.hasFlag("-d") && !ap.hasFlag("-i") && !ap.hasFlag("-r")
				&& !ap.hasFlag("-q")) {
			index.writeFile(outputFile);

		}
		if (ap.hasFlag("-i"))
			index.writeFile(outputFile);
		if (queryFile != null && ap.hasFlag("-r")) {
			SearchBuilder search = new SearchBuilder(workers);
			traverser.traverseDirectory(queryFile, "txt");
			search.parseQueryFiles(traverser.getPaths(), index);
			search.writeResultFile(searchFile);
			traverser.reset();
		}

		workers.shutdown();

	}
}
