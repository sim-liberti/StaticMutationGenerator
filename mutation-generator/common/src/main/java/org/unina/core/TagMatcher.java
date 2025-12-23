package org.unina.core;

import org.jsoup.nodes.Element;

public interface TagMatcher {
    boolean matches(Element element);
}
