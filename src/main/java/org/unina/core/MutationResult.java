package org.unina.core;

public class MutationResult {
    public boolean mutationApplied;
    public String failureMessage;

    public MutationResult(boolean mutationApplied, String failureMessage) {
        this.mutationApplied = mutationApplied;
        this.failureMessage = failureMessage;
    }
}
