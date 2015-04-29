import java.io.IOException;
import java.nio.file.Files;
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

		InvertedIndex index = new InvertedIndex();
		SearchBuilder search = new SearchBuilder();

		inputFile = ap.hasValue("-d") ? Paths.get(ap.getValue("-d")) : Paths
				.get("input", "search");
		queryFile = ap.hasValue("-q") ? Paths.get(ap.getValue("-q")) : null;
		outputFile = Paths.get(ap.hasValue("-i") ? ap.getValue("-i")
				: "invertedindex.txt");
		searchFile = Paths.get(ap.hasValue("-r") ? ap.getValue("-r")
				: "searchresults.txt");
		try {
			if (Files.isDirectory(inputFile))
				InvertedIndexBuilder.parseFiles(
						DirectoryTraverser.traverse(inputFile), index);
			else
				InvertedIndexBuilder.parseFile(outputFile, index);
			if (ap.hasFlag("-i"))
				index.writeFile(outputFile);

			if (queryFile != null && ap.hasFlag("-r")) {
				search.parseQueryFiles(DirectoryTraverser.traverse(queryFile),
						index);
				search.writeResultFile(searchFile);
			}

			if (ap.hasFlag("-d") && !ap.hasFlag("-i") && !ap.hasFlag("-r")
					&& !ap.hasFlag("-q"))
				index.writeFile(outputFile);
		} catch (IOException e) {
			System.out.println("Input file cannot be openned: "
					+ inputFile.toString());

		} catch (Exception e) {
			System.out.println("Program Erro!");
		}

	}
}
