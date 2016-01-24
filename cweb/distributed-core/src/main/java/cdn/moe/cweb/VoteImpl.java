package cdn.moe.cweb;

import java.util.List;

/**
 * @author davix
 */
public class VoteImpl implements Vote {

    private final List<Assertion> assertions;
    private final ContentObject content;

    public VoteImpl(List<Assertion> assertions,
                    ContentObject content) {
        this.assertions = assertions;
        this.content = content;
    }

    @Override
    public List<Assertion> getAssertions() {
        return assertions;
    }

    @Override
    public ContentObject getContentObject() {
        return content;
    }
}
