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
import org.unina.util.RandomSelector;
import org.unina.core.MutationRule;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

public class TagMovementToAnyHtmlTreePointRule implements MutationRule {
    @Override
    public MutationResult ApplyMutation(Element targetElement) {
        Set<Component> candidateComponents = new HashSet<>();
        candidateComponents.addAll(findChildrenRecursive(ElementExtension.getComponent(targetElement)));
        candidateComponents.addAll(findParentsRecursive(ElementExtension.getComponent(targetElement)));
        if (candidateComponents.isEmpty()) {
            return new MutationResult(false, "No candidate components found", null);
        }

        Component randomComponent = RandomSelector.getInstance().GetRandomItemFromCollection(candidateComponents);

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
        Component randomParent = RandomSelector.getInstance().GetRandomItemFromCollection(component.getParents());
        if (randomParent == null) return result;
        result.add(randomParent);
        result.addAll(findParentsRecursive(randomParent));
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
