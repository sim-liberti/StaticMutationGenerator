package org.unina.core.rules;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.unina.core.MutationResult;
import org.unina.data.Config;
import org.unina.data.MutationRuleId;
import org.unina.data.MutationTagType;
import org.unina.data.ComponentMetadata;
import org.unina.util.FileBrowser;
import org.unina.util.RandomSelector;
import org.unina.core.MutationRule;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
            return new MutationResult(false, "IOException: " + e.getMessage());
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
        int randomInsertionIndex = 0;
        if (!randomCandidateChildren.isEmpty()) {
            randomInsertionIndex = RandomSelector.GetInstance().GetRandomItemFromCollection(randomCandidateChildren).elementSiblingIndex();
        }

        targetElement.remove();
        randomCandidate.insertChildren(randomInsertionIndex, targetElement);

        FileBrowser.saveMutationToFile(destinationDocument,
                randomComponent.getFileName().toString(),
                randomComponent.toAbsolutePath().toString());

        return new MutationResult(true, "");
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



//    public MutationResult ApplyMutation(Element targetElement) {
//        Document ownerDocument = targetElement.ownerDocument();
//        if (ownerDocument == null) {
//            return new MutationResult(false, "Target element has no document");
//        }
//
//        Path currentHtmlPath = Paths.get(ownerDocument.location());
//        String fileName = currentHtmlPath.getFileName().toString();
//        String componentName = fileName.replace(".html", "");
//        Path currentTsPath = currentHtmlPath.resolveSibling(componentName + ".ts");
//        if (!Files.exists(currentTsPath)) {
//            return new MutationResult(false, "Traget .ts file does not exist");
//        }
//
//        ComponentMetadata componentMetadata = new ComponentMetadata(currentTsPath, currentHtmlPath);
//        try {
//            componentMetadata.buildComponentMetadata();
//        } catch (IOException e) {
//            return new  MutationResult(false, "Error building component metadata: " + e.getMessage());
//        }
//
//        Set<Path> candidateComponents = new HashSet<>();
//        candidateComponents.addAll(componentMetadata.getChildren());
//        candidateComponents.addAll(componentMetadata.getParents());
//        candidateComponents.addAll(componentMetadata.getSiblings());
//        if (candidateComponents.isEmpty()) {
//            return new MutationResult(false, "No valid candidate component found");
//        }
//
//        return new MutationResult(true, "");
//    }