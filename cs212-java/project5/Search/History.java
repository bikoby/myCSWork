import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class History extends LoginBaseServlet {
	/** SQL SELECT statement without the ORDER BY clause. */
	protected static final String SELECT = "SELECT "
			+ "username, his As 'History', time " + "FROM History";
	/**
	 * We use this map to validate information that may originate from the user.
	 * This maps user-friendly column names to the actual column names in our
	 * SQL database.
	 */
	private final Map<String, String> tableInfo;

	/**
	 * Sets the database connector to use, and populates the table of valid
	 * column names.
	 * 
	 * @param connector
	 */
	public History() {
		tableInfo = new LinkedHashMap<String, String>();
		tableInfo.put("Username", "History.username");
		tableInfo.put("History", "History.his");
		tableInfo.put("Time", "History.time");
	}

	/**
	 * A helper method to get the column name to sort by. If the column name is
	 * missing or invalid (i.e. a script), we will default to a safe value.
	 * 
	 * @param request
	 * @return safe column name
	 */
	private String getSortName(HttpServletRequest request) {
		String name = request.getParameter("column");

		if ((name == null) || !tableInfo.containsKey(name)) {
			name = "History";
		}

		return name;
	}

	/**
	 * A helper method to get the sort order (ascending or descending). If the
	 * sort order is missing or invalid (i.e. a script), we will default to a
	 * safe value.
	 * 
	 * @param request
	 * @return safe sort order
	 */
	private static boolean getSortOrder(HttpServletRequest request) {
		String asc = request.getParameter("asc");

		if (asc != null) {
			return Boolean.parseBoolean(request.getParameter("asc"));
		} else {
			return true;
		}
	}

	/**
	 * 
	 * {@link ContactSimpleServlet#outputHeaders(HttpServletRequest, HttpServletResponse)}
	 * to output column names that can be clicked to change the sort order. Must
	 * be careful that the GET parameters are validated, and not used directly
	 * on the web page (XSS Attack) or in the SELECT statement (SQL Injection).
	 */
	protected void outputHeaders(HttpServletRequest request,
			HttpServletResponse response) {

		// table format, includes link to change sort column, sort order, and
		// colum name
		String cellFormat = "\t<td><a href=\"/history?column=%s&asc=%b\"><b>%s</b></a></td>%n";
		PrintWriter out = null;
		try {
			out = response.getWriter();
		} catch (IOException e) {
			System.out.println("Programme Erro 91");
		}

		// get parameters safely
		String sort = getSortName(request);
		boolean asc = getSortOrder(request);
		out.printf("<table align=\"center\" cellspacing=\"0\" cellpadding=\"2\" border=\"1\">%n");
		out.printf("<tr style=\"background-color: #EEEEEE;\">%n");
		for (String column : tableInfo.keySet()) {
			// if we sorted by this column, reverse the sort order option
			if (column.equalsIgnoreCase(sort)) {
				out.printf(cellFormat, column, !asc, column);
			} else {
				out.printf(cellFormat, column, true, column);
			}
		}

		out.printf("</tr>%n");
	}

	/**
	 * 
	 * {@link ContactSimpleServlet#outputContacts(HttpServletRequest, HttpServletResponse)}
	 * to get contacts in the sort order specified. Must be careful that the GET
	 * parameters are validated, and not used directly on the web page (XSS
	 * Attack) or in the SELECT statement (SQL Injection).
	 * 
	 * @param user
	 */
	protected void outputContacts(HttpServletRequest request,
			HttpServletResponse response, String user) {

		PrintWriter out = null;
		try {
			out = response.getWriter();
		} catch (IOException e1) {
			System.out.println("Program Error 126");
		}

		// need different format strings to handle websites and emails
		String cellFormat = "\t<td>%s</td>%n";
		String linkFormat = "\t<td><a href=\"%s\">%s</a></td>%n";

		// get parameters safely
		String sort = getSortName(request);
		boolean asc = getSortOrder(request);

		// figure out ORDER BY clause to add to SELECT statement
		String orderby = " WHERE username='" + user + "' " + " ORDER BY "
				+ tableInfo.get(sort);
		orderby += (asc) ? " ASC;" : " DESC;";
		ResultSet results = db.getResultSet(SELECT, orderby);
		try {
			while (results.next()) {
				out.printf("<tr>%n");
				out.printf(cellFormat, results.getString("username"));

				String his = results.getString("History");
				out.printf(linkFormat, "/search?q=" + his, his);
				out.printf(cellFormat, results.getString("time"));
				out.printf("</tr>%n");
			}
		} catch (SQLException e) {
			System.out.println("Programme Error(145)");
		}

	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		prepareResponse("View History", response);
		PrintWriter out = response.getWriter();
		String user = getUsername(request);
		if (user == null) {

			Status status = Status.NotLoggedIn;
			response.sendRedirect("/login?error=" + status.ordinal());
		}
		String message = "";
		if (request.getParameter("clear") != null) {
			message += "Successfully clear history.";
		}
		if (request.getParameter("on") != null) {
			message += "Successfully turn on history.";
		}
		if (request.getParameter("off") != null) {
			message += "Successfully turn off history.";
		}
		printForm(out, message, request, response);

		finishResponse(request, response);
	}

	/**
	 * Print form
	 * 
	 * @param out
	 * @param message
	 * @param request
	 * @param response
	 */
	private void printForm(PrintWriter out, String message,
			HttpServletRequest request, HttpServletResponse response) {
		Path p = Paths.get(ROOT.toString(), "minions2.jpg");
		out.printf("<div style=\"position:relative;width:100px;height:100px;\">");
		out.printf(
				"<p align=\"center\" align=\"center\"><img src=\"%s/%s\" /></p>",
				base, p.getFileName().toString());
		out.printf("<div style=\"position:absolute;width:2000px;height:100px;z-indent:2;left:500;top:0;\">");
		for (int i = 0; i < 3; i++) {
			out.print("<br><br>");
		}
		out.print("<h1><p align=\"center\">~ History ~</p></h1>");
		outputHeaders(request, response);
		String user = getUsername(request);
		outputContacts(request, response, user);
		out.println("</table>");
		String form = "<a href=\"/%s\">%s</a>";
		if (!message.equals("")) {
			out.println("<p align=\"center\" style=\"color: red;\">" + message
					+ "</p>");
		}
		out.printf("<form  method=\"post\" action=\"%s\">%n", "/history?clear");
		if (db.getHistoryOption(user).equals("true")) {
			out.printf("<p align=\"center\" ><input  type=\"submit\" name=\"clearHistory\" value=\"Clear History\"></p>");
			out.printf("<p align=\"center\" ><input  type=\"submit\" name=\"option1\" value=\"Trun off Hitory\"></p>");
		} else {
			out.printf("<p align=\"center\" ><input  type=\"submit\" name=\"option\" value=\"Trun on Hitory\"></p>");
		}
		out.println("</form>");
		out.printf("<p align=\"center\">" + form + " | " + form + " | "
				+ "History" + " | " + form + "</p>", "index", "Home",
				"changepassword", "Change Pass Word", "index?logout", "Log Out");

		out.printf("</div>");
		out.printf("</div>");

	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html");
		response.setStatus(HttpServletResponse.SC_OK);
		String user = getUsername(request);

		try {
			if (request.getParameter("option1") != null) {
				db.changeHistoryOption(user, "false");
				db.clearHistory(user);
				response.setStatus(HttpServletResponse.SC_OK);
				response.sendRedirect("/history?off");
			}
			if (request.getParameter("option") != null) {
				db.changeHistoryOption(user, "true");
				response.setStatus(HttpServletResponse.SC_OK);
				response.sendRedirect("/history?on");
			}
			if (request.getParameter("clearHistory") != null) {
				db.clearHistory(user);
				response.sendRedirect("/history?clear");
			}
		} catch (SQLException e) {
			System.out.println("Program Error!(215)");
		}
	}
}
