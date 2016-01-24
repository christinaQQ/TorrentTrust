package cdn.moe.cweb;

/**
 * Created by evelynfifiyeung on 1/23/16.
 */
public class AssertionFakeImpl implements Assertion {

    private int overallContentAssertion;
    public AssertionFakeImpl(int overall) {
        overallContentAssertion = overall;
    }
    @Override
    public int overallContentAssertion() {
        return this.overallContentAssertion;
    }
}
