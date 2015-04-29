import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;

@SuppressWarnings("serial")
public class IndexServlet extends LoginBaseServlet {
	private static final String TITLE = "Koby's Search Engine";
	private static Logger log = Log.getRootLogger();

	public IndexServlet() {
		super();
	}

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) {
		response.setContentType("text/html");
		response.setStatus(HttpServletResponse.SC_OK);
		String message = "";
		if (request.getParameter("logout") != null) {
			String user = getUsername(request);
			clearCookies(request, response);
			removeUsers(user);
			message += "Successfully logged out.";
			ROOT = Paths.get("pic", "pictures", "pics1");
			base = "pic1";
		}
		if (request.getParameter("successed") != null) {
			message += "Successfully change your password.";
		}
		if (request.getParameter("theme") != null) {
			message += "Successfully change theme";
		}
		try {
			PrintWriter out = response.getWriter();
			out.printf("<html>%n");
			out.printf("<head><title>%s</title></head>%n", TITLE);
			out.printf("<body>%n");
			printForm(request, response, message);

			out.printf("</body>%n");
			out.printf("</html>%n");
			response.setStatus(HttpServletResponse.SC_OK);
		} catch (Exception e) {
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
		response.setStatus(HttpServletResponse.SC_OK);
		response.sendRedirect(request.getServletPath());
	}

	/**
	 * Print form
	 * 
	 * @param out
	 * @param response
	 * @param message
	 */
	private void printForm(HttpServletRequest request,
			HttpServletResponse response, String message) throws IOException {
		PrintWriter out = response.getWriter();
		Path p = Paths.get(ROOT.toString(), "minions3.jpg");
		out.printf("<div style=\"position:relative;width:100px;height:100px;\">");
		out.printf(
				"<p align=\"center\" align=\"center\"><img src=\"%s/%s\" /></p>",
				base, p.getFileName().toString());
		out.printf("<div style=\"position:absolute;width:600px;height:100px;z-indent:2;left:500;top:0;\">");
		for (int i = 0; i < 10; i++) {
			out.print("<br><br>");
		}
		out.printf("<form  method=\"post\" action=\"%s\">%n", "/search");

		p = Paths.get(ROOT.toString(), "koby.jpg");
		out.printf("<div align=\"center\"><img  src=\"%s/%s\" ></div>", base, p
				.getFileName().toString());
		if (!message.equals("")) {
			out.println("<p align=\"center\" style=\"color: red;\">" + message
					+ "</p>");
		}
		out.printf("<table  cellspacing=\"0\" cellpadding=\"2\""
				+ "align=\"center\" >%n");
		out.printf("<tr>%n");
		out.printf("\t<td>%n");
		out.printf("\t\t"
				+ "<input type=\"text\" name=\"search\" maxlength=\"200\" size=\"60\">"
				+ "%n");
		out.printf("\t</td>%n");
		out.printf("</tr>%n");
		out.printf("</table>%n");
		out.print("<p align=\"center\">");
		for (int i = 0; i < 25; i++) {
			out.print("&nbsp;&nbsp;");
		}
		out.print("<input type=\"submit\" name=\"submit\" value=\"   search   \" >");
		for (int i = 0; i < 5; i++) {
			out.print("&nbsp;&nbsp;");
		}
		out.printf("<input type=\"checkbox\"  name=\"partial\" unchecked value=\"partial\" >turn off Partial Search</p>");
		String user = getUsername(request);
		if (user != null) {
			out.println("<p align=\"center\">Hello <b>" + user + "</b>!<p>");
			if (request.getParameter("login") != null) {
				try {
					String last = db.getLast(user);
					if (last != null)
						out.println("<p align=\"center\">Your last login time is: "
								+ db.getLast(user) + "</p>");
					else
						out.println("<p align=\"center\">This is the first time that you log in! Welcome!</p>");
					db.updateLast(user, getLongDate());
					addUsers(user);
				} catch (SQLException e) {
					System.out.println("Update Last Loggin Time failed");
				}
			}
			String form = "<a href=\"/%s\">%s</a>";
			out.printf("<p align=\"center\">" + "Home" + " | " + form + " | "
					+ form + " | " + form + "</p>", "changepassword",
					"Change Pass Word", "history", "History", "index?logout",
					"Log Out");
			out.print("<p align=\"center\">Currently logged in users: ");
			for (String s : getUsers()) {
				out.print("&nbsp;&nbsp;" + s);
			}
			out.print("</p>");

		} else {
			out.printf("<p align=\"center\"><a href=\"/login\">%s</a>\n%n</p>",
					"Login here...");
		}
		out.printf("</form>\n%n");
		out.printf("</div>");
		out.printf("</div>");
	}
}