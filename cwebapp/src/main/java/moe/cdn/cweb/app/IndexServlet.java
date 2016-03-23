package moe.cdn.cweb.app;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class IndexServlet extends HttpServlet {
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException  {
		StringBuilder document = new StringBuilder();
		document.append("<html><head><title>Create Account</title></head><body>");
		if(req.getParameter("message") != null) {
			document.append("<h4>" + req.getParameter("message") + "</h4>");
		}
		document.append("<h3>Create Account</h3>");
		document.append("<form method=\"POST\">");
		document.append("<input name=\"username\" type=\"text\" placeholder=\"Username\"><br>");
		document.append("<input name=\"password\" type=\"password\" placeholder=\"Password\"><br>");
		document.append("<input type=\"submit\" value=\"submit\">");
		document.append("</form></body></head>");
		res.setContentType("text/html");
		PrintWriter writer = res.getWriter();
		writer.print(document.toString());
		writer.flush();
	}
}
