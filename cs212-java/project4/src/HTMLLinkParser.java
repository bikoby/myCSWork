import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedHashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * For this homework assignment, you must create a regular expression that is
 * able to parse links from HTML. Your code may assume the HTML is valid, and
 * all attributes are properly quoted and URL encoded.
 * 
 * <p>
 * See the following link for details on the HTML Anchor tag: <a
 * href="https://developer.mozilla.org/en-US/docs/Web/HTML/Element/a">
 * https://developer.mozilla.org/en-US/docs/Web/HTML/Element/a </a>
 * 
 * @author Jinpeng Bi
 * @see HTMLLinkTester
 */
public class HTMLLinkParser {

	/**
	 * The regular expression used to parse the HTML for links.
	 * 
	 */
	public static final String hRegex = "(?is)(<a href=\")";

	public static final String html = "([^#]*?\\.[^(#\"?=)]*?)";

	public static final String eRegex = "(([=?\"].*?)?>)";

	public static final String REGEX = hRegex + html + eRegex;

	/**
	 * The group in the regular expression that captures the raw link. This will
	 * usually be 1, depending on your specific regex.
	 * 
	 */
	public static final int GROUP = 2;

	/**
	 * Parses the provided text for HTML links. You should not need to modify
	 * this method.
	 * 
	 * @param text
	 *            - valid HTML code, with quoted attributes and URL encoded
	 *            links
	 * @param urls
	 * @param url
	 * @return list of links found in HTML code
	 */
	public static LinkedHashSet<String> listLinks(String text, String url) {
		// list to store links
		LinkedHashSet<String> links = new LinkedHashSet<String>();
		// compile string into regular expression
		Pattern p = Pattern.compile(REGEX);
		// match provided text against regular expression
		Matcher m = p.matcher(text);
		// loop through every match found in text
		while (m.find()) {
			try {
				URL absolute = new URL(new URL(url), m.group(GROUP));
				links.add(absolute.toString());

			} catch (MalformedURLException e) {
				System.out.println("Invalid link: " + m.group(GROUP));
			}

		}
		return links;
	}
}
