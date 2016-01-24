package cdn.moe.cweb;

/**
 * @author eyeung
 */



public interface TrustGenerator {
    /**
     * Calculates the correlation between users based on their votes.
     * Formula can be found in Credence paper.
     * Ignores users who vote all bad or all good.
     *
     * @param a the User who is calling
     * @param b the User who is being queried
     * @return a double representing the amount of correlation between them
     */
    double correlationCoefficient(User a, User b);


    /**
     * Determines whether a user is in the calling user's
     * direct network (1 degree away)
     *
     * @param a  User calling
     * @param b  User being queried
     * @return 1 if b in direct network, 0 otherwise.
     */
    double trustCoefficientDirect(User a, User b);

    /**
     * Determines whether a user is in the calling user's extended network
     * (anywhere in the connected component)
     *
     * @param a User calling
     * @param b User being queried
     * @return shortest path length to b if b in extended network, 0 otherwise.
     */
    double trustCoefficientNetwork(User a, User b);

    /**
     * Determines whether a user is in the calling user's network up to n steps
     *
     * @param a User calling
     * @param b User being queried
     * @param n Maximum allowed steps to reach b
     * @return the shortest path length to b if b in this network, 0 otherwise.
     */
    double trustCoefficientNumSteps(User a, User b, int num_steps);

}
