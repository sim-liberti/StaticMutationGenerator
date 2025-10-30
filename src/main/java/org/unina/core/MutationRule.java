package org.unina.core;

import org.jsoup.nodes.Element;
import org.unina.data.MutationRuleId;
import org.unina.data.ObjectType;

public interface MutationRule {
    boolean ApplyMutation(Element targetElement);

    ObjectType objectType();
    MutationRuleId mutationId();
}
