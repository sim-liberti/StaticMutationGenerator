package org.unina.core.rules;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.unina.core.MutationResult;
import org.unina.data.Component;
import org.unina.data.ElementExtension;
import org.unina.data.MutationRuleId;
import org.unina.data.MutationTagType;
import org.unina.util.ComponentIndexer;
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

    @Override
    public MutationResult ApplyMutation(Element targetElement) {
        Component randomComponent = RandomSelector.getInstance().GetRandomItemFromCollection(
                ComponentIndexer.getInstance().getAllComponents()
        );

        final Document destinationDocument;
        try{
            destinationDocument = Jsoup.parse(randomComponent.path.toFile(), "UTF-8");
        } catch (IOException e){
            return new MutationResult(false, "Error parsing the destination document: " + e.getMessage(), null);
        }

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
