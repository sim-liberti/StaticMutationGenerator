package org.unina.TagMatchers;

public class IdentifierMatcher implements TagMatcher {
    private final String identifier;

    public IdentifierMatcher(String identifier) {
        this.identifier = identifier;
    }

    @Override
    public boolean matches(org.jsoup.nodes.Element element) {
        return element.id().equals(identifier);
    }
}
