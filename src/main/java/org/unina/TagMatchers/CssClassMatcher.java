package org.unina.TagMatchers;

import org.jsoup.nodes.Element;

public class CssClassMatcher implements TagMatcher {
    private final String className;

    public CssClassMatcher(String className) {
        this.className = className;
    }

    @Override
    public boolean matches(Element element) {
        return element.className().contains(className);
    }
}
