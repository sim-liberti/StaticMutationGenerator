package org.unina.core;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.unina.core.matchers.TagMatcherFactory;
import org.unina.core.rules.*;
import org.unina.data.Config;
import org.unina.util.RandomSelector;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class MutationEngine {

    private static final List<MutationRule> mutationRules = new ArrayList<>();

    public static void Run(Config jsonConfig) throws IOException {
        initializeRules();

        if (jsonConfig.seed.isEmpty())
            RandomSelector.initialize();
        else
            RandomSelector.initialize(Integer.parseInt(jsonConfig.seed));

        Document document = Jsoup.parse(Paths.get(jsonConfig.inputFile).toFile(), "UTF-8");

        for (MutationRule mutation : mutationRules) {
            Document cloneDocument = document.clone();
            Element targetElement = findElement(jsonConfig, cloneDocument);

            String htmlBefore = cloneDocument.html();
            boolean elementWasMutated = mutation.ApplyMutation(targetElement);
            String htmlAfter = cloneDocument.html();

            System.out.println("=== Mutation Applied: " + mutation.mutationId() + " ===");
            System.out.println("Result: " + (elementWasMutated ? "Success" : "Failure"));
            if (elementWasMutated && !htmlBefore.equals(htmlAfter)){
                String fileName = String.format("%s_%s.html", mutation.mutationId().name(), mutation.mutationName());
                saveMutationToFile(cloneDocument, fileName, jsonConfig.outputDirectory);
            }
            System.out.println("===========================\n");
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

