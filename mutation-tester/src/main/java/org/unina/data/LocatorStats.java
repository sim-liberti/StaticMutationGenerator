package org.unina.data;

public class LocatorStats {
    private final String locatorName;
    public int totalTests = 0;
    public int successCount = 0;
    public int fragilityFailureCount = 0;
    public int obsolescenceFailureCount = 0;

    public LocatorStats(String locatorName) {
        this.locatorName = locatorName;
    }

    @Override
    public String toString() {
        return String.format("%s | %d | %d | %d | %d",
                locatorName, totalTests, successCount, fragilityFailureCount, obsolescenceFailureCount);
    }
}
