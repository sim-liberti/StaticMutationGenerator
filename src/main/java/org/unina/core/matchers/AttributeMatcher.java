package org.unina.core.matchers;

import org.jsoup.nodes.Element;
import org.unina.core.TagMatcher;

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
