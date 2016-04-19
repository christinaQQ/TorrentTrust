package moe.cdn.cweb.dht.internal;

import com.google.common.net.HostAndPort;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import moe.cdn.cweb.dht.DhtPeerAddress;
import moe.cdn.cweb.dht.ManagedPeer;
import moe.cdn.cweb.dht.PeerEnvironment;
import moe.cdn.cweb.dht.internal.tomp2pcompat.BaseFutureAsListenableFuture;
import moe.cdn.cweb.dht.util.Number160s;
import moe.cdn.cweb.security.CwebId;
import net.tomp2p.dht.PeerBuilderDHT;
import net.tomp2p.dht.PeerDHT;
import net.tomp2p.dht.Storage;
import net.tomp2p.futures.BaseFuture;
import net.tomp2p.futures.FutureBootstrap;
import net.tomp2p.p2p.Peer;
import net.tomp2p.p2p.PeerBuilder;
import net.tomp2p.peers.PeerAddress;
import net.tomp2p.replication.IndirectReplication;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * A {@link PeerDHT} peer that is under management. This allows external sources
 * to manage the peer without requiring access to TomP2P.
 *
 * @author jim
 */
public class ManagedPeerDhtPeer implements ManagedPeer {
    private final PeerDHT peerDht;
    private final DhtPeerAddress address;
    private Optional<IndirectReplication> replication;

    public ManagedPeerDhtPeer(PeerDHT peerDht) {
        this.peerDht = peerDht;
        this.address = new DhtPeerAddress(new CwebId(peerDht.peer().peerID().toByteArray()),
                HostAndPort.fromParts(peerDht.peerAddress().inetAddress().getHostAddress(),
                        peerDht.peerAddress().tcpPort()));
        this.replication = Optional.empty();
    }

    /**
     * Builds a {@link ManagedPeer} from the environment. Does NOT bootstrap to
     * peers.
     *
     * @param peerEnvironment environment containing parameters to build to
     * @param storage         storage validator
     * @return a ManagedDhtPeer
     * @throws IOException
     */
    public static ManagedPeerDhtPeer fromEnviroment(PeerEnvironment peerEnvironment,
                                                    Storage storage) throws IOException {
        Peer peer = new PeerBuilder(Number160s.fromCwebId(peerEnvironment.getMyId()))
                .tcpPort(peerEnvironment.getLocalTcpPort())
                .udpPort(peerEnvironment.getLocalUdpPort()).start();
        PeerDHT peerDht = new PeerBuilderDHT(peer).storage(storage).start();
        return new ManagedPeerDhtPeer(peerDht);
    }

    /**
     * Translates a {@link DhtPeerAddress} to a {@link PeerAddress}
     *
     * @param address
     * @return
     */
    private static PeerAddress peerAddressFromDhtPeerAddress(DhtPeerAddress address) {
        return new PeerAddress(Number160s.fromCwebId(address.getId()), new InetSocketAddress(
                address.getHostAndPort().getHostText(), address.getHostAndPort().getPort()));
    }

    public PeerDHT getUnmanaged() {
        return peerDht;
    }

    @Override
    public ListenableFuture<Void> setReplication(int replicationFactor) {
        // FIXME Make this wait for replication to successfully set
        if (replicationFactor < 0) {
            throw new IllegalArgumentException("replicationFactor must be non-negative.");
        }
        if (replication.isPresent()) {
            if (replication.get().replicationFactor().replicationFactor() == replicationFactor) {
                return Futures.immediateFuture(null);
            }
            replication.get().shutdown();
        }
        if (replicationFactor > 0) {
            replication = Optional
                    .of(new IndirectReplication(peerDht).replicationFactor(replicationFactor));
            replication.get().start();
            return Futures.immediateFuture(null);
        } else {
            replication = Optional.empty();
            return Futures.immediateFuture(null);
        }
    }

    /**
     * Bootstraps from raw {@link PeerAddress} instead
     *
     * @param peerAddresses
     * @return
     */
    public ListenableFuture<Void> bootstrapRawTo(Collection<PeerAddress> peerAddresses) {
        return new BaseFutureAsListenableFuture<Void, FutureBootstrap>(
                peerDht.peer().bootstrap().bootstrapTo(peerAddresses).start()) {

            @Override
            protected Void toValueAfterGet() {
                return null;
            }
        };
    }

    /**
     * Bootstraps from {@link PeerAddress} instead
     *
     * @param peerAddresses
     * @return
     */
    public void bootstrapRawToSync(Collection<PeerAddress> peerAddresses) {
        peerDht.peer().bootstrap().bootstrapTo(peerAddresses).start().awaitUninterruptibly();
    }

    @Override
    public ListenableFuture<Void> bootstrapTo(DhtPeerAddress address) {
        return new BaseFutureAsListenableFuture<Void, FutureBootstrap>(peerDht.peer().bootstrap()
                .peerAddress(peerAddressFromDhtPeerAddress(address)).start()) {
            @Override
            protected Void toValueAfterGet() {
                return null;
            }
        };
    }

    @Override
    public ListenableFuture<Void> bootstrapTo(Collection<DhtPeerAddress> addresses) {
        if (addresses == null) {
            throw new IllegalArgumentException("addresses must be non-null");
        }
        return bootstrapRawTo(
                addresses.stream().map(ManagedPeerDhtPeer::peerAddressFromDhtPeerAddress)
                        .collect(Collectors.toList()));
    }

    @Override
    public void bootstrapToSync(DhtPeerAddress address) {
        peerDht.peer().bootstrap().peerAddress(peerAddressFromDhtPeerAddress(address)).start()
                .awaitUninterruptibly();
    }

    @Override
    public void bootstrapToSync(Collection<DhtPeerAddress> addresses) {
        if (addresses == null) {
            throw new IllegalArgumentException("addresses must be non-null");
        }
        bootstrapRawToSync(addresses.stream().map(ManagedPeerDhtPeer::peerAddressFromDhtPeerAddress)
                .collect(Collectors.toList()));
    }

    public PeerAddress getRawAddress() {
        return peerDht.peerAddress();
    }

    @Override
    public DhtPeerAddress getAddress() {
        return address;
    }

    @Override
    public ListenableFuture<Void> shutdown() {
        return new BaseFutureAsListenableFuture<Void, BaseFuture>(peerDht.shutdown()) {
            @Override
            protected Void toValueAfterGet() {
                return null;
            }
        };
    }
}
