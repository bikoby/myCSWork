import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class LoginRegisterServlet extends LoginBaseServlet {

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		prepareResponse("Register New User", response);
		PrintWriter out = response.getWriter();
		String error = request.getParameter("error");
		String message = "";
		if (error != null) {
			String errorMessage = StringUtilities.getStatus(error).message();
			message += errorMessage;
		}

		printForm(out, message);
		finishResponse(request, response);
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		prepareResponse("Register New User", response);

		String newuser = request.getParameter("user");
		String newpass = request.getParameter("pass");
		Status status = db.registerUser(newuser, newpass);

		status = db.defautOption(newuser);

		if (status == Status.OK) {
			response.sendRedirect(response
					.encodeRedirectURL("/login?newuser=true"));
		} else {
			String url = "/register?error=" + status.name();
			response.sendRedirect(response.encodeRedirectURL(url));
		}
	}

	/**
	 * Print form
	 * 
	 * @param out
	 * @param message
	 */
	private void printForm(PrintWriter out, String message) {
		Path p = Paths.get(ROOT.toString(), "minions5.jpg");
		out.printf("<div style=\"position:relative;width:100px;height:100px;\">");
		out.printf(
				"<p align=\"center\" align=\"center\"><img src=\"%s/%s\" /></p>",
				base, p.getFileName().toString());
		out.printf("<div style=\"position:absolute;width:1500px;height:100px;z-indent:2;left:500;top:0;\">");
		for (int i = 0; i < 11; i++) {
			out.print("<br><br>");
		}
		out.println("<p align=\"center\"><font size=\"20\">~ Register ~</font></p>");
		if (!message.equals("")) {
			out.println("<p align=\"center\" style=\"color: red;\">" + message
					+ "</p>");
		}
		out.println("<form action=\"/register\" method=\"post\">");
		out.println("<table border=\"0\" align=\"center\">");
		out.println("\t<tr>");
		out.println("\t\t<td>Usename:</td>");
		out.println("\t\t<td><input type=\"text\" name=\"user\" size=\"30\"></td>");
		out.println("\t</tr>");
		out.println("\t<tr>");
		out.println("\t\t<td>Password:</td>");
		out.println("\t\t<td><input type=\"password\" name=\"pass\" size=\"30\"></td>");
		out.println("</tr>");
		out.println("</table>");
		out.println("<p align=\"center\"><input type=\"submit\" value=\"Register\"></p>");
		out.println("</form>");
		String form = "<a href=\"/%s\">%s</a>";
		out.printf("<p align=\"center\">" + form + " | " + form + "</p>",
				"index", "Home", "login", "Log In");
		out.printf("</div>");
		out.printf("</div>");
	}
}
