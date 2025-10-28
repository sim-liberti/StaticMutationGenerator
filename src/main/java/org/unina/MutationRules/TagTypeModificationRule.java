package org.unina.MutationRules;

import org.jsoup.nodes.Element;
import org.unina.Data.MutationRuleId;
import org.unina.Data.ObjectType;

public class TagTypeModificationRule  implements MutationRule {
    @Override
    public boolean ApplyMutation(Element targetElement) {
        String targetElementName = targetElement.tagName();

        if (targetElementName.equalsIgnoreCase("html") ||
            targetElementName.equalsIgnoreCase("body") ||
            targetElementName.equalsIgnoreCase("head")){
            return false;
        }

        targetElement.tagName(GetNewElementTagName(targetElement));

        return true;
    }

    private String GetNewElementTagName(Element element) {
        switch (element.tagName().toLowerCase()) {
            case "a":
                return "button";
            case "input":
                return "div";
            case "div":
                return "span";
            case "span":
                return "div";
            case "h1":
                return "h2";
            case "h2":
                return "h3";
            default:
                return "div";
        }
    }

    @Override
    public ObjectType objectType() {
        return ObjectType.Tag;
    }

    @Override
    public MutationRuleId mutationId() {
        return MutationRuleId.j;
    }
}
