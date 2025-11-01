package org.unina.core.rules;

import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.unina.core.MutationResult;
import org.unina.data.MutationRuleId;
import org.unina.data.MutationTagType;
import org.unina.util.RandomSelector;
import org.unina.core.MutationRule;

import java.util.ArrayList;
import java.util.List;

public class TextContentRemovalRule  implements MutationRule {
    @Override
    public MutationResult ApplyMutation(Element targetElement) {
        List<Node> children = new ArrayList<>(targetElement.childNodes());

        if (children.isEmpty()) {
            return new MutationResult(false, "Target element has no child nodes");
        }

        children.removeIf(node -> !(node instanceof TextNode));

        if (children.isEmpty()) {
            return new MutationResult(false, "Target element has no child text nodes");
        }

        Node randomNode = RandomSelector.GetInstance().GetRandomItemFromCollection(children);
        String existingText = ((TextNode) randomNode).getWholeText();

        if (existingText.trim().isEmpty()) {
            return new MutationResult(false, "Target element text content is empty");
        }

        ((TextNode) randomNode).text("");

        return new MutationResult(true, "");
    }

    @Override
    public String mutationName() { return "text_cont_rem_mut"; }

    @Override
    public MutationTagType objectType() {
        return MutationTagType.Text;
    }

    @Override
    public MutationRuleId mutationId() {
        return MutationRuleId.e;
    }
}
