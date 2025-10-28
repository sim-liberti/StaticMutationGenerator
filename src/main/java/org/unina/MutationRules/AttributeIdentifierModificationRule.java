package org.unina.MutationRules;

import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Element;
import org.unina.Data.MutationRuleId;
import org.unina.Data.ObjectType;
import org.unina.Utils.RandomSelector;

import java.util.List;

public class AttributeIdentifierModificationRule implements MutationRule {


    @Override
    public boolean ApplyMutation(Element targetElement) {
        Attribute attribute = targetElement.attribute("id");
        if (attribute == null)
            return false;

        targetElement.attr(attribute.getKey(), attribute.getValue() + "_mutated");

        return true;
    }

    @Override
    public ObjectType objectType() {
        return ObjectType.Attribute;
    }

    @Override
    public MutationRuleId mutationId() {
        return MutationRuleId.c;
    }
}
