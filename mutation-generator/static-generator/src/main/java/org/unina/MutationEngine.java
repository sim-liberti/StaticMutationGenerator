package org.unina;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.ParseSettings;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;
import org.unina.matchers.TagMatcherFactory;
import org.unina.core.*;
import org.unina.rules.*;
import org.unina.data.Config;
import org.unina.data.ElementExtension;
import org.unina.data.MutationDatabase;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class MutationEngine {

    private static final List<MutationRule> mutationRules = new ArrayList<>();
    private static final Map<String, Integer> targetElements = new HashMap<>();

    public static void Run(Config jsonConfig) throws IOException {
        MutationDatabase db = new MutationDatabase();

        initializeRules();
        final Parser parser = Parser.htmlParser();
        parser.settings(new ParseSettings(true, true));
        final String docBaseUri = Paths.get(jsonConfig.inputFile).toAbsolutePath().toString();
        final Document document = Jsoup.parse(Files.readString(Paths.get(jsonConfig.inputFile)), docBaseUri, parser);
        document.outputSettings().prettyPrint(false);
        final Element targetElement = findElement(jsonConfig, document);

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
                    db.saveMutation(entry.getKey(), mutation.mutationName(), mutation.mutationId().name(), mutationResult.mutatedDocuments);
                } else {
                    System.out.println("Error: " + mutationResult.failureMessage);
                }
                System.out.println("=============================================\n");
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
        mutationRules.add(new TagMovementBetweenTemplatesRule());
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
}

