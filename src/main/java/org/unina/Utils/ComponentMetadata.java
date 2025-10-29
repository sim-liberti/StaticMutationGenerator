package org.unina.Utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class ComponentMetadata {
    private String currentSelector;
    private Set<Path> children = new HashSet<>();
    private Set<Path> parents = new HashSet<>();
    private Set<Path> siblings = new HashSet<>();
    private final Path currentTsPath;
    private final Path currentHtmlPath;

    public ComponentMetadata(Path tsPath, Path htmlPath){
        this.currentTsPath = tsPath;
        this.currentHtmlPath = htmlPath;
    }

    public Set<Path> getChildren() {
        return Collections.unmodifiableSet(children);
    }
    public Set<Path> getParents() {
        return Collections.unmodifiableSet(parents);
    }
    public Set<Path> getSiblings() {
        return Collections.unmodifiableSet(siblings);
    }

    public void buildComponentMetadata() throws IOException {
        String htmlContent = Files.readString(this.currentHtmlPath);
        String tsContent = Files.readString(this.currentTsPath);

        // Matches with the angular selector in the .ts file (e.g., selector: 'app-component')
        Pattern selectorPattern = Pattern.compile("selector\\s*:\\s*['\"](.*?)['\"]");
        if (selectorPattern.matcher(tsContent).find()) {
            this.currentSelector = selectorPattern.matcher(tsContent).group(1);
        }

        this.children = extractChildComponents(htmlContent, this.currentTsPath.getParent());
        this.parents = extractParentComponents();
        this.siblings = extractSiblingComponents();
    }

    private Set<Path> extractChildComponents(String htmlContent, Path currentDir) throws IOException{
        Set<Path> children = new HashSet<>();
        Pattern componentTagPattern = Pattern.compile("<([a-z][a-z0-9]*(?:-[a-z0-9]+)+)");
        Matcher matcher = componentTagPattern.matcher(htmlContent);
        Set<String> componentTags = new HashSet<>();
        while (matcher.find()) {
            componentTags.add(matcher.group(1));
        }

        for (String tag : componentTags) {
            Path childComponent = FileBrowser.findComponentBySelector(tag, currentDir);
            if (childComponent != null) {
                children.add(childComponent);
            }
        }
        return children;
    }

    private Set<Path> extractParentComponents() throws IOException{
        if (this.currentSelector == null) {
            return Collections.emptySet();
        }

        Set<Path> parents = new HashSet<>();
        Path srcDir = FileBrowser.getSrcPathDirectory(this.currentTsPath.getParent());
        if (srcDir == null){
            return parents;
        }

        try (Stream<Path> stream = Files.walk(srcDir)){
            stream.filter(file -> file.toString().endsWith(".component.html"))
                .filter(file -> {
                    try{
                        String content = Files.readString(file);
                        return content.contains("<" + this.currentSelector);
                    } catch(IOException e){
                        return false;
                    }
                })
                .forEach(parents::add);
        }

        return parents;
    }

    private Set<Path> extractSiblingComponents() throws IOException {
        Set<Path> siblings = new HashSet<>();

        for (Path parent : this.parents){
            String parentHtml = Files.readString(parent);
            Set<Path> parentChildren = extractChildComponents(parentHtml, parent.getParent());
            siblings.addAll(parentChildren);
        }
        return siblings;
    }

}



















