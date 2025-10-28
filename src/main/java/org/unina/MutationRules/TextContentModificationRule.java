package org.unina.MutationRules;

import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.unina.Data.MutationRuleId;
import org.unina.Data.ObjectType;
import org.unina.Utils.RandomSelector;

import java.util.List;

public class TextContentModificationRule  implements MutationRule {
    @Override
    public boolean ApplyMutation(Element targetElement) {
        List<Node> children = targetElement.childNodes();

        if (children.isEmpty()) {
            return false;
        }

        children.removeIf(node -> !(node instanceof TextNode));

        if (children.isEmpty()) {
            return false;
        }

        Node randomNode = RandomSelector.GetInstance().GetRandomItemFromCollection(children);
        String existingText = ((TextNode) randomNode).getWholeText();

        if (existingText.trim().isEmpty()) {
            return false;
        }

        ((TextNode) randomNode).text("[The following text has been mutated] " + existingText);

        return true;
    }


    @Override
    public ObjectType objectType() {
        return ObjectType.Text;
    }

    @Override
    public MutationRuleId mutationId() {
        return MutationRuleId.d;
    }
}
