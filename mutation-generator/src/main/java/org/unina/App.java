package org.unina;

import org.apache.commons.io.FileUtils;
import org.unina.core.MutationEngine;
import org.unina.data.Config;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class App {
    public static void main(String[] args) throws IOException {
        Path jsonPath = Paths.get("generator-config.json");
        if (args.length > 0 && args[0].startsWith("--config="))
            jsonPath = Paths.get(args[0].substring("--config=".length()));

        if (!jsonPath.toFile().exists()) {
            System.err.println("Configuration file not found. Make sure to provide a valid path or to create a config file in the same directory as the jar file.");
            return;
        }
        Config jsonConfig = Config.loadConfiguration(jsonPath);
        if (jsonConfig == null) throw new RuntimeException("Error initializing configuration object");

        FileUtils.copyDirectory(
            new File(jsonConfig.repositoryRootPath),
            new File("tmp/mutation-generator-output"));
        jsonConfig.outputDirectory = "tmp/mutation-generator-output";

        System.out.println("Configuration loaded. Running the mutation engine.\n");

        try {
            MutationEngine.Run(jsonConfig);
        } catch(IOException e) {
            System.err.println(e.getMessage());
        }
    }

}
