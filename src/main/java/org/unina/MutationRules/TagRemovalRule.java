package org.unina.MutationRules;

import org.jsoup.nodes.Element;
import org.unina.Data.MutationRuleId;
import org.unina.Data.ObjectType;

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
