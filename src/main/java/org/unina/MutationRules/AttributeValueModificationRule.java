package org.unina.MutationRules;

import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Element;
import org.unina.Data.MutationRuleId;
import org.unina.Data.ObjectType;

import java.util.List;

public class AttributeValueModificationRule implements MutationRule {
    @Override
    public boolean ApplyMutation(Element targetElement) {
        List<Attribute> attributes = targetElement.attributes().asList();

        if (attributes.isEmpty())
            return false;

        for (Attribute attribute : attributes){
            String key = attribute.getKey();
            String value = attribute.getValue();

            if (!key.equalsIgnoreCase("id") && !key.equalsIgnoreCase("class") && !key.startsWith("x-text-"))
                targetElement.attr(key, value + "_mutated");
        }

        return false;
    }

    @Override
    public ObjectType objectType() {
        return ObjectType.Attribute;
    }

    @Override
    public MutationRuleId mutationId() {
        return MutationRuleId.a;
    }
}
