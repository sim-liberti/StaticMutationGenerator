package org.unina.MutationRules;

import org.jsoup.nodes.Element;
import org.unina.Data.MutationRuleId;
import org.unina.Data.ObjectType;
import org.unina.Utils.RandomSelector;

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
    public ObjectType objectType() {
        return ObjectType.Tag;
    }

    @Override
    public MutationRuleId mutationId() {
        return MutationRuleId.f;
    }
}
