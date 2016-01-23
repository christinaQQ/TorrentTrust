package cdn.moe.cweb;

import java.util.List;

/**
 * @author davix
 */
public interface CwebApi {

    List<Vote> getVotesForUser();

    List<User> getTrustedUsersForUser(User user);

    List<User> getVotedUsersForObject(String objectHash);

    List<Vote> getVotesForUserForObject(String objectHash, User user);

}
