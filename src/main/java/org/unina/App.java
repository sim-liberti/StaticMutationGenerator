package org.unina;

import org.unina.core.MutationEngine;
import org.unina.data.Config;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class App {
    public static void main(String[] args) {
        Path jsonPath = Paths.get("config.json");
        Config jsonConfig = Config.loadConfiguration(jsonPath);
        if (jsonConfig == null) throw new RuntimeException("Error initializing configuration object");

        try {
            MutationEngine.Run(jsonConfig);
        } catch(IOException e) {
            System.err.println(e.getMessage());
        }
    }

}
