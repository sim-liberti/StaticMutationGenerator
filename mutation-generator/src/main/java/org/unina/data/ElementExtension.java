package org.unina.data;

import org.jsoup.nodes.Element;
import java.util.regex.Pattern;

public class ElementExtension {

    public static Element getSibling(Element element){
        return element.nextElementSibling() != null
             ? element.nextElementSibling()
             : element.previousElementSibling();
    }

    public static Element getAncestor(Element element){
        if (element.parent() == null) return null;
        Element candidate = element.parent().parent();
        if (candidate == null) return null;
        String name = candidate.tagName();
        if (!("html".equalsIgnoreCase(name) || "body".equalsIgnoreCase(name) || "head".equalsIgnoreCase(name) || "#root".equalsIgnoreCase(name))) {
            return candidate;
        }
        return null;
    }

    public static Element getContainingComponent(Element element){
        Pattern componentTagPattern = Pattern.compile("<([a-z][a-z0-9]*(?:-[a-z0-9]+)+)");
        Element temp = element.parent();
        while (temp != null && !componentTagPattern.matcher(temp.tagName()).find()) {
            temp = temp.parent();
        }
        return temp;
    }
}
