package org.unina.core;

import org.jsoup.nodes.Document;

import java.util.List;

public class MutationResult {
    public boolean mutationApplied;
    public String failureMessage;
    public List<Document> mutatedDocuments;

    public MutationResult(boolean mutationApplied, String failureMessage, List<Document> mutatedDocuments) {
        this.mutationApplied = mutationApplied;
        this.failureMessage = failureMessage;
        this.mutatedDocuments = mutatedDocuments;
    }
}
