package cdn.moe.cweb;

import java.util.List;

/**
 * @author davix
 */
public class VoteImpl implements Vote {

    private final List<Assertion> assertions;

    public VoteImpl(List<Assertion> assertions) {
        this.assertions = assertions;
    }

    @Override
    public List<Assertion> getAssertions() {
        return assertions;
    }
}
