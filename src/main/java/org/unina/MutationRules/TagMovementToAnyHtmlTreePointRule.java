package org.unina.MutationRules;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.unina.Data.MutationRuleId;
import org.unina.Data.ObjectType;
import org.unina.Utils.RandomSelector;

import java.util.ArrayList;
import java.util.List;

public class TagMovementToAnyHtmlTreePointRule implements MutationRule {
    @Override
    public boolean ApplyMutation(Element targetElement) {
        Document document = targetElement.ownerDocument();
        assert document != null;

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

        List<Element> allElements = new ArrayList<>(document.getAllElements());
        allElements.removeIf(candidate -> !isValidTarget(candidate, targetElement));
        if (allElements.isEmpty()) {
            return false;
        }

        Element randomCandidate = RandomSelector.GetInstance().GetRandomItemFromCollection(allElements);

        targetElement.remove();

        int randomInsertionIndex = 0;
        List<Element> randomCandidateChildren = randomCandidate.children();
        if (!randomCandidateChildren.isEmpty()) {
            randomInsertionIndex = RandomSelector.GetInstance().GetRandomItemFromCollection(randomCandidateChildren).elementSiblingIndex();
        }

        randomCandidate.insertChildren(randomInsertionIndex, targetElement);

        return true;
    }

    private boolean isValidTarget(Element candidate, Element target) {
        String name = candidate.tagName().toLowerCase();

        if (name.equals("html") || name.equals("head")) return false;
        if (candidate == target) return false;
        if (candidate == target.parent()) return false;
        if (candidate.parents().contains(target)) return false;

        return true;
    }

    @Override
    public ObjectType objectType() {
        return ObjectType.Tag;
    }

    @Override
    public MutationRuleId mutationId() {
        return MutationRuleId.g;
    }
}
