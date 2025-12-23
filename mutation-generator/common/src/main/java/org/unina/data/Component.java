package org.unina.data;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;
import org.unina.util.ComponentIndexer;

import java.nio.file.Path;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Component {
    public final Path path;
    public final String selector;
    public final String htmlContent;

    public Component(Path path, String selector, String htmlContent) {
        this.path = path;
        this.selector = selector;
        this.htmlContent = htmlContent;
    }

    public Set<Component> getChildren(){
        Set<Component> children = new HashSet<>();

        Document doc = Jsoup.parse(htmlContent, Parser.xmlParser());
        Elements allElements = doc.getAllElements();

        for (Element element : allElements) {
            String tagName = element.tagName();

            if (tagName.contains("-")) {
                Component childComponent = ComponentIndexer.getInstance().getComponentBySelector(tagName);
                if (childComponent != null) {
                    children.add(childComponent);
                }
            }
        }
        return children;
    }

    public Set<Component> getParents() {
        if (this.selector == null) {
            return Collections.emptySet();
        }

        Set<Component> parents = new HashSet<>();

        for (Component candidateParent : ComponentIndexer.getInstance().getAllComponents()) {
            if (candidateParent.htmlContent == null || !candidateParent.htmlContent.contains(this.selector)) {
                continue;
            }
            Document doc = Jsoup.parse(candidateParent.htmlContent, Parser.xmlParser());
            if (!doc.select(this.selector).isEmpty()) {
                parents.add(candidateParent);
            }
        }

        return parents;
    }

}



















