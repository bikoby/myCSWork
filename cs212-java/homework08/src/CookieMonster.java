import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;

// More XSS Prevention:
// https://www.owasp.org/index.php/XSS_(Cross_Site_Scripting)_Prevention_Cheat_Sheet

// Apache Comments:
// http://commons.apache.org/proper/commons-lang/download_lang.cgi

@SuppressWarnings("serial")
public class CookieMonster extends BaseServlet {
	private static final String TITLE = "CookieMonster";
	private static Logger log = Log.getRootLogger();

	public CookieMonster() {
		super();
	}

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) {

		response.setContentType("text/html");
		response.setStatus(HttpServletResponse.SC_OK);

		log.info("MessageServlet ID " + this.hashCode()
				+ " handling GET request.");
		try {
			PrintWriter out = response.getWriter();
			out.printf("<html>%n");
			out.printf("<head><title>%s</title></head>%n", TITLE);
			out.printf("<body bgcolor=\"LightSteelBlue\">%n");

			out.printf("<h1>Saved Cookies</h1>%n%n");
			prepareResponse("Cookies!", response);
			Map<String, String> cookies = getCookieMap(request);
			synchronized (cookies) {
				if (!cookies.isEmpty()) {
					for (String formatted : cookies.values()) {
						out.printf("<p>%s</p>%n%n", formatted);
					}
				} else {
					out.printf("No cookie saved");
				}
			}
			out.printf("<head><title>%s</title></head>%n", TITLE);
			out.printf("<h1>Edit Cookies</h1>%n%n");
			printForm(request, response);
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

		prepareResponse("Cookies!", response);
		Map<String, String> cookies = getCookieMap(request);

		String name = request.getParameter("name");
		String value = request.getParameter("value");
		String delete = request.getParameter("delete");

		name = name == null ? "anonymous" : name.trim();
		value = value == null ? "" : value.trim();
		delete = delete == null ? "" : delete;
		String formatted = String.format("<b>%s:</b>%s", name, value);
		if (delete.equals("delete")) {
			System.out.println(11);
			synchronized (cookies) {
				Cookie[] cookiesList = request.getCookies();
				for (Cookie cookie : cookiesList) {
					if (cookie.getName().equals(name)) {
						cookie.setValue(null);
						cookie.setMaxAge(0);
						response.addCookie(cookie);
						break;
					}
				}

			}
		} else {
			if (!formatted.isEmpty() && !name.isEmpty()) {
				synchronized (cookies) {
					if (request.getIntHeader("DNT") != 1) {
						response.addCookie(new Cookie(name, formatted));
					} else {
						clearCookies(request, response);
					}
				}
			}

		}
		String d = request.getParameter("d");
		d = d == null ? "" : d;
		if (d.equals("Delete All Cookies!!")) {
			clearCookies(request, response);
		}
		response.setStatus(HttpServletResponse.SC_OK);
		response.sendRedirect(request.getServletPath());
	}

	private static void printForm(HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		PrintWriter out = response.getWriter();
		out.printf("<form method=\"post\" action=\"%s\">%n",
				request.getServletPath());
		out.printf("<table cellspacing=\"0\" cellpadding=\"2\"%n");
		out.printf("<tr>%n");
		out.printf("\t<td nowrap>Name:</td>%n");
		out.printf("\t<td>%n");
		out.printf("\t\t<input type=\"text\" name=\"name\" maxlength=\"50\" size=\"20\">%n");
		out.printf("\t</td>%n");
		out.printf("</tr>%n");
		out.printf("<tr>%n");
		out.printf("\t<td nowrap>Value:</td>%n");
		out.printf("\t<td>%n");
		out.printf("\t\t<input type=\"text\" name=\"value\" maxlength=\"100\" size=\"60\">%n");
		out.printf("\t</td>%n");
		out.printf("</tr>%n");
		out.printf("</table>%n");
		out.printf("<input type=\"checkbox\"  name=\"delete\" unchecked value=\"delete\" >Delete this cookie!");
		out.printf("<p><input type=\"submit\" value=\"Edit cookie\"></p>\n%n");
		out.printf("<p><input type=\"submit\" name=\"d\" value=\"Delete All Cookies!!\"></p>\n%n");
		out.printf("Last updated at %s", getDate());
		out.printf("</form>\n%n");
	}

	private static String getDate() {
		String format = "hh:mm a 'on' EEEE, MMMM dd yyyy";
		DateFormat formatter = new SimpleDateFormat(format);
		return formatter.format(new Date());
	}

}