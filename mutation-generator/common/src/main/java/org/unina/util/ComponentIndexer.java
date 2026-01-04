package org.unina.util;

import org.unina.data.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ComponentIndexer {
    private static ComponentIndexer instance;
    private final List<Component> componentList = new ArrayList<>();
    private final Map<String, Component> selectorToComponentMap = new HashMap<>();

    public static void initialize() {
        if (instance == null) {
            instance = new ComponentIndexer();
        }
    }

    public static ComponentIndexer getInstance() {
        if (instance == null) {
            throw new IllegalStateException("ComponentIndexer has not been initialized");
        }
        return instance;
    }

    public Component getComponentBySelector(String selector) {
        return selectorToComponentMap.get(selector);
    }

    public List<Component> getAllComponents() {
        return componentList;
    }

    public List<Component> getAllHtmlComponents() {
        return componentList.stream()
                .filter(comp -> comp.path.endsWith(".html")).collect(Collectors.toList());
    }

    public void buildSelectorMap(Path rootDir) throws IOException {
        try (Stream<Path> stream = Files.walk(rootDir)) {
            stream.filter(Files::isRegularFile)
                    .filter(file -> file.toString().endsWith(".component.ts"))
                    .forEach(this::processComponentFile);
        }
    }

    private void processComponentFile(Path file) {
        try {
            String content = Files.readString(file);
            // Regex to find the selector in the component decorator
            Pattern p = Pattern.compile("selector:\\s*['\"]([^'\"]+)['\"]");
            Matcher m = p.matcher(content);

            if (m.find()) {
                String selector = m.group(1);
                String htmlContent = "";

                String htmlFileName = file.getFileName().toString().replace(".component.ts", ".component.html");
                Path htmlFile = file.resolveSibling(htmlFileName);

                if (Files.exists(htmlFile)) {
                    htmlContent = Files.readString(htmlFile);
                }
                Component component = new Component(htmlFile, selector, htmlContent);
                componentList.add(component);
                selectorToComponentMap.put(selector, component);
            }
        } catch (IOException e) {
            System.err.println("Cannot read file: " + file);
        }
    }
}
