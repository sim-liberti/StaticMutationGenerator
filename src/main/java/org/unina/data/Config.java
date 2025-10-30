package org.unina.data;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Objects;

public class Config {
    public String seed;
    public String inputFile;
    public String outputDirectory;
    public Map<String, String> matcher;

    public static Config loadConfiguration(Path jsonPath) {
        try {
            String jsonContent = Files.readString(jsonPath);
            ObjectMapper mapper = new ObjectMapper();
            Config config = mapper.readValue(jsonContent, new TypeReference<>() {});
            String validationMessage = validate(config);
            if (!validationMessage.isEmpty())
                throw new Exception(validationMessage);
            return config;
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    private static String validate(Config config) {
        if (config.seed.isEmpty()){
            try {
                Integer.parseInt(config.seed);
            } catch (NumberFormatException e) {
                return "Seed must be an integer value";
            }
        }
        if (config.inputFile.isEmpty()) return "Must provide an input file";
        if (!Files.exists(Paths.get(config.inputFile))) return "Input file not found";
        if (config.outputDirectory.isEmpty()) return "Must provide an output directory";
        if (!Files.exists(Paths.get(config.outputDirectory))) return "Output directory not found";

        return "";
    }
}
