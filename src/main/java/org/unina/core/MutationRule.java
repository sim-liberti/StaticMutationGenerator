package org.unina.core;

import org.jsoup.nodes.Element;
import org.unina.data.MutationRuleId;
import org.unina.data.MutationTagType;

public interface MutationRule {
    boolean ApplyMutation(Element targetElement);
    String mutationName();
    MutationTagType objectType();
    MutationRuleId mutationId();
}
