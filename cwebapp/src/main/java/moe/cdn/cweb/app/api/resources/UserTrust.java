package moe.cdn.cweb.app.api.resources;

import moe.cdn.cweb.SecurityProtos.Hash;
import moe.cdn.cweb.TorrentTrustProtos.User;
import moe.cdn.cweb.app.api.CwebApiEndPoint;
import moe.cdn.cweb.app.api.exceptions.CwebApiEndPointException;
import moe.cdn.cweb.app.dto.UserRef;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * @author davix
 */
@Path("user/trust")
@Produces("application/json")
public class UserTrust extends CwebApiEndPoint {

    @GET
    public List<UserRef> getTrustedUsers() throws InterruptedException, ExecutionException {
        Optional<User> currentUser = getCwebTrustNetworkApi().getUserIdentity().get();
        return getCwebTrustNetworkApi().getLocalTrustNetwork(currentUser.get()).get().stream()
                .map(u -> new UserRef(u.getPublicKey().getHash())).collect(Collectors.toList());
    }

    @GET
    @Path("search")
    public String getLocalNetworkBfs(String depth) throws InterruptedException, ExecutionException {
        Optional<User> currentUser = getCwebTrustNetworkApi().getUserIdentity().get();

        Queue<User> discoveryQueue = new LinkedList<>();
        Queue<User> frontier = new LinkedList<>();
        Set<User> seen = new HashSet<>();

        frontier.add(currentUser.get());

        while (!frontier.isEmpty()) {
            User next = frontier.poll();
            for (User u : getCwebTrustNetworkApi().getLocalTrustNetwork(next).get()) {
                if (!seen.contains(u) && !frontier.contains(u)) {
                    frontier.add(u);
                }
            }
            discoveryQueue.add(next);
            seen.add(next);
        }
        return discoveryQueue.toString();
    }

    @POST
    public Response trustUser(UserRef userRef) throws ExecutionException, InterruptedException {
        Hash publicKey = userRef.getPublicKey();
        if (publicKey == null) {
            throw new BadRequestException("Invalid public key");
        }
        boolean r = getCwebTrustNetworkApi().addUserAsTrusted(publicKey).get();
        if (r) {
            return Response.ok().build();
        }
        throw new CwebApiEndPointException();
    }

    @POST
    @Path("delete")
    public Response untrustUser(UserRef userRef) throws ExecutionException, InterruptedException {
        Hash publicKey = userRef.getPublicKey();
        if (publicKey == null) {
            throw new BadRequestException("Invalid public key");
        }
        boolean r = getCwebTrustNetworkApi().removeUserAsTrusted(publicKey).get();
        if (r) {
            return Response.ok().build();
        }
        throw new CwebApiEndPointException();
    }
}
