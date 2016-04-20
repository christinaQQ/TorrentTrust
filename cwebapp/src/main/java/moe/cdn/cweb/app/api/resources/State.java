package moe.cdn.cweb.app.api.resources;

import moe.cdn.cweb.app.api.CwebApiEndPoint;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;

@Path("setState")
public class State extends CwebApiEndPoint {
    @POST
    @Consumes({"text/plain"})
    public void setState(String state) throws IOException {
        try (BufferedWriter bw = Files.newBufferedWriter(getStateFilePath())) {
            bw.write(state);
        }
    }
}
