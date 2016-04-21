package moe.cdn.cweb.app;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import moe.cdn.cweb.app.services.CwebApiService;

public class HydrationServlet extends HttpServlet {

    private static final long serialVersionUID = -3632823424320834412L;

    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/javascript");
        Writer writer = resp.getWriter();
        writer.write(getHydrationScript());
        writer.close();
    }

    private String getHydrationScript() throws IOException {
        Path stateFilePath =
                (Path) getServletContext().getAttribute(CwebApiService.STATE_FILE_PATH_ATTRIBUTE);
        String initialStateJson = new String(Files.readAllBytes(stateFilePath));
        return "window.INITIAL_APP_STATE = " + initialStateJson;
    }
}
