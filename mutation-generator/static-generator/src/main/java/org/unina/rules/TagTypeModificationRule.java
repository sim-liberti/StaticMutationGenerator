package org.unina.rules;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.unina.core.MutationResult;
import org.unina.data.MutationRuleId;
import org.unina.data.MutationTagType;
import org.unina.core.MutationRule;

import java.util.ArrayList;
import java.util.List;

public class TagTypeModificationRule  implements MutationRule {
    @Override
    public MutationResult ApplyMutation(Element targetElement) {
        String targetElementName = targetElement.tagName();

        if (targetElementName.equalsIgnoreCase("html") ||
            targetElementName.equalsIgnoreCase("body") ||
            targetElementName.equalsIgnoreCase("head")){
            return new MutationResult(false, "Target element with " + targetElementName + " tag cannot be mutated", null);
        }

        targetElement.tagName(GetNewElementTagName(targetElement));

        List<Document> mutatedDocuments = new ArrayList<>();
        mutatedDocuments.add(targetElement.ownerDocument());

        return new MutationResult(true, "", mutatedDocuments);
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
    public MutationTagType objectType() {
        return MutationTagType.Tag;
    }

    @Override
    public MutationRuleId mutationId() {
        return MutationRuleId.j;
    }
}
