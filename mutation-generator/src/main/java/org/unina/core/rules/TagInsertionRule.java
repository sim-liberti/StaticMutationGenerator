package org.unina.core.rules;

import org.jsoup.nodes.Element;
import org.unina.core.MutationResult;
import org.unina.data.MutationRuleId;
import org.unina.data.MutationTagType;
import org.unina.core.MutationRule;

public class TagInsertionRule  implements MutationRule {
    @Override
    public MutationResult ApplyMutation(Element targetElement) {
        String targetElementName = targetElement.tagName();

        if (targetElementName.equalsIgnoreCase("html") ||
            targetElementName.equalsIgnoreCase("body") ||
            targetElementName.equalsIgnoreCase("head")){
            return new MutationResult(false, "Target element with " + targetElementName + " tag cannot be mutated" );
        }

        // TODO: Controllare se corretta - nel paper sembra fatta in modo diverso
        targetElement.after("<div class='inserted-sibling'></div>");

        return new MutationResult(true, "");
    }

    @Override
    public String mutationName() { return "tag_ins_mut"; }

    @Override
    public MutationTagType objectType() {
        return MutationTagType.Tag;
    }

    @Override
    public MutationRuleId mutationId() {
        return MutationRuleId.k;
    }
}
