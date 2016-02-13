package moe.cdn.cweb.trust;

import java.util.Collection;

import moe.cdn.cweb.TorrentTrustProtos.User;

public interface CwebTrustNetworkService {
    Collection<User> getLocalTrustNetwork(User user);
}
