package org.unina.data;

import java.util.ArrayList;
import java.util.List;

public class MutationBatch {
    public String batchId; // e.g.: mutation_a_attribute_beta
    private final List<TestExecution> executions = new ArrayList<>();

    public List<TestExecution> getExecutions() {
        return this.executions;
    }

    public void addExecution(TestExecution execution) {
        this.executions.add(execution);
    }

    public BatchStatus getBatchStatus() {
        long passedCount = executions.stream().filter(TestExecution::isPassed).count();

        if (passedCount == 0) {
            return BatchStatus.OBSOLETE;
        } else if (passedCount == executions.size()) {
            return BatchStatus.ROBUST;
        } else {
            return BatchStatus.FRAGILE;
        }
    }
}
