package org.unina.core.matchers;

import org.jsoup.nodes.Element;
import org.unina.core.TagMatcher;

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
