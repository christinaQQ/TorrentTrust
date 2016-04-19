package moe.cdn.cweb.app.api.resources;

import moe.cdn.cweb.TorrentTrustProtos;
import moe.cdn.cweb.app.api.CwebApiEndPoint;
import moe.cdn.cweb.app.dto.TrustRating;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import java.security.InvalidKeyException;
import java.security.SignatureException;
import java.util.Random;
import java.util.concurrent.ExecutionException;

/**
 * @author davix
 */
@Path("object/{hash}")
public class ObjectInfo extends CwebApiEndPoint {

    @GET
    @Path("{algo}")
    public TrustRating getTrustRating(@PathParam("hash") String hash,
                                      @PathParam("algo") String algo) {
        return new TrustRating(new Random().nextDouble(), "rand");
    }

    @POST
    @Path("up")
    public boolean upVote(@PathParam("hash") String hash) throws SignatureException,
            InvalidKeyException, ExecutionException, InterruptedException {
        getCwebVoteApi()
                .castVote(TorrentTrustProtos.Vote.newBuilder()
                        .addAssertion(TorrentTrustProtos.Vote.Assertion.newBuilder()
                                .setRating(TorrentTrustProtos.Vote.Assertion.Rating.GOOD))
                        .build())
                .get();
        return true;
    }

    @POST
    @Path("down")
    public boolean downVote(@PathParam("hash") String hash) throws SignatureException,
            InvalidKeyException, ExecutionException, InterruptedException {
        getCwebVoteApi()
                .castVote(TorrentTrustProtos.Vote.newBuilder()
                        .addAssertion(TorrentTrustProtos.Vote.Assertion.newBuilder()
                                .setRating(TorrentTrustProtos.Vote.Assertion.Rating.BAD))
                        .build())
                .get();
        return true;
    }
}
