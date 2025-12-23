package org.unina.matchers;

import org.unina.core.TagMatcher;

import java.util.Map;

public class TagMatcherFactory {
    public static TagMatcher fromConfig(Map<String, String> config) {
        String type = config.get("type");
        String key = config.get("key");
        String value = config.get("value");

        return switch (type.toLowerCase()){
            case "class" -> new CssClassMatcher(value);
            case "id" -> new IdentifierMatcher(value);
            case "text" -> new TextContentMatcher(value);
            case "attribute" -> new AttributeMatcher(key, value);
            default -> throw new IllegalArgumentException("Unknown matcher type: " + type);
        };
    }
}
