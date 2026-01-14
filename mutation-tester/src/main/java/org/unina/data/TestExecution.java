package org.unina.data;

public class TestExecution {
    public String locatorName;
    public String mutatedTag; // alpha, beta, gamma, delta, epsilon
    public TestStatus status;
    public String errorMessage;

    public TestExecution(String locatorName, String mutatedTag) {
        this.locatorName = locatorName;
        this.mutatedTag = mutatedTag;
    }

    public boolean isPassed() {
        return this.status == TestStatus.PASSED;
    }

    public boolean isApplicable() {
        return this.status != TestStatus.NOT_APPLICABLE;
    }
}
