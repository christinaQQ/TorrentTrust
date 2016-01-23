package cdn.moe.cweb;

import javax.swing.text.AbstractDocument;
import java.util.HashMap;
import java.util.List;

/**
 * @author eyeung
 */
public class TrustGenerator {

    public double correlationCoefficient(User a, User b, CwebApi api) {
        HashMap<ContentObject, Integer> overlapping_votes =
                new HashMap<ContentObject, Integer>();
        List<Vote> A_votes = api.getVotesForUser(a);
        List<Vote> B_votes = api.getVotesForUser(b);
        for (Vote v : A_votes) {
            ContentObject co = v.getContentObject();
             // temporary use
            int assertion = v.getAssertions().get(0).overallContentAssertion();
            overlapping_votes.put(co,assertion);
        }
        float positive_a = 0;
        float positive_b = 0;
        float positive_both = 0;
        for (Vote v: B_votes) {
            ContentObject co = v.getContentObject();
            int b_assertion = v.getAssertions().get(0).overallContentAssertion();
            if (b_assertion > 0) {
                positive_b += 1;
            }
            if (overlapping_votes.containsKey(co)) {
                if (overlapping_votes.get(co) > 0) {
                    positive_a +=1;
                    if (b_assertion > 0) {
                        positive_both +=1;
                    }
                }
            }
            overlapping_votes.put(co, b_assertion);
        }
        positive_a = positive_a / (float) A_votes.size();
        positive_b = positive_b / (float) B_votes.size();
        positive_both = positive_both /
                (float) overlapping_votes.keySet().size();

        double theta = (positive_both - positive_a * positive_b ) /
                Math.sqrt(positive_a * (1-positive_a) * positive_b * (1-positive_b));
        return theta;
    }
}
