package moe.cdn.cweb.trust;

import moe.cdn.cweb.TorrentTrustProtos.User;

import java.util.Collection;

public interface CwebTrustNetworkService {
    Collection<User> getLocalTrustNetwork(User user);
}
