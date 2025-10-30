package org.unina.cli;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class ConfigLoader {

    public void load() throws IOException {
        Path jsonPath = null;
        String jsonContent = Files.readString(jsonPath);
        ObjectMapper mapper = new ObjectMapper();

        Map<String, String> map = mapper.readValue(jsonContent, new TypeReference<Map<String, String>>() {});
    }

}
