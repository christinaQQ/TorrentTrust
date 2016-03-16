package moe.cdn.cweb;

import moe.cdn.cweb.TorrentTrustProtos.User;

/**
 * @author eyeung
 */
public interface TrustGenerator {
    /**
     * Calculates the correlation between users based on their votes. Formula can be found in
     * Credence paper. Ignores users who vote all bad or all good.
     *
     * @param a the user making this call
     * @param b the user being queried
     * @return a double representing the amount of correlation between tahem
     */
    double correlationCoefficient(User a, User b);

    /**
     * Determines whether a user is in the calling user's direct network (1 degree away).
     *
     * @param a the user making this call
     * @param b the user being queried
     * @return 1 if {@code b} is in the direct network; otherwise, 0.
     */
    double trustCoefficientDirect(User a, User b);

    /**
     * Determines whether a target user is in the source user's extended network (anywhere in the
     * connected component).
     *
     * @param src the source user
     * @param tgt the target user
     * @return shortest path length to {@code tgt} if {@code tgt} is in the extended network;
     * otherwise, 0
     */
    double trustCoefficientNetwork(User src, User tgt);

    /**
     * Determines whether a target user is in the source user's network within a certain number
     * of hops.
     *
     * @param src     the source user
     * @param tgt     the target user
     * @param numHops maximum allowed steps to reach {@code tgt}
     * @return the shortest path length from {@code src} to {@code tgt} if {@code tgt} in {@code
     * src}'s network; otherwise 0
     */
    double trustCoefficientNumSteps(User src, User tgt, int numHops);

}
