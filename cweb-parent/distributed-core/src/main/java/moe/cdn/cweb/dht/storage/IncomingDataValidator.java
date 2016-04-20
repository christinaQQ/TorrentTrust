package moe.cdn.cweb.dht.storage;

import com.google.protobuf.InvalidProtocolBufferException;
import moe.cdn.cweb.TorrentTrustProtos;
import moe.cdn.cweb.TorrentTrustProtos.SignedVote;
import moe.cdn.cweb.TorrentTrustProtos.SignedVoteHistory;
import moe.cdn.cweb.dht.annotations.UserDomain;
import moe.cdn.cweb.dht.annotations.VoteDomain;
import moe.cdn.cweb.dht.annotations.VoteHistoryDomain;
import moe.cdn.cweb.dht.security.CwebSignatureValidationService;
import moe.cdn.cweb.security.utils.Representations;
import net.tomp2p.peers.Number160;
import net.tomp2p.storage.Data;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;

class IncomingDataValidator {
    private static final Logger logger = LogManager.getLogger();
    private final Number160 voteDomainKey;
    private final Number160 userDomainKey;
    private final Number160 voteHistoryDomainKey;

    private final CwebSignatureValidationService cwebSignatureValidationService;

    @Inject
    public IncomingDataValidator(@VoteDomain Number160 voteDomainKey,
            @UserDomain Number160 userDomainKey,
            @VoteHistoryDomain Number160 voteHistoryDomainKey,
            CwebSignatureValidationService cwebSignatureValidationService) {
        this.voteDomainKey = voteDomainKey;
        this.userDomainKey = userDomainKey;
        this.voteHistoryDomainKey = voteHistoryDomainKey;
        this.cwebSignatureValidationService = cwebSignatureValidationService;
    }


    public boolean validate(Number160 domainKey, Data data) {
        // TODO: What is a sane way to log validation actions?
        logger.info("Validating {}", data);
        if (voteDomainKey.equals(domainKey)) {
            return validateVoteRawData(data);
        }
        if (userDomainKey.equals(domainKey)) {
            return validateUserRawData(data);
        }
        if (voteHistoryDomainKey.equals(domainKey)) {
            return validateVoteHistoryRawData(data);
        }
        logger.warn("Did not belong to acceptable domain. Failed validation.");
        return false;
    }

    private boolean validateVoteRawData(Data data) {
        try {
            SignedVote signedVote = SignedVote.PARSER.parseFrom(data.toBytes());
            logger.info("Validating Vote {}", Representations.asString(signedVote));
            return cwebSignatureValidationService.validateVote(signedVote);
        } catch (InvalidProtocolBufferException e) {
            logger.catching(e);
            return false;
        }
    }

    private boolean validateUserRawData(Data data) {
        try {
            TorrentTrustProtos.SignedUser signedUser =
                    TorrentTrustProtos.SignedUser.PARSER.parseFrom(data.toBytes());
            logger.info("Validating User {}", Representations.asString(signedUser));
            return cwebSignatureValidationService.validateUser(signedUser);
        } catch (InvalidProtocolBufferException e) {
            logger.catching(e);
            return false;
        }
    }

    private boolean validateVoteHistoryRawData(Data data) {
        try {
            SignedVoteHistory signedVoteHistory =
                    SignedVoteHistory.PARSER.parseFrom(data.toBytes());
            logger.info("Validating VoteHistory {}", Representations.asString(signedVoteHistory.getHistory()));
            return cwebSignatureValidationService.validateVoteHistory(signedVoteHistory);
        } catch (InvalidProtocolBufferException e) {
            logger.catching(e);
            return false;
        }
    }
}
