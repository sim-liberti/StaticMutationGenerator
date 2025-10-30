package org.unina.TagMatchers;

import org.jsoup.nodes.Element;

public interface TagMatcher {
    boolean matches(Element element);
}
