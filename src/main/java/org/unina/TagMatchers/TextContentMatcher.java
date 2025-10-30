package org.unina.TagMatchers;

public class TextContentMatcher implements TagMatcher {
    private final String textContent;

    public TextContentMatcher(String textContent) {
        this.textContent = textContent;
    }

    @Override
    public boolean matches(org.jsoup.nodes.Element element) {
        return element.text().contains(textContent);
    }
}
