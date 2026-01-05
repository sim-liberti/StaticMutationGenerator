package org.unina.rules;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.ParseSettings;
import org.jsoup.parser.Parser;
import org.unina.core.MutationResult;
import org.unina.data.Component;
import org.unina.data.ElementExtension;
import org.unina.data.MutationRuleId;
import org.unina.data.MutationTagType;
import org.unina.util.ComponentIndexer;
import org.unina.util.RandomSelector;
import org.unina.core.MutationRule;

import java.io.IOException;
import java.util.List;

public class TagMovementBetweenTemplatesRule implements MutationRule {

    @Override
    public MutationResult ApplyMutation(Element targetElement) {
        final Parser parser = Parser.htmlParser();
        parser.settings(new ParseSettings(true, true));

        Component sourceComponent;
        try {
            sourceComponent = ElementExtension.getComponent(targetElement);
            if (sourceComponent == null) {
                return new MutationResult(false, "Target element does not belong to any component", null);
            }
        } catch(IOException e){
            return new MutationResult(false, "Error retrieving component information: " + e.getMessage(), null);
        }

        Component randomComponent = RandomSelector.getInstance().GetRandomItemFromCollection(
                ComponentIndexer.getInstance().getAllComponents()
        );

        final Document destinationDocument;
        destinationDocument = Jsoup.parse(randomComponent.htmlContent, randomComponent.path.toString(), parser);

        List<Document> mutatedDocuments = ElementExtension.moveToNewComponent(targetElement, destinationDocument);
        if (mutatedDocuments.isEmpty()) {
            return new MutationResult(false, "Destination document has no valid candidate elements", null);
        }

        return new MutationResult(true, "", mutatedDocuments);
    }

    @Override
    public String mutationName() { return "tag_mov_temp_mut"; }

    @Override
    public MutationTagType objectType() { return MutationTagType.Tag; }

    @Override
    public MutationRuleId mutationId() { return MutationRuleId.h; }
}
