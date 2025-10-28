package org.unina.MutationRules;

import org.jsoup.nodes.Element;
import org.unina.Data.MutationRuleId;
import org.unina.Data.ObjectType;

public class TagInsertionRule  implements MutationRule {
    @Override
    public boolean ApplyMutation(Element targetElement) {
        return false;
    }

    @Override
    public ObjectType objectType() {
        return ObjectType.Tag;
    }

    @Override
    public MutationRuleId mutationId() {
        return MutationRuleId.k;
    }
}
