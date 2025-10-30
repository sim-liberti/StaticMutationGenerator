package org.unina.TagMatchers;

import org.jsoup.nodes.Element;

public class AttributeMatcher implements TagMatcher {
    private final String attributeName;
    private final String attributeValue;

    public AttributeMatcher(String attributeName, String attributeValue) {
        this.attributeName = attributeName;
        this.attributeValue = attributeValue;
    }

    @Override
    public boolean matches(Element element) {
        return element.hasAttr(attributeName) &&
               element.attr(attributeName).contains(attributeValue);
    }
}
