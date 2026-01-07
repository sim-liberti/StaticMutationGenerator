package org.unina;

import org.unina.data.Config;
import org.unina.util.ComponentIndexer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class App {
    public static void main(String[] args) {
        Path jsonPath = Paths.get("generator-config.json");
        if (args.length > 0 && args[0].startsWith("--config="))
            jsonPath = Paths.get(args[0].substring("--config=".length()));

        if (!jsonPath.toFile().exists()) {
            System.err.println("Configuration file not found. Make sure to provide a valid path or to create a config file in the same directory as the jar file.");
            return;
        }
        Config jsonConfig = Config.loadConfiguration(jsonPath);
        if (jsonConfig == null) throw new RuntimeException("Error initializing configuration object");

        ComponentIndexer.initialize();
        try {
            ComponentIndexer.getInstance().buildSelectorMap(new File(jsonConfig.repositoryRootPath).toPath());
        } catch (IOException e) {
            System.err.println("Error initializing Component Indexer: " + e.getMessage());
        }

        try {
            Injector.injectHooks();
        } catch (IOException e) {
            System.err.println("Error initializing Component Indexer: " + e.getMessage());
        }
    }
}
