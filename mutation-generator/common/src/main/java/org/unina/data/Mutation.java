package org.unina.data;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;import java.util.List;public class Mutation {
    public String uuid;
    public String element;
    public String mutation_type;
    public String mutation_id;
    public String error_log;

    public List<MutatedFile> mutatedFiles = new ArrayList<>();

    public Mutation(String uuid, String element, String mutation_type, String mutation_id, String error_log) {
        this.uuid = uuid;
        this.element = element;
        this.mutation_type = mutation_type;
        this.mutation_id = mutation_id;
        this.error_log = error_log;
    }

    public void applyMutationToRepository() throws IOException {
        for (MutatedFile file : mutatedFiles) {
            Path filePath = Paths.get(file.filePath);
            file.originalCode = Files.readString(filePath, StandardCharsets.UTF_8);
            Files.writeString(filePath, file.mutatedCode, StandardCharsets.UTF_8);
        }
    }

    public void revertMutations() throws IOException {
        for (MutatedFile file : mutatedFiles) {
            Files.writeString(Paths.get(file.filePath), file.originalCode, StandardCharsets.UTF_8);
        }
    }
}
