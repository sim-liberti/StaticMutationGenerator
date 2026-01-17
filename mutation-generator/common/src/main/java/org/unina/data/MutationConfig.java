package org.unina.data;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class MutationConfig {
    public String name;

    @JsonProperty("file_path")
    public String filePath;

    @JsonProperty("target_matcher")
    public Map<String, String> matcher;
}
