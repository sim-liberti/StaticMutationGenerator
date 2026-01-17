package org.unina.data;

import java.util.ArrayList;
import java.util.List;

public class MutationBatch {
    public String batchName;
    public String batchId; // e.g.: mutation_a_attribute_beta
    private final List<TestExecution> executions = new ArrayList<>();

    public List<TestExecution> getExecutions() {
        return this.executions;
    }

    public void addExecution(TestExecution execution) {
        this.executions.add(execution);
    }

    public BatchStatus getBatchStatus() {
        List<TestExecution> appliedExecutions = executions.stream().filter(TestExecution::isApplicable).toList();
        long passedCount = appliedExecutions.stream().filter(TestExecution::isPassed).count();

        if (appliedExecutions.isEmpty()) {
            return BatchStatus.NOT_TESTED;
        } else if (passedCount == 0) {
            return BatchStatus.OBSOLETE;
        } else if (passedCount == appliedExecutions.size()) {
            return BatchStatus.ROBUST;
        } else {
            return BatchStatus.FRAGILE;
        }
    }
}
