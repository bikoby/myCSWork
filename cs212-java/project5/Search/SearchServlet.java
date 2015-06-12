import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;

@SuppressWarnings("serial")
public class SearchServlet extends LoginBaseServlet {
	private static final String TITLE = "Koby's Search Engine";
	private static Logger log = Log.getRootLogger();
	private final InvertedIndex index;
	private ArrayList<InvertedIndex.MatchWordLocation> results;
	private String key = "";
	private boolean partial = true;
	private int numPerPage = 10;
	private int page = 0;

	public SearchServlet(InvertedIndex index) {
		super();
		this.index = index;
		results = new ArrayList<>();
	}

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) {

		response.setContentType("text/html");
		response.setStatus(HttpServletResponse.SC_OK);
		try {
			PrintWriter out = response.getWriter();
			out.printf("<html>%n");
			out.printf("<head><title>%s</title></head>%n", TITLE);
			out.printf("<body>%n");

			String q = request.getParameter("q");
			if (q == null) {
				response.setStatus(HttpServletResponse.SC_OK);
				response.sendRedirect("/index");
			}
			long startTime = System.nanoTime();
			search(q, request);
			double searchTime = (System.nanoTime() - startTime) / (1000000000.0);
			printForm(request, response);
			out.printf("<form method=\"post\" action=\"%s\">%n", "/search?q="
					+ q);
			String format = "<option  value=\"%d\" %s>%s</option>";
			page = request.getParameter("p") == null ? 1 : Integer
					.parseInt(request.getParameter("p"));
			String[] check = new String[6];
			for (int i = 5; i < 11; i++) {
				if (numPerPage == i) {
					check[i - 5] = "selected=\"selected\"";
				} else {
					check[i - 5] = "";
				}
			}
			out.printf(
					"Number of results display in each page:"
							+ "<select id=\"choice\" name=\"choice\" onchange=\"submit();\">"
							+ format + format + format + format + format
							+ format + "</select></form>", 5, check[0], "5", 6,
					check[1], "6", 7, check[2], "7", 8, check[3], "8", 9,
					check[4], "9", 10, check[5], "10");

			out.printf("<p>You are searching: <b>%s</b></p>%n%n", key);

			if (!results.isEmpty()) {
				out.printf(
						"<p><font color=\"LightSteelBlue\" >About %d results. (%f seconds)</font></p>",
						results.size(), searchTime);
				int pageNum = results.size() % numPerPage == 0 ? results.size()
						/ numPerPage : results.size() / numPerPage + 1;
				for (int j = 0; j < pageNum; j++) {
					if (j != page) {
						out.printf("<a href=\"/%s\">%s</a>&nbsp;&nbsp;&nbsp;",
								"search?q=" + key + "&p=" + j, j + 1);
					} else {
						out.print((j + 1) + "&nbsp;&nbsp;&nbsp;");
					}
				}
				int end;

				if (page >= results.size() / numPerPage) {
					end = results.size() - numPerPage * page;
				} else {
					end = numPerPage;
				}
				for (int i = page * numPerPage; i < page * numPerPage + end; i++) {
					out.printf("<p><a href=\"%s\">%100s</a></p><p>%s</p>%n%n",
							results.get(i).getLocation(), results.get(i)
									.getLocation(), db.getSnippet(results
									.get(i).getLocation()));
				}
				for (int j = 0; j < pageNum; j++) {
					if (j != page) {
						out.printf("<a href=\"/%s\">%s</a>&nbsp;&nbsp;&nbsp;",
								"search?q=" + key + "&p=" + j, j + 1);
					} else {
						out.print((j + 1) + "&nbsp;&nbsp;&nbsp;");
					}
				}

			} else {
				out.printf("<p>No result found: <b>%s</b></p>%n%n ", key);
			}
			out.print("<p>Currently logged in users: ");
			for (String s : getUsers()) {
				out.print("&nbsp;&nbsp;" + s);
			}
			out.print("</p>");
			out.printf("</body>%n");
			out.printf("</html>%n");
			response.setStatus(HttpServletResponse.SC_OK);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error.");
		}
	}

	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		response.setStatus(HttpServletResponse.SC_OK);
		log.info("MessageServlet ID " + this.hashCode()
				+ " handling POST request.");
		if (request.getParameter("partial") == null) {
			partial = false;
		} else {
			partial = true;
		}
		String html = request.getParameter("url");
		if (html != null && !html.isEmpty()) {
			WorkQueue workers = new WorkQueue(5);
			UrlTraverser urlTraverser = new UrlTraverser(workers, index);
			urlTraverser.traverseDirectory(html);
			urlTraverser.finish();
		}

		if (request.getParameter("theme") != null) {
			String user = getUsername(request);
			int theme = db.getTheme(user) == 1 ? 2 : 1;
			db.changeThemeOption(user, theme);
			String themefile = "pic" + theme;
			ROOT = Paths.get("pic", themefile);
			base = themefile;
			response.setStatus(HttpServletResponse.SC_OK);
			response.sendRedirect("/index?theme");
		} else if (request.getParameter("choice") != null) {
			numPerPage = Integer.parseInt(request.getParameter("choice"));
			response.setStatus(HttpServletResponse.SC_OK);
			response.sendRedirect("/search?q=" + key + "&p=0");
		} else {
			String search = HTMLCleaner.cleanHTML(request
					.getParameter("search"));
			String result = "";
			if (search != null && !search.isEmpty()) {
				key = search;
				String[] keysArray = search.split(" ");
				for (int i = 0; i < keysArray.length; i++) {
					result += keysArray[i];
					if (i != keysArray.length - 1)
						result += "+";
				}

				result = "/search?q=" + result + "&p=0";
				page = 0;
			} else {
				key = "";
				results.clear();
				result = "/index";
			}
			if (request.getParameter("choice") != null) {
				System.out.println(request.getParameter("id"));
			}

			response.setStatus(HttpServletResponse.SC_OK);
			response.sendRedirect(result);
		}
	}

	/**
	 * Search for the key.
	 * 
	 * @param search
	 * @param request
	 */
	private void search(String search, HttpServletRequest request) {
		if (search != null && !search.isEmpty()) {
			key = search;
			ArrayList<String> keys = new ArrayList<String>();
			String[] keysArray = search.split(" ");
			for (int i = 0; i < keysArray.length; i++) {
				keys.add(keysArray[i]);

			}
			if (partial) {
				results = index.matchSearch(keys);
			} else {
				results = index.partialSearch(keys);
			}
		} else {
			results.clear();
		}

	}

	/**
	 * Print form
	 * 
	 * @param out
	 * @param response
	 */
	private void printForm(HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		PrintWriter out = response.getWriter();

		Path p = Paths.get(ROOT.toString(), "minions4.jpg");
		out.printf("<div style=\"position:relative;width:100px;height:100px;\">");
		out.printf("<p align=\"center\" ><img src=\"%s/%s\" /></p>", base, p
				.getFileName().toString());
		out.printf("<div style=\"position:absolute;width:1500px;height:100px;z-indent:2;left:0;top:0;\">");

		p = Paths.get(ROOT.toString(), "koby.jpg");
		out.printf("<form method=\"post\" action=\"%s\">%n",
				request.getServletPath());
		out.printf("<table cellspacing=\"0\" cellpadding=\"2\">%n");
		out.printf("<tr>%n");
		out.printf("\t<td>%n");
		out.printf(
				"<a href=\"/\"><img  src=\"%s/%s\" width=\"130\" height=\"60\"></a>",
				base, p.getFileName().toString());
		out.printf("\t\t" + "<input type=\"text\" name=\"search\" value=\""
				+ key + "\" maxlength=\"80\" size=\"80\" >" + "%n");
		out.printf("<input type=\"submit\" name=\"submit\" value=\"   search   \" >\n%n");

		out.printf("\t</td>%n");
		out.printf("</tr>%n");
		out.printf("</table>%n");

		String user = getUsername(request);
		if (user != null) {
			out.printf("<input type=\"submit\" name=\"theme\" value=\"   change theme   \" >\n%n");
			out.println("Welcome <b>" + user + "</b>!");
			if (user.equals("koby")) {
				out.printf("\t\t" + "<input type=\"text\" name=\"url\" "
						+ " maxlength=\"80\" size=\"80\" >" + "%n");
				out.printf("<input type=\"submit\" name=\"add\" value=\"Add New Crawl\" >\n%n");
			}
			for (int i = 0; i < 40; i++) {
				out.print("&nbsp;&nbsp;");
			}
			out.printf("<input type=\"checkbox\"  name=\"partial\" unchecked value=\"partial\" >turn off Partial Search");
			String form = "<a href=\"/%s\">%s</a>";
			out.printf("<p >" + form + " | " + form + " | " + form + " | "
					+ form + "</p>", "index", "Home", "changepassword",
					"Change Pass Word", "history", "History", "index?logout",
					"Log Out");
			if (key != null && !key.isEmpty()
					&& db.getHistoryOption(user).equals("true")) {
				db.updateHis(user, key, getLongDate());
			}

		} else {
			out.printf(
					"<a href=\"/index\">%s</a> | <a href=\"/login\">%s</a>\n%n",
					"Home", "Login here...");
			for (int i = 0; i < 50; i++) {
				out.print("&nbsp;&nbsp;");
			}
			out.printf("<input type=\"checkbox\"  name=\"partial\" unchecked value=\"partial\" >turn off Partial Search</p>");

		}
		out.printf("</form>\n%n");
	}

}