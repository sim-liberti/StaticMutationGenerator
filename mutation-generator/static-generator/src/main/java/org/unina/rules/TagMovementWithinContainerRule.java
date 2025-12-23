package org.unina.rules;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.unina.core.MutationResult;
import org.unina.data.MutationRuleId;
import org.unina.data.MutationTagType;
import org.unina.util.RandomSelector;
import org.unina.core.MutationRule;

import java.util.ArrayList;
import java.util.List;

public class TagMovementWithinContainerRule  implements MutationRule {
    @Override
    public MutationResult ApplyMutation(Element targetElement) {
        Element parent = targetElement.parent();

        if (parent == null || parent.childrenSize() <= 1) {
            return new  MutationResult(false, "The parent is null or empty", null);
        }

        String targetElementName = targetElement.tagName();

        if (targetElementName.equalsIgnoreCase("html") ||
                targetElementName.equalsIgnoreCase("body") ||
                targetElementName.equalsIgnoreCase("head")){
            return new MutationResult(false, "Target element with " + targetElementName + " tag cannot be mutated", null);
        }

        int randomIndex = RandomSelector.getInstance().GetRandomItemFromCollection(targetElement.siblingElements()).elementSiblingIndex();

        targetElement.remove();
        parent.insertChildren(randomIndex, targetElement);

        List<Document> mutatedDocuments = new ArrayList<>();
        mutatedDocuments.add(targetElement.ownerDocument());

        return new MutationResult(true, "", mutatedDocuments);
    }

    @Override
    public String mutationName() { return "tag_mov_container_mut"; }

    @Override
    public MutationTagType objectType() {
        return MutationTagType.Tag;
    }

    @Override
    public MutationRuleId mutationId() {
        return MutationRuleId.f;
    }
}
