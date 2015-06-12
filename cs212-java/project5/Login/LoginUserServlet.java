import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class LoginUserServlet extends LoginBaseServlet {

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		prepareResponse("Login", response);
		String user = getUsername(request);
		if (user != null) {
			response.sendRedirect("/index");
		}
		PrintWriter out = response.getWriter();
		String error = request.getParameter("error");
		int code = 0;
		String message = "";
		if (error != null) {
			try {
				code = Integer.parseInt(error);
			} catch (Exception ex) {
				code = -1;
			}

			message = StringUtilities.getStatus(code).message();

		}

		if (request.getParameter("newuser") != null) {
			message += "Registration was successful!\n";
			message += "Login with your new username and password below.";
		}

		printForm(out, message);
		finishResponse(request, response);
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) {
		String user = request.getParameter("user");
		String pass = request.getParameter("pass");

		Status status = db.authenticateUser(user, pass);

		try {
			if (status == Status.OK) {
				// should eventually change this to something more secure
				response.addCookie(new Cookie("login", "true"));
				response.addCookie(new Cookie("name", user));
				int theme = db.getTheme(user);
				db.changeThemeOption(user, theme);
				String themefile = "pic" + theme;
				ROOT = Paths.get("pic", themefile);
				base = themefile;
				response.sendRedirect(response
						.encodeRedirectURL("/index?login"));
			} else {
				response.addCookie(new Cookie("login", "false"));
				response.addCookie(new Cookie("name", ""));
				response.sendRedirect(response
						.encodeRedirectURL("/login?error=" + status.ordinal()));
			}
		} catch (Exception ex) {
			log.error("Unable to process login form.", ex);
		}
	}

	/**
	 * Print form
	 * 
	 * @param out
	 * @param errorMessage
	 */
	private void printForm(PrintWriter out, String errorMessage) {

		Path p = Paths.get(ROOT.toString(), "minions.jpg");
		out.printf("<div style=\"position:relative;width:100px;height:100px;\">");
		out.printf(
				"<p align=\"center\" align=\"center\"><img src=\"%s/%s\" /></p>",
				base, p.getFileName().toString());
		out.printf("<div style=\"position:absolute;width:1500px;height:100px;z-indent:2;left:500;top:0;\">");
		for (int i = 0; i < 11; i++) {
			out.print("<br><br>");
		}
		out.println("<p align=\"center\"><font size=\"20\">~ Log In ~</font></p>");
		if (!errorMessage.equals("")) {
			out.println("<p align=\"center\" style=\"color: red;\">"
					+ errorMessage + "</p>");
		}
		out.println("<form action=\"/login\" method=\"post\" >");
		out.printf("<table  border=\"0\" align=\"center\" >");
		out.println("\t<tr>");
		out.println("\t\t<td>Usename:</td>");
		out.println("\t\t<td><input type=\"text\" name=\"user\" size=\"30\"></td>");
		out.println("\t</tr>");
		out.println("\t<tr>");
		out.println("\t\t<td>Password:</td>");
		out.println("\t\t<td><input type=\"password\" name=\"pass\" size=\"30\"></td>");
		out.println("</tr>");
		out.println("</table>");
		out.println("<p align=\"center\" ><input  type=\"submit\" value=\"Login\"></p>");
		String form = "<a href=\"/%s\">%s</a>";
		out.printf("<p align=\"center\">" + form + "</p>", "index", "Home");
		out.println("<p align=\"center\" >(<a href=\"/register\">new user? register here.</a>)</p>");
		out.println("</form>");
		out.printf("</div>");
		out.printf("</div>");

	}
}
