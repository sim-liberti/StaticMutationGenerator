package org.unina.core.rules;

import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Element;
import org.unina.core.MutationResult;
import org.unina.data.MutationRuleId;
import org.unina.data.MutationTagType;
import org.unina.util.RandomSelector;
import org.unina.core.MutationRule;

import java.util.ArrayList;
import java.util.List;

public class AttributeValueModificationRule implements MutationRule {
    @Override
    public MutationResult ApplyMutation(Element targetElement) {
        List<Attribute> attributes = new ArrayList<>(targetElement.attributes().asList());

        if (attributes.isEmpty())
            return new MutationResult(false, "Target element has no attributes");

        attributes.removeIf(a -> a.getKey().equalsIgnoreCase("id") ||
                a.getKey().equalsIgnoreCase("class") ||
                a.getKey().startsWith("x-test-"));

        if (attributes.isEmpty())
            return new  MutationResult(false, "Target element has no attributes");

        Attribute randomAttribute = RandomSelector.GetInstance().GetRandomItemFromCollection(attributes);

        targetElement.attr(randomAttribute.getKey(),  randomAttribute.getValue() + "_mutated");

        return new MutationResult(true, "");
    }

    @Override
    public String mutationName() { return "attr_val_mod_mut"; }

    @Override
    public MutationTagType objectType() {
        return MutationTagType.Attribute;
    }

    @Override
    public MutationRuleId mutationId() {
        return MutationRuleId.a;
    }
}
