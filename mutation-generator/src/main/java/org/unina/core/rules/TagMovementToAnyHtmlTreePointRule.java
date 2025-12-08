package org.unina.core.rules;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.unina.core.MutationResult;
import org.unina.data.Component;
import org.unina.data.ElementExtension;
import org.unina.data.MutationRuleId;
import org.unina.data.MutationTagType;
import org.unina.util.RandomSelector;
import org.unina.core.MutationRule;

import java.io.IOException;
import java.util.*;

public class TagMovementToAnyHtmlTreePointRule implements MutationRule {
    @Override
    public MutationResult ApplyMutation(Element targetElement) {
        Component targetComponent;
        try {
            targetComponent = ElementExtension.getComponent(targetElement);
            if (targetComponent == null) {
                return new MutationResult(false, "Target element does not belong to any component", null);
            }
        } catch(IOException e){
            return new MutationResult(false, "Error retrieving component information: " + e.getMessage(), null);
        }

        Set<Component> candidateComponents = new HashSet<>();
        Set<Component> children = findChildrenRecursive(targetComponent);
        Set<Component> parents = findParentsRecursive(targetComponent);
        candidateComponents.addAll(children);
        candidateComponents.addAll(parents);
        if (candidateComponents.isEmpty()) {
            return new MutationResult(false, "No candidate components found", null);
        }

        Component randomComponent = RandomSelector.getInstance().GetRandomItemFromCollection(candidateComponents);

        final Document destinationDocument;
        destinationDocument = Jsoup.parse(randomComponent.htmlContent, Parser.xmlParser());

        List<Document> mutatedDocuments = ElementExtension.moveToNewComponent(targetElement, destinationDocument);
        if (mutatedDocuments.isEmpty()) {
            return new MutationResult(false, "Destination document has no valid candidate elements", null);
        }

        return new MutationResult(true, "", mutatedDocuments);
    }


    private Set<Component> findChildrenRecursive(Component component){
        Set<Component> result = new HashSet<>();

        if (component == null) return result;
        Set<Component> children = component.getChildren();
        if (children.isEmpty()) return result;
        for (Component child : children) {
            result.add(child);
            result.addAll(findChildrenRecursive(child));
        }
        return result;
    }

    private Set<Component> findParentsRecursive(Component component){
        Set<Component> result = new HashSet<>();

        if (component == null) return result;
        Set<Component> parents = component.getParents();
        if (parents.isEmpty()) return result;
        for (Component parent : parents) {
            result.add(parent);
            result.addAll(findParentsRecursive(parent));
        }
        return result;
    }

    @Override
    public String mutationName() { return "tag_mov_html_mut"; }

    @Override
    public MutationTagType objectType() {
        return MutationTagType.Tag;
    }

    @Override
    public MutationRuleId mutationId() {
        return MutationRuleId.g;
    }
}
