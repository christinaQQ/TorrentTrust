package moe.cdn.cweb.app.api.resources;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import moe.cdn.cweb.SecurityProtos.Hash;
import moe.cdn.cweb.TorrentTrustProtos.User;
import moe.cdn.cweb.app.api.CwebApiEndPoint;
import moe.cdn.cweb.app.dto.UserRef;

/**
 * @author davix
 */
@Path("user/trust")
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
    public boolean trustUser(UserRef userRef) throws ExecutionException, InterruptedException {
        Hash publicKey = userRef.getPublicKey();
        return getCwebTrustNetworkApi().addUserAsTrusted(publicKey).get();
    }

    @POST
    @Path("delete")
    public boolean untrustUser(UserRef userRef) throws ExecutionException, InterruptedException {
        Hash publicKey = userRef.getPublicKey();
        return getCwebTrustNetworkApi().removeUserAsTrusted(publicKey).get();
    }
}
