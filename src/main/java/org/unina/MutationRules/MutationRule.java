package org.unina.MutationRules;

import org.jsoup.nodes.Element;
import org.unina.Data.MutationRuleId;
import org.unina.Data.ObjectType;

public interface MutationRule {
    boolean ApplyMutation(Element targetElement);

    ObjectType objectType();
    MutationRuleId mutationId();
}
