package org.unina;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.ParseSettings;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;
import org.unina.data.MutationConfig;
import org.unina.matchers.TagMatcherFactory;
import org.unina.core.*;
import org.unina.rules.*;
import org.unina.data.Config;
import org.unina.data.ElementExtension;
import org.unina.data.MutationDatabase;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class MutationEngine {
    private static final StringBuilder mutationCsv = new StringBuilder();
    private static final List<MutationRule> mutationRules = new ArrayList<>();
    private static final Map<String, Integer> targetElements = new HashMap<>();

    public static void Run(Config config) throws IOException {
        MutationDatabase db = new MutationDatabase();

        initializeRules();
        final Parser parser = Parser.htmlParser();
        parser.settings(new ParseSettings(true, true));

        mutationCsv.append("Nome,Elemento,Mutazione,Stato,Errore\n");
        for (MutationConfig mutation : config.mutations) {
            System.out.println("============= " + mutation.name + " =============");
            generateMutations(mutation, parser, db);
            System.out.println("================================");
        }
        saveCsv(mutationCsv, Paths.get("output/mutations/mutations.csv").toString());
    }

    public static void generateMutations(MutationConfig mutation, Parser parser, MutationDatabase db) throws IOException {
        Path path = Paths.get(mutation.filePath);
        final String docBaseUri = path.toAbsolutePath().toString();
        final Document document = Jsoup.parse(Files.readString(path), docBaseUri, parser);
        document.outputSettings().prettyPrint(false);
        final Element targetElement = findElement(mutation, document);

        for (MutationRule mutationRule : mutationRules) {
            initializeTargets(targetElement);
            for (Map.Entry<String, Integer> entry : targetElements.entrySet()) {
                System.out.println("Applying mutation " + mutationRule.mutationId() + " to tag " + entry.getKey());

                Document cloneDocument = document.clone();
                Element targetElementClone = cloneDocument.getAllElements().get(entry.getValue());

                String htmlBefore = cloneDocument.html();
                MutationResult mutationResult = mutationRule.ApplyMutation(targetElementClone);
                String htmlAfter = cloneDocument.html();

                if (mutationResult.mutationApplied && !htmlBefore.equals(htmlAfter)){
                    db.saveMutation(entry.getKey(), mutation.name, mutationRule.mutationName(), mutationRule.mutationId().name(), mutationResult.mutatedDocuments);
                }
                mutationCsv.append(String.format("%s,%s,%s,%s,%s\n",
                        mutation.name, entry.getKey(), mutationRule.mutationId(), (mutationResult.mutationApplied ? "OK" : "KO"), mutationResult.failureMessage));
                System.out.println("Mutation applied: " + mutationResult.mutationApplied);
                if (!mutationResult.mutationApplied){
                    System.out.println("Error: " + mutationResult.failureMessage);
                }
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

    private static Element findElement(MutationConfig mutation, Document document) {
        TagMatcher matcher = TagMatcherFactory.fromConfig(mutation.matcher);
        return document.getAllElements()
                .stream()
                .filter(matcher::matches)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No target tag found"));
    }

    private static void saveCsv(StringBuilder content, String path) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path, StandardCharsets.UTF_8))) {

            writer.write(content.toString());
            System.out.println("File saved: " + path);

        } catch (IOException e) {
            System.err.println("Error while saving " + path);
            e.printStackTrace();
        }
    }
}

