package org.unina.core.rules;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.unina.core.MutationResult;
import org.unina.data.MutationRuleId;
import org.unina.data.MutationTagType;
import org.unina.util.RandomSelector;
import org.unina.core.MutationRule;

import java.util.ArrayList;
import java.util.List;

public class TagMovementToAnyHtmlTreePointRule implements MutationRule {
    @Override
    public MutationResult ApplyMutation(Element targetElement) {
        Document document = targetElement.ownerDocument();
        assert document != null;

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

        List<Element> allElements = new ArrayList<>(document.getAllElements());
        allElements.removeIf(candidate -> !isValidTarget(candidate, targetElement));
        if (allElements.isEmpty()) {
            return new MutationResult(false, "No valid candidate elements have been found", null);
        }

        Element randomCandidate = RandomSelector.GetInstance().GetRandomItemFromCollection(allElements);

        targetElement.remove();

        int randomInsertionIndex = 0;
        List<Element> randomCandidateChildren = randomCandidate.children();
        if (!randomCandidateChildren.isEmpty()) {
            randomInsertionIndex = RandomSelector.GetInstance().GetRandomItemFromCollection(randomCandidateChildren).elementSiblingIndex();
        }

        randomCandidate.insertChildren(randomInsertionIndex, targetElement);

        List<Document> mutatedDocuments = new ArrayList<>();
        mutatedDocuments.add(targetElement.ownerDocument());

        return new MutationResult(true, "", mutatedDocuments);
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
    public String mutationName() { return "tag_mov_html_mut"; }

    @Override
    public MutationTagType objectType() {
        return MutationTagType.Tag;
    }

    @Override
    public MutationRuleId mutationId() {
        return MutationRuleId.g;
    }
}
