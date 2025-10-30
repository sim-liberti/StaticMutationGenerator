package org.unina.core.rules;

import org.jsoup.nodes.Element;
import org.unina.data.MutationRuleId;
import org.unina.data.ObjectType;
import org.unina.core.MutationRule;

public class TagInsertionRule  implements MutationRule {
    @Override
    public boolean ApplyMutation(Element targetElement) {
        String targetElementName = targetElement.tagName();

        if (targetElementName.equalsIgnoreCase("html") ||
            targetElementName.equalsIgnoreCase("body") ||
            targetElementName.equalsIgnoreCase("head")){
            return false;
        }

        // TODO: Controllare se corretta - nel paper sembra fatta in modo diverso
        targetElement.after("<div class='inserted-sibling'></div>");

        return true;
    }

    @Override
    public String mutationName() { return "tag_ins_mut"; }

    @Override
    public ObjectType objectType() {
        return ObjectType.Tag;
    }

    @Override
    public MutationRuleId mutationId() {
        return MutationRuleId.k;
    }
}
