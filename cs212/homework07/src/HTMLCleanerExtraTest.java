import org.junit.Assert;
import org.junit.Test;

public class HTMLCleanerExtraTest {

	@Test
	public void testEntities1() {
		String test = "&2010&;2;011";
		String expected = "2;011";
		String actual = HTMLCleaner.stripEntities(test);

		Assert.assertEquals(expected, actual);
	}

	/*
	 * TEST HTML TAGS
	 */

	@Test
	public void testTags1() {
		String test = "<b>hello</b> <world!<b>";
		String expected = "hello ";
		String actual = HTMLCleaner.stripTags(test);

		Assert.assertEquals(expected, actual);
	}

	@Test
	public void testTags2() {
		String test = "<b>hello\n</b>>>> world!";
		String expected = "hello\n>>> world!";
		String actual = HTMLCleaner.stripTags(test);

		Assert.assertEquals(expected, actual);
	}

	@Test
	public void testTags3() {
		String test = "<a \n name=toc table of contents</a>";
		String expected = "";
		String actual = HTMLCleaner.stripTags(test);

		Assert.assertEquals(expected, actual);
	}

	@Test
	public void testTags5() {
		String test = "c<a;a>b";
		String expected = "cb";
		String actual = HTMLCleaner.stripTags(test);

		Assert.assertEquals(expected, actual);
	}

	/*
	 * TEST HTML ELEMENTS
	 */

	@Test
	public void testElements1() {
		String test = "<style type=\"text/css\"?></style>body { font-size: 10pt; }</style>";
		String expected = "body { font-size: 10pt; }</style>";
		String actual = HTMLCleaner.stripElement("style", test);

		Assert.assertEquals(expected, actual);
	}

	@Test
	public void testElements2() {
		String test = "<style type=\"text/css\">\nbody { font-size: 10pt; }\n</style>>";
		String expected = ">";
		String actual = HTMLCleaner.stripElement("style", test);

		Assert.assertEquals(expected, actual);
	}

	@Test
	public void testElements3() {
		String test = "<style \n type=\"text/css\">\nbody /{ font-size: 10pt; }\n<a/style>";
		String expected = "<style \n type=\"text/css\">\nbody /{ font-size: 10pt; }\n<a/style>";
		String actual = HTMLCleaner.stripElement("style", test);

		Assert.assertEquals(expected, actual);
	}

}
