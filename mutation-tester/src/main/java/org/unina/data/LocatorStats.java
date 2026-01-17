package org.unina.data;

public class LocatorStats {
    public final String locatorName;
    public int successCount = 0;
    public int fragilityFailureCount = 0;
    public int obsolescenceFailureCount = 0;
    public int notTestedCount = 0;

    public LocatorStats(String locatorName) {
        this.locatorName = locatorName;
    }

    public int getTotalTests() {
        return successCount + fragilityFailureCount + obsolescenceFailureCount +  notTestedCount;
    }

    @Override
    public String toString() {
        return String.format("%s,%d,%d,%d,%d,%d",
                locatorName, getTotalTests(), successCount, fragilityFailureCount, obsolescenceFailureCount, notTestedCount);
    }
}
