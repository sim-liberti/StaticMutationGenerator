package org.unina.core.rules;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.unina.core.MutationResult;
import org.unina.data.MutationRuleId;
import org.unina.data.MutationTagType;
import org.unina.util.RandomSelector;
import org.unina.core.MutationRule;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public class TagMovementBetweenTemplatesRule implements MutationRule {
    private final Path repositoryRootPath;

    public TagMovementBetweenTemplatesRule(Path repositoryRootPath) {
        this.repositoryRootPath = repositoryRootPath;
    }

    @Override
    public MutationResult ApplyMutation(Element targetElement) {
        Set<Path> candidateComponents = new HashSet<>();

        try (Stream<Path> paths = Files.walk(repositoryRootPath)) {
            paths
                .filter(Files::isRegularFile)
                .filter(path -> path.toString().endsWith(".component.html"))
                .forEach(candidateComponents::add);
        } catch (IOException e) {
            return new MutationResult(false, "IOException: " + e.getMessage(), null);
        }

        Path randomComponent = RandomSelector.GetInstance().GetRandomItemFromCollection(candidateComponents);

        // After collecting all candidate components, it replicates the logic of TagMovementToAnyHtmlTreePointRule,
        // to make the mutation even more effective, but using the destinationDocument.
        final Document destinationDocument;
        try{
            destinationDocument = Jsoup.parse(randomComponent.toFile(), "UTF-8");
        } catch (IOException e){
            return new MutationResult(false, "Error parsing the destination document: " + e.getMessage(), null);
        }

        Elements allElements = destinationDocument.getAllElements();
        allElements.clone().removeIf(candidate -> !isValidTarget(candidate, targetElement));
        if (allElements.isEmpty()) {
            return new MutationResult(false, "Destination document has no valid candidate elements", null);
        }

        Element randomCandidate = RandomSelector.GetInstance().GetRandomItemFromCollection(allElements);
        Elements randomCandidateChildren = randomCandidate.children();
        int randomInsertionIndex = 0;
        if (!randomCandidateChildren.isEmpty()) {
            randomInsertionIndex = RandomSelector.GetInstance().GetRandomItemFromCollection(randomCandidateChildren).elementSiblingIndex();
        }

        targetElement.remove();
        randomCandidate.insertChildren(randomInsertionIndex, targetElement);

        List<Document> mutatedDocuments = new ArrayList<>();
        mutatedDocuments.add(targetElement.ownerDocument());
        mutatedDocuments.add(randomCandidate.ownerDocument());

        return new MutationResult(true, "", mutatedDocuments);
    }

    private boolean isValidTarget(Element candidate, Element target) {
        String name = candidate.tagName().toLowerCase();

        return !name.equals("html") && !name.equals("head")
                && candidate != target
                && candidate != target.parent()
                && !candidate.parents().contains(target);
    }

    @Override
    public String mutationName() { return "tag_mov_temp_mut"; }

    @Override
    public MutationTagType objectType() { return MutationTagType.Tag; }

    @Override
    public MutationRuleId mutationId() { return MutationRuleId.h; }
}
