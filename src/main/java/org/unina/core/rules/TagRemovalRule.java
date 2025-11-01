package org.unina.core.rules;

import org.jsoup.nodes.Element;
import org.unina.core.MutationResult;
import org.unina.data.MutationRuleId;
import org.unina.data.MutationTagType;
import org.unina.core.MutationRule;

public class TagRemovalRule  implements MutationRule {
    @Override
    public MutationResult ApplyMutation(Element targetElement) {
        if (targetElement.parent() == null) {
            return new  MutationResult(false, "The parent is null");
        }

        String targetElementName = targetElement.tagName();

        if (targetElementName.equalsIgnoreCase("html") ||
            targetElementName.equalsIgnoreCase("body") ||
            targetElementName.equalsIgnoreCase("head")){
            return new MutationResult(false, "Target element with " + targetElementName + " tag cannot be mutated" );
        }

        targetElement.unwrap();

        return new MutationResult(true, "");
    }

    @Override
    public String mutationName() { return "tag_rem_mut"; }

    @Override
    public MutationTagType objectType() {
        return MutationTagType.Tag;
    }

    @Override
    public MutationRuleId mutationId() {
        return MutationRuleId.i;
    }
}
