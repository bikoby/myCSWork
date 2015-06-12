import java.util.ArrayList;

/*
 * This class does not take a particularly efficient approach, but this
 * simplifies the process of retrieving and cleaning HTML code for your
 * web crawler project later.
 */

/**
 * A helper class with several static methods that will help fetch a webpage,
 * strip out all of the HTML, and parse the resulting plain text into words.
 * Meant to be used for the web crawler project.
 * 
 * @author Jinpeng Bi(Koby)
 */
public class HTMLCleaner {

	/**
	 * Parses the provided plain text (already cleaned of HTML tags) into
	 * individual words.
	 * 
	 * THIS METHOD IS PROVIDED FOR YOU. DO NOT MODIFY.
	 * 
	 * @param text
	 *            - plain text without html tags
	 * @return list of parsed words
	 */
	public static ArrayList<String> parseWords(String text) {
		ArrayList<String> words = new ArrayList<String>();

		for (String word : text.split("\\s+")) {
			word = word.toLowerCase().replaceAll("[\\W_]+", "").trim();
			if (!word.isEmpty()) {
				words.add(word);
			}
		}

		return words;
	}

	/**
	 * Removes all style and script tags (and any text in between those tags),
	 * all HTML tags, and all special characters/entities.
	 * 
	 * THIS METHOD IS PROVIDED FOR YOU. DO NOT MODIFY.
	 * 
	 * @param html
	 *            - html code to parse
	 * @return plain text
	 */
	public static String cleanHTML(String html) {
		String text = html;
		text = stripElement("script", text);
		text = stripElement("style", text);
		text = stripTags(text);
		text = stripEntities(text);
		return text;
	}

	// Click on the Javadoc pane in Eclipse to view the rendered
	// Javadoc for each method. Embedded HTML code, such as in the
	// Javadoc below, will appear properly.

	/**
	 * Removes everything between the element tags, and the element tags
	 * themselves. For example, consider the html code:
	 * 
	 * <pre>
	 * &lt;style type="text/css"&gt;body { font-size: 10pt; }&lt;/style&gt;
	 * </pre>
	 * 
	 * If removing the "style" element, all of the above code will be removed,
	 * and replaced with the empty string.
	 * 
	 * @param name
	 *            - name of the element to strip, like style or script
	 * @param html
	 *            - html code to parse
	 * @return html code without the element specified
	 */
	public static String stripElement(String name, String html) {
		return html.replaceAll("(?is)<" + name + ".*?" + ">" + ".*?" + "</"
				+ name + ".*?" + ">", " ");
	}

	/**
	 * Removes all HTML tags, which is essentially anything between the < and >
	 * symbols. The tag will be replaced by the empty string.
	 * 
	 * @param html
	 *            - html code to parse
	 * @return text without any html tags
	 */
	public static String stripTags(String html) {
		return html.replaceAll("(?s)<.*?>", " ");
	}

	/**
	 * Replaces all HTML entities in the text with the empty string. For
	 * example, "2010&ndash;2012" will become "20102012".
	 * 
	 * @param html
	 *            - the text with html code being checked
	 * @return text with HTML entities replaced by a space
	 */
	public static String stripEntities(String html) {
		return html.replaceAll("(?s)&.*?;", " ");
	}
}