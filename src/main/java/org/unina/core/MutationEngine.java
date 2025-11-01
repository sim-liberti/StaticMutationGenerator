package org.unina.core;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.unina.core.matchers.TagMatcherFactory;
import org.unina.core.rules.*;
import org.unina.data.Config;
import org.unina.data.ElementExtension;
import org.unina.util.RandomSelector;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;

public class MutationEngine {

    private static final List<MutationRule> mutationRules = new ArrayList<>();
    private static final Map<String, Integer> targetElements = new HashMap<>();

    public static void Run(Config jsonConfig) throws IOException {
        initializeRules();

        if (jsonConfig.seed.isEmpty())
            RandomSelector.initialize();
        else
            RandomSelector.initialize(Integer.parseInt(jsonConfig.seed));

        Document document = Jsoup.parse(Paths.get(jsonConfig.inputFile).toFile(), "UTF-8");
        Element targetElement = findElement(jsonConfig, document);

        for (MutationRule mutation : mutationRules) {
            initializeTargets(targetElement);
            for (Map.Entry<String, Integer> entry : targetElements.entrySet()) {
                Document cloneDocument = document.clone();
                Element targetElementClone = cloneDocument.getAllElements().get(entry.getValue());

                String htmlBefore = cloneDocument.html();
                boolean elementWasMutated = mutation.ApplyMutation(targetElementClone);
                String htmlAfter = cloneDocument.html();

                System.out.println("====== Mutation Applied: " + mutation.mutationId() + " ======");
                System.out.println("Target: " + entry.getKey());
                System.out.println("Result: " + (elementWasMutated ? "Success" : "Failure"));
                if (elementWasMutated && !htmlBefore.equals(htmlAfter)){
                    String fileName = String.format("%s_%s_%s.html",
                            mutation.mutationId().name(),
                            entry.getKey(),
                            mutation.mutationName());
                    saveMutationToFile(cloneDocument, fileName, jsonConfig.outputDirectory);
                }
                System.out.println("=================================\n");
            }
        }
    }

    private static void initializeRules(){
        mutationRules.add(new AttributeValueModificationRule());
        mutationRules.add(new AttributeRemovalRule());
        mutationRules.add(new AttributeIdentifierModificationRule());
        mutationRules.add(new TextContentModificationRule());
        mutationRules.add(new TextContentRemovalRule());
        mutationRules.add(new TagMovementWithinContainerRule());
        mutationRules.add(new TagMovementToAnyHtmlTreePointRule());
        // mutationRules.add(new TagMovementBetweenTemplatesRule());
        mutationRules.add(new TagRemovalRule());
        mutationRules.add(new TagTypeModificationRule());
        mutationRules.add(new TagInsertionRule());
    }

    private static void initializeTargets(Element element) {
        Elements elements = Objects.requireNonNull(element.ownerDocument()).getAllElements();
        if (elements.isEmpty()) {
            throw new RuntimeException("No elements found in target document.");
        }
        targetElements.clear();
        targetElements.put("alpha", elements.indexOf(element));
        if (element.parent() != null)
            targetElements.put("beta", elements.indexOf(element.parent()));
        if (ElementExtension.getSibling(element) != null)
            targetElements.put("delta", elements.indexOf(ElementExtension.getSibling(element)));
        if (ElementExtension.getAncestor(element) != null)
            targetElements.put("gamma", elements.indexOf(ElementExtension.getAncestor(element)));
        if (ElementExtension.getContainingComponent(element) != null)
            targetElements.put("epsilon", elements.indexOf(ElementExtension.getContainingComponent(element)));
    }

    private static Element findElement(Config config, Document document) {
        TagMatcher matcher = TagMatcherFactory.fromConfig(config.matcher);
        return document.getAllElements()
                .stream()
                .filter(matcher::matches)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No target tag found"));
    }

    private static void saveMutationToFile(Document document, String filename, String outputDirectory){
        try {
            Path outputPath = Paths.get(outputDirectory + "/" + filename);
            String content = document.html();
            Files.write(outputPath, content.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            System.out.println(" -> Saved: " + filename);
        } catch (IOException e) {
            System.err.println("Error saving mutant file " + filename + ": " + e.getMessage());
        }
    }
}

