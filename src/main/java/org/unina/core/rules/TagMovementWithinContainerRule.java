package org.unina.core.rules;

import org.jsoup.nodes.Element;
import org.unina.data.MutationRuleId;
import org.unina.data.ObjectType;
import org.unina.util.RandomSelector;
import org.unina.core.MutationRule;

public class TagMovementWithinContainerRule  implements MutationRule {
    @Override
    public boolean ApplyMutation(Element targetElement) {
        Element parent = targetElement.parent();

        if (parent == null || parent.childrenSize() <= 1) {
            return false;
        }

        String targetElementName = targetElement.tagName();

        if (targetElementName.equalsIgnoreCase("html") ||
                targetElementName.equalsIgnoreCase("body") ||
                targetElementName.equalsIgnoreCase("head")){
            return false;
        }

        int randomIndex = RandomSelector.GetInstance().GetRandomItemFromCollection(targetElement.siblingElements()).elementSiblingIndex();

        targetElement.remove();
        parent.insertChildren(randomIndex, targetElement);

        return true;
    }

    @Override
    public String mutationName() { return "tag_mov_container_mut"; }

    @Override
    public ObjectType objectType() {
        return ObjectType.Tag;
    }

    @Override
    public MutationRuleId mutationId() {
        return MutationRuleId.f;
    }
}
