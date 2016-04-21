package moe.cdn.cweb.app.api.resources;

import java.util.concurrent.ExecutionException;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import com.google.common.net.HostAndPort;

import moe.cdn.cweb.app.api.CwebApiEndPoint;
import moe.cdn.cweb.app.dto.BootstrapInitializer;
import moe.cdn.cweb.dht.DhtPeerAddress;

@Path("bootstrap")
@Produces("application/json")
public class Bootstrap extends CwebApiEndPoint {
    @POST
    public String bootstrap(BootstrapInitializer bootstrapper)
            throws InterruptedException, ExecutionException {
        DhtPeerAddress peer = new DhtPeerAddress(bootstrapper.getId(),
                HostAndPort.fromParts(bootstrapper.getIp(), bootstrapper.getPort()));
        getDhtPeer().bootstrapToSync(peer);
        return peer.toString();
    }

    @GET
    public BootstrapInitializer myId() throws InterruptedException, ExecutionException {
        DhtPeerAddress peer = getDhtPeer().getAddress();
        return new BootstrapInitializer(peer.getHostAndPort().getHostText(), peer.getId(),
                peer.getHostAndPort().getPort());
    }
}
