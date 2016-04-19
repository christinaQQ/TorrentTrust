package moe.cdn.cweb.app;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@SuppressWarnings("serial")
public class StateServlet extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        // get the file
        res.setContentType("application/json");
        OutputStream out = res.getOutputStream();
        String stateFilePath = this.getInitParameter("stateFilePath");
        Files.copy(Paths.get(stateFilePath), out);
        out.flush();
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        InputStream in = req.getInputStream();
        String stateFilePath = this.getInitParameter("stateFilePath");
        Files.copy(in, Paths.get(stateFilePath), StandardCopyOption.REPLACE_EXISTING);
        res.setContentType("application/json");
        PrintWriter writer = res.getWriter();
        writer.print("{\"success\": true}");
        writer.flush();
    }
}
