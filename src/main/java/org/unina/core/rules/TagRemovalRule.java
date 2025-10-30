package org.unina.core.rules;

import org.jsoup.nodes.Element;
import org.unina.data.MutationRuleId;
import org.unina.data.ObjectType;
import org.unina.core.MutationRule;

public class TagRemovalRule  implements MutationRule {
    @Override
    public boolean ApplyMutation(Element targetElement) {
        if (targetElement.parent() == null) {
            return false;
        }

        String targetElementName = targetElement.tagName();

        if (targetElementName.equalsIgnoreCase("html") ||
            targetElementName.equalsIgnoreCase("body") ||
            targetElementName.equalsIgnoreCase("head")){
            return false;
        }

        targetElement.unwrap();

        return true;
    }

    @Override
    public ObjectType objectType() {
        return ObjectType.Tag;
    }

    @Override
    public MutationRuleId mutationId() {
        return MutationRuleId.i;
    }
}
