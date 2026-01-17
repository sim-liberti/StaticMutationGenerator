package org.unina;

import org.unina.data.Config;
import org.unina.util.ComponentIndexer;
import org.unina.util.RandomSelector;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class App {
    public static void main(String[] args) {
        ObjectMapper mapper = new ObjectMapper();
        Config config = new Config();

        try {
            File jsonFile = new File("generator-config.json");
            config = mapper.readValue(jsonFile, Config.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Ensure the database file exists
        Path dbPath = Paths.get("mutations.db");
        try {
            if (!dbPath.toFile().exists()) {
                File templateDb = new File("mutations.db");
                if (!templateDb.createNewFile()) throw new IOException("Failed to create the database file.");
            }
        } catch (IOException e) {
            System.err.println("Error creating the database file: " + e.getMessage());
            return;
        }

        // Initialize utilities
        if (config.seed.isEmpty())
            RandomSelector.initialize();
        else
            RandomSelector.initialize(Integer.parseInt(config.seed));

        ComponentIndexer.initialize();
        try {
            ComponentIndexer.getInstance().buildSelectorMap(new File(config.repositoryRootPath).toPath());
        } catch (IOException e) {
            System.err.println("Error initializing Component Indexer: " + e.getMessage());
            return;
        }

        System.out.println("Configuration loaded. Running the mutation engine.\n");

        try {
            MutationEngine.Run(config);
        } catch(IOException e) {
            System.err.println(e.getMessage());
        }
    }

}
