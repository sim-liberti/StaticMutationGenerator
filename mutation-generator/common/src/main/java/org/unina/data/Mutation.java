package org.unina.data;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;import java.util.List;public class Mutation {
    public String uuid;
    public String element;
    public String mutation_type;
    public String mutation_id;
    public String error_log;

    public List<MutatedFile> mutatedFiles = new ArrayList<>();

    public Mutation(String uuid, String element, String mutation_type, String mutation_id) {
        this.uuid = uuid;
        this.element = element;
        this.mutation_type = mutation_type;
        this.mutation_id = mutation_id;
    }

    public void applyMutationToRepository() throws IOException {
        for (MutatedFile file : mutatedFiles) {
            Path filePath = Paths.get(file.filePath);
            Path tempFile = Files.createTempFile(filePath.getParent(), "java_edit_", ".tmp");
            try {

                file.originalCode = Files.readString(filePath, StandardCharsets.UTF_8);
                Files.writeString(tempFile, file.mutatedCode, StandardCharsets.UTF_8);
                Files.move(tempFile, filePath,
                        StandardCopyOption.REPLACE_EXISTING,
                        StandardCopyOption.ATOMIC_MOVE);
            } catch (IOException e) {
                Files.deleteIfExists(tempFile);
                throw e;
            }
        }
    }

    public void revertMutations() throws IOException {
        for (MutatedFile file : mutatedFiles) {
            Files.writeString(Paths.get(file.filePath), file.originalCode, StandardCharsets.UTF_8);
        }
    }
}
