import java.nio.file.Paths;

import org.junit.Assert;
import org.junit.Test;

public class MultithreadedDirectoryTraverserTester {

	@Test
	public void testOneFile() {
		MultithreadedDirectoryTraverser m = new MultithreadedDirectoryTraverser();
		m.traverseDirectory(Paths.get("queries-alphabet.txt"), "txt");
		m.shutdown();
		Assert.assertEquals(1, m.getPaths().size());
	}

	@Test
	public void testOneDirectory() {
		MultithreadedDirectoryTraverser m = new MultithreadedDirectoryTraverser();
		m.traverseDirectory(Paths.get("input", "search"), "txt");
		m.shutdown();
		Assert.assertEquals(3, m.getPaths().size());
	}

	@Test
	public void testAllDirectory() {
		MultithreadedDirectoryTraverser m = new MultithreadedDirectoryTraverser();
		m.traverseDirectory(Paths.get("input"), "txt");
		m.shutdown();
		Assert.assertEquals(23, m.getPaths().size());
	}

}
