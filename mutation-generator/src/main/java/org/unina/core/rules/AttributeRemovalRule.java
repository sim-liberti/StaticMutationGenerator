package org.unina.core.rules;

import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.unina.core.MutationResult;
import org.unina.data.MutationRuleId;
import org.unina.data.MutationTagType;
import org.unina.util.RandomSelector;
import org.unina.core.MutationRule;

import java.util.ArrayList;
import java.util.List;

public class AttributeRemovalRule implements MutationRule {
    @Override
    public MutationResult ApplyMutation(Element targetElement) {
        List<Attribute> attributes = new ArrayList<>(targetElement.attributes().asList());

        if (attributes.isEmpty())
            return new MutationResult(false, "Target element has no attributes", null);

        attributes.removeIf(a -> a.getKey().equalsIgnoreCase("id") ||
                a.getKey().equalsIgnoreCase("class") ||
                a.getKey().startsWith("x-test-"));

        if (attributes.isEmpty())
            return new MutationResult(false, "Target element has no valid candidate attributes", null);

        Attribute randomAttribute = RandomSelector.getInstance().GetRandomItemFromCollection(attributes);

        targetElement.attributes().remove(randomAttribute.getKey());

        List<Document> mutatedDocuments = new ArrayList<>();
        mutatedDocuments.add(targetElement.ownerDocument());

        return new MutationResult(true, "", mutatedDocuments);
    }

    @Override
    public String mutationName() { return "attr_rem_mut"; }

    @Override
    public MutationTagType objectType() {
        return MutationTagType.Attribute;
    }

    @Override
    public MutationRuleId mutationId() {
        return MutationRuleId.b;
    }
}