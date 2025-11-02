package org.unina.core.rules;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.unina.core.MutationResult;
import org.unina.data.MutationRuleId;
import org.unina.data.MutationTagType;
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
    public MutationResult ApplyMutation(Element targetElement) {
        Document ownerDocument = targetElement.ownerDocument();
        if (ownerDocument == null) {
            return new MutationResult(false, "Target element has no document");
        }

        Path currentHtmlPath = Paths.get(ownerDocument.location());
        String fileName = currentHtmlPath.getFileName().toString();
        String componentName = fileName.replace(".html", "");
        Path currentTsPath = currentHtmlPath.resolveSibling(componentName + ".ts");
        if (!Files.exists(currentTsPath)) {
            return new MutationResult(false, "Traget .ts file does not exist");
        }

        ComponentMetadata componentMetadata = new ComponentMetadata(currentTsPath, currentHtmlPath);
        try {
            componentMetadata.buildComponentMetadata();
        } catch (IOException e) {
            return new  MutationResult(false, "Error building component metadata: " + e.getMessage());
        }

        Set<Path> candidateComponents = new HashSet<>();
        candidateComponents.addAll(componentMetadata.getChildren());
        candidateComponents.addAll(componentMetadata.getParents());
        candidateComponents.addAll(componentMetadata.getSiblings());
        if (candidateComponents.isEmpty()) {
            return new MutationResult(false, "No valid candidate component found");
        }

        Path randomComponent = RandomSelector.GetInstance().GetRandomItemFromCollection(candidateComponents);

        // After collecting all candidate components, it replicates the logic of TagMovementToAnyHtmlTreePointRule,
        // to make the mutation even more effective, but using the destinationDocument.
        Document destinationDocument;
        try{
             destinationDocument = Jsoup.parse(randomComponent.toFile(), "UTF-8");
        } catch (IOException e){
            return new MutationResult(false, "Error parsing the destination document: " + e.getMessage());
        }

        Elements allElements = destinationDocument.getAllElements();
        allElements.removeIf(candidate -> !isValidTarget(candidate, targetElement));
        if (allElements.isEmpty()) {
            return new MutationResult(false, "Destination document has no valid candidate elements");
        }

        Element randomCandidate = RandomSelector.GetInstance().GetRandomItemFromCollection(allElements);
        Elements randomCandidateChildren = randomCandidate.children();
        int randomInsertionIndex = RandomSelector.GetInstance().GetRandomItemFromCollection(randomCandidateChildren).elementSiblingIndex();

        targetElement.remove();
        randomCandidate.insertChildren(randomInsertionIndex, targetElement);

        return new MutationResult(true, "");
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
    public MutationTagType objectType() {
        return MutationTagType.Tag;
    }

    @Override
    public MutationRuleId mutationId() {
        return MutationRuleId.h;
    }
}
