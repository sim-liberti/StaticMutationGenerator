package org.unina.MutationRules;

import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Element;
import org.unina.Data.MutationRuleId;
import org.unina.Data.ObjectType;
import org.unina.Utils.RandomSelector;

import java.util.ArrayList;
import java.util.List;

public class AttributeRemovalRule implements MutationRule {
    @Override
    public boolean ApplyMutation(Element targetElement) {
        List<Attribute> attributes = new ArrayList<>(targetElement.attributes().asList());

        if (attributes.isEmpty())
            return false;

        attributes.removeIf(a -> a.getKey().equalsIgnoreCase("id") ||
                a.getKey().equalsIgnoreCase("class") ||
                a.getKey().startsWith("x-test-"));

        if (attributes.isEmpty())
            return false;

        Attribute randomAttribute = RandomSelector.GetInstance().GetRandomItemFromCollection(attributes);

        targetElement.attributes().remove(randomAttribute.getKey());

        return true;
    }

    @Override
    public ObjectType objectType() {
        return ObjectType.Attribute;
    }

    @Override
    public MutationRuleId mutationId() {
        return MutationRuleId.b;
    }
}