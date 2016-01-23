package cdn.moe.cweb;

import java.util.List;

/**
 * @author davix
 */
public interface Vote {

    List<Assertion> getAssertions();
    ContentObject getContentObject();
}
