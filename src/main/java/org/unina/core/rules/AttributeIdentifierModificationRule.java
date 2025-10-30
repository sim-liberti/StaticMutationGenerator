package org.unina.core.rules;

import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Element;
import org.unina.data.MutationRuleId;
import org.unina.data.MutationTagType;
import org.unina.core.MutationRule;

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
    public String mutationName() { return "attr_id_mut"; }

    @Override
    public MutationTagType objectType() { return MutationTagType.Attribute; }

    @Override
    public MutationRuleId mutationId() {
        return MutationRuleId.c;
    }
}
