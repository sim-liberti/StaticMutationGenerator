package org.unina;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

public class Repository {
    public String sourcePath;
    public String copyPath;

    public Repository(String path) {
        this.sourcePath = path;
        this.copyPath = path + "-copy";
    }

    public void createCopy() throws IOException {
        try (Stream<Path> stream = Files.walk(Paths.get(sourcePath))) {
                stream.forEach(source -> {
                Path destination = Paths.get(copyPath, source.toString().substring(sourcePath.length()));
                try {
                    Files.copy(source, destination);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    public Set<Path> getSourceFiles() throws IOException {
        Set<Path> sourceFiles = new HashSet<>();
        try (Stream<Path> stream = Files.walk(Paths.get(copyPath))){
            stream.filter(file -> file.toString().endsWith(".component.html"))
                    .forEach(sourceFiles::add);
        }
        return sourceFiles;
    }
}
