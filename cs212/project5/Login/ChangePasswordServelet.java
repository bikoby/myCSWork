import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class ChangePasswordServelet extends LoginBaseServlet {

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		prepareResponse("Change Pass Word", response);
		PrintWriter out = response.getWriter();
		String user = getUsername(request);
		if (user == null) {

			Status status = Status.NotLoggedIn;
			response.sendRedirect("/login?error=" + status.ordinal());
		}
		String wrong = request.getParameter("wrong");

		String message = "";
		if (wrong != null) {
			message += "Wrong old password";
		}

		printForm(out, message);
		finishResponse(request, response);
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		prepareResponse("Register New User", response);
		String user = getUsername(request);
		String oldP = request.getParameter("old");
		String newP = request.getParameter("new");
		Status status = db.changePassword(user, newP, oldP);
		if (status == Status.OK) {
			// should eventually change this to something more secure
			response.addCookie(new Cookie("login", "true"));
			response.addCookie(new Cookie("name", user));
			response.setStatus(HttpServletResponse.SC_OK);
			response.sendRedirect(response.encodeRedirectURL("/index")
					+ "?successed");
		} else {
			String url = "/changepassword?wrong";
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
		Path p = Paths.get(ROOT.toString(), "minions3.jpg");
		out.printf("<div style=\"position:relative;width:100px;height:100px;\">");
		out.printf(
				"<p align=\"center\" align=\"center\"><img src=\"%s/%s\" /></p>",
				base, p.getFileName().toString());
		out.printf("<div style=\"position:absolute;width:1500px;height:100px;z-indent:2;left:500;top:0;\">");
		for (int i = 0; i < 11; i++) {
			out.print("<br><br>");
		}
		out.println("<p align=\"center\"><font size=\"20\">~ Change Password ~</font></p>");
		if (!message.equals("")) {
			out.println("<p align=\"center\" style=\"color: red;\">" + message
					+ "</p>");
		}
		out.println("<form action=\"/changepassword\" method=\"post\">");
		out.println("<table border=\"0\" align=\"center\">");
		out.println("\t<tr>");
		out.println("\t\t<td>Old PassWord:</td>");
		out.println("\t\t<td><input type=\"password\" name=\"old\" size=\"30\"></td>");
		out.println("\t</tr>");
		out.println("\t<tr>");
		out.println("\t\t<td>New Password:</td>");
		out.println("\t\t<td><input type=\"password\" name=\"new\" size=\"30\"></td>");
		out.println("</tr>");
		out.println("</table>");
		out.println("<p align=\"center\"><input type=\"submit\" value=\"Submit\"></p>");
		out.println("</form>");
		String form = "<a href=\"/%s\">%s</a>";
		out.printf("<p align=\"center\">" + form + " | " + "Change Pass Word"
				+ " | " + form + " | " + form + "</p>", "index", "Home",
				"history", "History", "index?logout", "Log Out");
		out.printf("</div>");
		out.printf("</div>");
	}
}
