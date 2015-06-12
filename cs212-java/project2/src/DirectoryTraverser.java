import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class DirectoryTraverser {

	/**
	 * Traverse all files in root and then add all path in the allPath
	 * arrayList. Then return the allPath arrayList
	 * 
	 * @param root
	 *            to parse
	 * 
	 * @return ArrayList<Path> allPath
	 * 
	 */
	public static ArrayList<Path> traverse(Path root) throws IOException {
		ArrayList<Path> allPath = new ArrayList<>();
		if (root != null)
			traverse(root, allPath);
		return allPath;
	}

	/**
	 * Check whether the given root path is a directory. Then get all paths in
	 * it and store it into a Path array.
	 * 
	 * @param root
	 *            allPath to parse
	 * 
	 * 
	 */
	private static void traverse(Path root, ArrayList<Path> allPath)
			throws IOException {
		if (Files.isDirectory(root)) {
			for (Path file : Files.newDirectoryStream(root)) {
				traverse(file, allPath);
			}
		} else {
			if (root.toString().toLowerCase().endsWith("txt"))
				allPath.add(root);
		}
	}

}
