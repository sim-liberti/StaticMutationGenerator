package org.unina.core.rules;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.unina.data.MutationRuleId;
import org.unina.data.ObjectType;
import org.unina.data.ComponentMetadata;
import org.unina.util.RandomSelector;
import org.unina.core.MutationRule;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TagMovementBetweenTemplatesRule  implements MutationRule {
    @Override
    public boolean ApplyMutation(Element targetElement) {
        Document ownerDocument = targetElement.ownerDocument();
        if (ownerDocument == null) {
            return false;
        }

        Path currentHtmlPath = Paths.get(ownerDocument.location());
        String fileName = currentHtmlPath.getFileName().toString();
        String componentName = fileName.replace(".html", "");
        Path currentTsPath = currentHtmlPath.resolveSibling(componentName + ".ts");
        if (!Files.exists(currentTsPath)) {
            return false;
        }

        ComponentMetadata componentMetadata = new ComponentMetadata(currentTsPath, currentHtmlPath);
        try {
            componentMetadata.buildComponentMetadata();
        } catch (IOException e) {
            return false;
        }

        Set<Path> candidateComponents = new HashSet<>();
        candidateComponents.addAll(componentMetadata.getChildren());
        candidateComponents.addAll(componentMetadata.getParents());
        candidateComponents.addAll(componentMetadata.getSiblings());
        if (candidateComponents.isEmpty()) {
            return false;
        }

        Path randomComponent = RandomSelector.GetInstance().GetRandomItemFromCollection(candidateComponents);

        // After collecting all candidate components, it replicates the logic of TagMovementToAnyHtmlTreePointRule,
        // to make the mutation even more effective, but using the destinationDocument.
        Document destinationDocument;
        try{
             destinationDocument = Jsoup.parse(randomComponent.toFile(), "UTF-8");
        } catch (IOException e){
            return false;
        }

        List<Element> allElements = destinationDocument.getAllElements();
        allElements.removeIf(candidate -> !isValidTarget(candidate, targetElement));
        if (allElements.isEmpty()) {
            return false;
        }

        Element randomCandidate = RandomSelector.GetInstance().GetRandomItemFromCollection(allElements);
        List<Element> randomCandidateChildren = randomCandidate.children();
        int randomInsertionIndex = RandomSelector.GetInstance().GetRandomItemFromCollection(randomCandidateChildren).elementSiblingIndex();

        targetElement.remove();
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
    public String mutationName() { return "tag_mov_temp_mut"; }

    @Override
    public ObjectType objectType() {
        return ObjectType.Tag;
    }

    @Override
    public MutationRuleId mutationId() {
        return MutationRuleId.h;
    }
}
