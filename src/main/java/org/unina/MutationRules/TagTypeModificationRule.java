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
        return switch (element.tagName().toLowerCase()) {
            case "a" -> "button";
            case "input" -> "div";
            case "div" -> "span";
            case "span" -> "div";
            case "h1" -> "h2";
            case "h2" -> "h3";
            default -> "div";
        };
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
