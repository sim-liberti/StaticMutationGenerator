package org.unina.core.rules;

import org.jsoup.nodes.Element;
import org.unina.data.MutationRuleId;
import org.unina.data.ObjectType;
import org.unina.core.MutationRule;

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
    public String mutationName() { return "tag_type_mod_mut"; }

    @Override
    public ObjectType objectType() {
        return ObjectType.Tag;
    }

    @Override
    public MutationRuleId mutationId() {
        return MutationRuleId.j;
    }
}
