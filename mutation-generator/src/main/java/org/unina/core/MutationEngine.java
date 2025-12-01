package org.unina.core;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.unina.core.matchers.TagMatcherFactory;
import org.unina.core.rules.*;
import org.unina.data.Config;
import org.unina.data.ElementExtension;
import org.unina.util.FileBrowser;
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
        initializeRules(jsonConfig);

        if (jsonConfig.seed.isEmpty())
            RandomSelector.initialize();
        else
            RandomSelector.initialize(Integer.parseInt(jsonConfig.seed));

        final Document document = Jsoup.parse(Paths.get(jsonConfig.inputFile).toFile(), "UTF-8");
        final Element targetElement = findElement(jsonConfig, document);

        try {
            for (MutationRule mutation : mutationRules) {
                initializeTargets(targetElement);
                for (Map.Entry<String, Integer> entry : targetElements.entrySet()) {
                    Document cloneDocument = document.clone();
                    Element targetElementClone = cloneDocument.getAllElements().get(entry.getValue());

                    String htmlBefore = cloneDocument.html();
                    MutationResult mutationResult = mutation.ApplyMutation(targetElementClone);
                    String htmlAfter = cloneDocument.html();

                    System.out.println("============ Mutation Applied: " + mutation.mutationId() + " ============");
                    System.out.println("Target: " + entry.getKey());
                    System.out.println("Result: " + (mutationResult.mutationApplied ? "Success" : "Failure"));
                    if (mutationResult.mutationApplied && !htmlBefore.equals(htmlAfter)){
                        String fileName = String.format("%s_%s_%s.html",
                                mutation.mutationId().name(),
                                entry.getKey(),
                                mutation.mutationName());
                        FileBrowser.saveMutationToFile(cloneDocument, fileName, jsonConfig.outputDirectory);
                    } else {
                        System.out.println("Error: " + mutationResult.failureMessage);
                    }
                    System.out.println("=============================================\n");
                }
            }
        } catch(Exception e) {

        } finally {

        }

    }

    private static void initializeRules(Config config){
//        mutationRules.add(new AttributeValueModificationRule());
//        mutationRules.add(new AttributeRemovalRule());
//        mutationRules.add(new AttributeIdentifierModificationRule());
//        mutationRules.add(new TextContentModificationRule());
//        mutationRules.add(new TextContentRemovalRule());
//        mutationRules.add(new TagMovementWithinContainerRule());
//        mutationRules.add(new TagMovementToAnyHtmlTreePointRule());
          mutationRules.add(new TagMovementBetweenTemplatesRule(Paths.get(config.repositoryRootPath)));
//        mutationRules.add(new TagRemovalRule());
//        mutationRules.add(new TagTypeModificationRule());
//        mutationRules.add(new TagInsertionRule());
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
}

