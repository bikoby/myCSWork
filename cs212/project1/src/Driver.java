import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Create helper object:
 * 
 * ArgumentPaser to get the arguments of input and output path.
 * 
 * InvertedIndex to create a map to store all words, paths, locations. And write
 * all things in a file
 * 
 * InvertedIndex to go through all words in file
 * 
 * DirectoryTraverser to get all file path
 * 
 * Class Usage: to get input path and output filename, then get all words, word
 * locations, and paths, write it into that output file
 * 
 */
public class Driver {

	public static void main(String[] args) {
		ArgumentParser ap = new ArgumentParser(args);
		Path inputFile;
		Path outputFile;
		inputFile = Paths.get(ap.hasFlag("-d") ? ap.getValue("-d")
				: "input/index");
		outputFile = Paths.get(ap.hasFlag("-i") ? ap.getValue("-i")
				: "invertedindex.txt");
		InvertedIndex index = new InvertedIndex();
		try {
			InvertedIndexBuilder.parseFiles(
					DirectoryTraverser.traverse(inputFile), index);
		} catch (IOException e) {
			System.out.println("inputFile cannot be openned: "
					+ inputFile.toString());
		}
		index.writeFile(outputFile);

	}
}
