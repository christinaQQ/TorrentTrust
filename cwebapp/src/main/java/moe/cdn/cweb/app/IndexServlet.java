package moe.cdn.cweb.app;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.inject.Singleton;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import moe.cdn.cweb.app.services.CwebApiService;

@Singleton
public class IndexServlet extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.setContentType("text/html");
        PrintWriter writer = res.getWriter();
        String linkTemplate = "<link rel=\"stylesheet\" href=\"%s\"></link>";
        String scriptTemplate = "<script type=\"text/javascript\" src=\"%s\"></script>";
        String[] stylesheets = {"/app/build/css/main.css"};
        String[] scripts = {"/app/build/js/libs.js", "/app/build/js/main.js"};
        writer.print("<html><head><title>TorrentTrust</title>");
        // load typekit for fonts
        writer.print("<script src=\"https://use.typekit.net/eqe7tlh.js\"></script>"
                + "<script>try{Typekit.load({ async: true });}catch(e){}</script>");
        for (String href : stylesheets) {
            writer.print(String.format(linkTemplate, href));
        }
        // livereload snippet - for development only
        writer.print("</head>");
        writer.print("<body><div id=\"app-container\"></div>");
        writer.print(
                "<script>document.write('<script src=\"http://' + (location.host || 'localhost')"
                        + ".split(':')[0] + ':35729/livereload.js?snipver=1\"></' + 'script>')"
                        + "</script>");
         writer.print(getHydrationScript());
        for (String script : scripts) {
            writer.print(String.format(scriptTemplate, script));
        }
        writer.print("</body>");
        writer.print("</html>");
        writer.flush();
    }

    private String getHydrationScript() throws IOException {
        Path stateFilePath = (Path) getServletContext().getAttribute(
                CwebApiService.STATE_FILE_PATH_ATTRIBUTE);
        String initialStateJson = new String(Files.readAllBytes(stateFilePath));
        return "<script>window.INITIAL_APP_STATE = " + initialStateJson + "</script>";
    }
}
